package com.billflx.csgo.page

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.sqlite.SQLiteConstraintException
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billflx.csgo.bean.DownloadStatus
import com.billflx.csgo.bean.GameResItemBean
import com.billflx.csgo.bean.MDownloadItemBean
import com.billflx.csgo.bean.MDownloadStatusBean
import com.billflx.csgo.data.db.DownloadInfo
import com.billflx.csgo.data.db.DownloadInfoDao
import com.billflx.csgo.data.db.DownloadInfoDatabase
import com.gtastart.common.util.Coroutines
import com.gtastart.common.util.MDownload
import com.gtastart.common.util.MDownloadService
import com.gtastart.common.util.MToast
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadManagerViewModel @Inject constructor(
    private val downloadInfoDao: DownloadInfoDao
) : ViewModel() {

    companion object {
        private const val TAG = "DownloadManagerVM"
    }

    var mDownloadBroadcast: MDownloadBroadcast // 下载广播
    lateinit var mBinder: MDownloadService.LocalBinder
    var isBound: Boolean = false
    var downloadService: MDownloadService? = null

    var downloadList = mutableStateListOf<MDownloadItemBean>()
    var downloadedList = mutableStateListOf<MDownloadItemBean>()


    // 服务链接
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mBinder = service as MDownloadService.LocalBinder
            downloadService = mBinder.getService()
            Log.d(TAG, "onServiceConnected: 服务已连接！")
            isBound = true
            viewModelScope.launch {
                loadDownloadingListDB() // 加载数据库的下载中列表
                loadDownloadedListDB() // 加载已完成列表
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected: 服务已断开！")
            downloadService = null
            isBound = false
        }
    }

    init {
        mDownloadBroadcast = MDownloadBroadcast() // 初始化下载广播
    }

    class MDownloadBroadcast : BroadcastReceiver() {
        init {
            Log.d(TAG, "MDownloadBroadcast: 初始化下载广播")
        }
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive: 广播接收")
        }

    }

    fun startDownloadService(context: Context) {
/*        if (!isServiceRunning(context, MDownloadService::class.java)) {

        }*/
        val intent = Intent(context, MDownloadService::class.java)
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    suspend fun addDownloadInfoDB(downloadInfo: DownloadInfo): Result<Unit> {
        return try {
            downloadInfoDao.addInfo(downloadInfo)
            Result.success(Unit)
        } catch (e: SQLiteConstraintException) {
            Log.d(TAG, "addDownloadInfoDB: $e")
            Result.failure(e)
        }
    }

    suspend fun updateDownloadInfoDB(downloadInfo: DownloadInfo): Result<Unit> {
        return try {
            downloadInfoDao.updateInfo(downloadInfo)
            Result.success(Unit)
        } catch (e: SQLiteConstraintException) {
            Log.d(TAG, "updateDownloadInfoDB: $e")
            Result.failure(e)
        }
    }

    suspend fun getDownloadInfoDB(url: String): Result<DownloadInfo> {
        return try {
            val info = downloadInfoDao.getInfoByUrl(url = url)
            Result.success(info)
        } catch (e: SQLiteConstraintException) {
            Log.d(TAG, "updateDownloadInfoDB: $e")
            Result.failure(e)
        }
    }

    suspend fun loadDownloadedListDB() {
        downloadedList.clear() // 先清除
        val downloadedInfos: List<DownloadInfo> = downloadInfoDao.getDownloadedInfos()
        downloadedInfos.forEach { item ->
            val parentPath = item.parentPath
            val fileName = item.fileName
            val url = item.url
            val mDownload = MDownload(
                url = url,
                parentPath = parentPath,
                fileName = fileName
            )
            // 只需要一些必要信息
            downloadedList.add(MDownloadItemBean(
                mDownload = mDownload
            ))
        }

    }

    fun setupListener(downloadStatusData: MDownloadStatusBean): MDownload.MDownloadListener {
        val listener = object : MDownload.MDownloadListener {
            override fun onStart(task: DownloadTask) {
                Log.d("", "taskStart: 任务开始")
                downloadStatusData.downloadStatus.value = DownloadStatus.Started
            }

            override fun onConnected(
                task: DownloadTask,
                blockCount: Int,
                currentOffset: Long,
                totalLength: Long
            ) {
                // 在这获取文件名
                viewModelScope.launch {
                    val info = getDownloadInfoDB(task.url)
                    info.onSuccess {
                        updateDownloadInfoDB(it.copy(fileName = task.filename.orEmpty()))
                    }
                }
            }

            override fun onProgress(
                task: DownloadTask,
                currentOffset: Long,
                totalLength: Long
            ) {
                viewModelScope.launch {
                    val info = getDownloadInfoDB(task.url)
                    info.onSuccess {
                        updateDownloadInfoDB(it.copy(fileName = task.filename.orEmpty(), downloadedBytes = currentOffset, totalBytes = totalLength))
                    }
                }
                downloadStatusData.downloadStatus.value = DownloadStatus.Downloading
                downloadStatusData.downloadProgressStr.value =
                    MDownload.getProgressDisplayLine(currentOffset, totalLength)
            }

            override fun onStop(
                task: DownloadTask,
                cause: EndCause,
                realCause: Exception?
            ) {
                Log.d("", "taskEnd: 任务结束")
                if (cause == EndCause.COMPLETED) {
                    viewModelScope.launch {
                        val info = getDownloadInfoDB(task.url)
                        info.onSuccess {
                            updateDownloadInfoDB(it.copy(isFinished = true))
                        }
                        val downloadData = downloadList.find { it.mDownload?.url == task.url }
                        downloadData?.let { downloadList.remove(it) } // 移除列表
                    }
                    downloadStatusData.downloadStatus.value = DownloadStatus.Finished
                } else if (cause == EndCause.CANCELED) {
                    downloadStatusData.downloadStatus.value = DownloadStatus.PAUSE
                } else {
                    downloadStatusData.downloadStatus.value = DownloadStatus.ERROR
                }
            }

            override fun onRetry(task: DownloadTask, cause: ResumeFailedCause) {
                Log.d("", "retry: 任务重试")
                downloadStatusData.downloadStatus.value = DownloadStatus.ERROR
            }
        }
        return listener
    }

    suspend fun loadDownloadingListDB() {
        val downloadingInfos: List<DownloadInfo> = downloadInfoDao.getDownloadingInfos()
        // 先列出来，等会处理当前列表已经在下载(已经有下载实例)的item
        downloadingInfos.forEach { item ->
            val url = item.url
            val parentPath = item.parentPath
            val fileName: String? = if (!item.fileName.isEmpty()) item.fileName else null
            val downloadStatusData = MDownloadStatusBean(
                downloadStatus = mutableStateOf(DownloadStatus.PAUSE),
                currentOffset = item.downloadedBytes,
                totalLength = item.totalBytes,
                downloadProgressStr = mutableStateOf(MDownload.getProgressDisplayLine(item.downloadedBytes, item.totalBytes))
            )

            val listener = setupListener(downloadStatusData)
            val mDownload = mBinder.getService().addDownloadTask(
                url = url,
                parentPath = parentPath,
                fileName = fileName,
                listener = listener
            )

            val downloadData = MDownloadItemBean(
                mDownload = mDownload,
                gameResData = GameResItemBean(), // 这个byd已经没有任何用处了，有空直接干掉
                downloadStatusData = downloadStatusData
            )
            downloadList.add(downloadData)
        }
    }

    suspend fun addDownload(
        url: String,
        parentPath: String,
        fileName: String? = null,
        startNow: Boolean = true,
    ) {
        val downloadStatusData = MDownloadStatusBean()

        val listener = setupListener(downloadStatusData)
        val mDownload = mBinder.getService().addDownloadTask(
            url = url,
            parentPath = parentPath,
            fileName = fileName,
            listener = listener
        )

        val downloadInfo = DownloadInfo(
            fileName = fileName.orEmpty(), // 一般为空，需要okDownload获取的时候，再更新
            parentPath = parentPath,
            url = url,
            downloadedBytes = 0,
            totalBytes = 0,
            isFinished = false,
        )

        val result = addDownloadInfoDB(downloadInfo) // 写入数据库
        result.onFailure {
            Log.d(TAG, "addDownload: 无法添加下载任务，任务已存在")
            return
        }

        val downloadData = MDownloadItemBean(
            mDownload = mDownload,
            gameResData = GameResItemBean(),
            downloadStatusData = downloadStatusData
        )

        downloadList.add(downloadData)

        // 用单个任务启动下载试试
        if (startNow) {
            mDownload.start()
        }
        /* viewModel.mBinder.getService().startQueueDownload(
             listener = listener
         )*/
    }

    override fun onCleared() {
        super.onCleared()
        
    }

}
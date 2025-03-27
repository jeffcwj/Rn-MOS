package com.billflx.csgo.page

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.billflx.csgo.bean.DataType
import com.billflx.csgo.bean.DownloadStatus
import com.billflx.csgo.bean.DownloadExtraInfoBean
import com.billflx.csgo.bean.MDownloadItemBean
import com.billflx.csgo.bean.MDownloadStatusBean
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.db.CSVersionInfo
import com.billflx.csgo.data.db.DownloadInfo
import com.billflx.csgo.data.db.DownloadInfoDao
import com.billflx.csgo.nav.LocalSettingViewModel
import com.billflx.csgo.page.settings.game.GameSettingViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.M7ZipService
import com.gtastart.common.util.MDialog
import com.gtastart.common.util.MDownload
import com.gtastart.common.util.MDownloadService
import com.gtastart.common.util.MOSDialog
import com.gtastart.common.util.MToast
import com.gtastart.common.util.ZipUtils
import com.gtastart.common.util.compose.widget.CircleProgressPlaceHolder
import com.gtastart.common.util.compose.widget.MButton
import com.gtastart.common.util.extend.getNextFolderSuffix
import com.gtastart.common.util.isBlank
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.valvesoftware.source.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.nillerusr.DirchActivity
import me.nillerusr.LauncherActivity
import java.io.File
import javax.inject.Inject

// @SuppressLint("MissingPermission") // 通知权限直接忽略
@HiltViewModel
class DownloadManagerViewModel @Inject constructor(
    private val downloadInfoDao: DownloadInfoDao,
    private val app: Application
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
            viewModelScope.launch {
                loadDownloadingListDB() // 加载数据库的下载中列表
                loadDownloadedListDB() // 加载已完成列表
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected: 服务已断开！")
            downloadService = null
        }
    }

    private var currentUnZipStatus: MutableState<DownloadStatus>? = null
    private var currentUnZipProgress: MutableState<String>? = null
    private var currentUnzippedCount by mutableIntStateOf(0)
    private var currentTotalZipCount by mutableIntStateOf(0)
    private val isShowCloseButton = mutableStateOf(false)

    fun clearUnZipStatus() {
        //currentTotalZipCount = 0
        currentUnzippedCount = 0
        currentUnZipStatus?.value = DownloadStatus.IDLE
        currentUnZipProgress?.value = ""
        isShowCloseButton.value = false
    }

    private val unZipConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            (binder as M7ZipService.LocalBinder).let {
                it.setListener(object : ZipUtils.Companion.NameProgressListener {
                    override fun onStart() {
                        viewModelScope.launch {
                            clearUnZipStatus()
                        }
                    }

                    override fun onGetFileNum(num: Int) {
                        viewModelScope.launch(Dispatchers.IO) {
                            currentTotalZipCount = num
                            Log.d(TAG, "onGetFileNum: $num")
                        }
                    }

                    override fun onProgressUpdate(name: String, size: Long) {
                        viewModelScope.launch {
                            // 更新 UI，比如进度条
                            currentUnzippedCount++
                            currentUnZipProgress?.value = "正在解压...(${currentUnzippedCount}/${currentTotalZipCount})\n$name"
                            Log.d("解压", "正在解压: $name, 大小: $size")
                        }
                    }

                    override fun onError(errorMessage: String) {
                        viewModelScope.launch {
                            currentUnZipProgress?.value = "解压失败: $errorMessage"
                            currentUnZipStatus?.value = DownloadStatus.Finished
                            app.MToast("解压失败: $errorMessage")
                            isShowCloseButton.value = true
                        }
                    }

                    override fun onCompleted() {
                        viewModelScope.launch {
                            currentUnZipProgress?.value = "解压完成"
                            currentUnZipStatus?.value = DownloadStatus.Finished
                            app.MToast("解压完成")
                            isShowCloseButton.value = true
                        }
                    }
                })
                it
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
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
        val intent = Intent(context, MDownloadService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    suspend fun addDownloadInfoDB(downloadInfo: DownloadInfo): Result<Unit> {
        return try {
            downloadInfoDao.addInfo(downloadInfo)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d(TAG, "addDownloadInfoDB: $e")
            Result.failure(e)
        }
    }

    suspend fun updateDownloadInfoDB(downloadInfo: DownloadInfo): Result<Unit> {
        return try {
            downloadInfoDao.updateInfo(downloadInfo)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d(TAG, "updateDownloadInfoDB: $e")
            Result.failure(e)
        }
    }

    suspend fun getDownloadInfoDB(url: String): Result<DownloadInfo?> {
        return try {
            val info = downloadInfoDao.getInfoByUrl(url = url)
            Result.success(info)
        } catch (e: Exception) {
            Log.d(TAG, "updateDownloadInfoDB: $e")
            Result.failure(e)
        }
    }

    suspend fun removeDownloadInfoDB(url: String): Result<Unit> {
        return try {
            val info = downloadInfoDao.deleteInfoByUrl(url)
            loadDownloadedListDB()
            Result.success(info)
        } catch (e: Exception) {
            Log.d(TAG, "removeDownloadInfoDB: $e")
            Result.failure(e)
        }
    }


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    suspend fun removeDownloadingItem(item: MDownloadItemBean) {
        val url = item.mDownload?.url
        val status = item.downloadStatusData?.downloadStatus?.value
        if (status == DownloadStatus.Downloading || status == DownloadStatus.Started) {
            // item.mDownload?.stop() // 停止下载
            downloadService?.stopSingleTask(item.mDownload)
            Log.d(TAG, "removeDownloadingItem: 停止下载")
        }
        item.mDownload?.getDownloadTask()?.file?.delete() // 删除未完成的文件
        url?.let { theUrl ->
            val result = removeDownloadInfoDB(theUrl)
            result.onSuccess {
                val item = downloadList.find { it.mDownload?.url == theUrl }
                item?.let {
                    downloadList.remove(it)
                }
            }
        }
    }

    /**
     * 从数据库加载下载完成列表
     */
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
                mDownload = mDownload,
                gameResData = DownloadExtraInfoBean(
                    dataType = item.dataType // 文件的类型
                ),
                downloadStatusData = MDownloadStatusBean() // 无论如何也初始化一下
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
                        it?.let {
                            updateDownloadInfoDB(it.copy(fileName = task.filename.orEmpty()))
                        }
                    }
                }
            }

            override fun onProgress(
                task: DownloadTask,
                currentOffset: Long,
                totalLength: Long
            ) {
                downloadStatusData.retryCount.value = 0 // 正常下载，重试次数清零
                viewModelScope.launch {
                    val info = getDownloadInfoDB(task.url)
                    info.onSuccess {
                        it?.let {
                            updateDownloadInfoDB(it.copy(fileName = task.filename.orEmpty(), downloadedBytes = currentOffset, totalBytes = totalLength))
                        }
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
                            it?.let {
                                updateDownloadInfoDB(it.copy(isFinished = true))
                            }
                        }
                        val downloadData = downloadList.find { it.mDownload?.url == task.url }
                        downloadData?.let { downloadList.remove(it) } // 移除列表
                    }
                    downloadStatusData.downloadStatus.value = DownloadStatus.Finished
                } else if (cause == EndCause.CANCELED) {
                    downloadStatusData.downloadStatus.value = DownloadStatus.PAUSE
                } else {
                    // 重试最多三次
                    if (downloadStatusData.retryCount.value < 3) {
                        Log.d(TAG, "retrying: ${downloadStatusData.retryCount.value}次")
                        task.enqueue(task.listener)
                        downloadStatusData.retryCount.value++
                    } else {
                        downloadStatusData.downloadStatus.value = DownloadStatus.ERROR // 出现错误
                    }
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
                gameResData = DownloadExtraInfoBean(), // 这个byd已经没有任何用处了，有空直接干掉
                downloadStatusData = downloadStatusData
            )
            downloadList.add(downloadData)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    suspend fun addDownload(
        url: String,
        parentPath: String,
        fileName: String? = null,
        dataType: String,
        startNow: Boolean = true,
    ): MDownloadItemBean? {
        // 修复路径问题
        val parentFile = File(parentPath)
        if (!parentFile.isDirectory) {
            parentFile.delete()
        }
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }

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
            dataType = dataType
        )

        val result = addDownloadInfoDB(downloadInfo) // 写入数据库
        result.onFailure {
            Log.d(TAG, "addDownload: 无法添加下载任务，任务已存在")
            val targetDownloadData = downloadList.find { it.mDownload?.url == url }
            if (targetDownloadData != null) {
                if (targetDownloadData.downloadStatusData?.downloadStatus?.value != DownloadStatus.Downloading) { // 没处于下载状态
                    //targetDownloadData.mDownload?.start() // 直接启动下载已存在的任务
                    downloadService?.startSingleTask(targetDownloadData.mDownload)
                }
                return targetDownloadData
            }
            return null
        }

        val downloadData = MDownloadItemBean(
            mDownload = mDownload,
            gameResData = DownloadExtraInfoBean(),
            downloadStatusData = downloadStatusData
        )

        downloadList.add(downloadData)

        // 用单个任务启动下载试试
        if (startNow) {
            // mDownload.start()
            downloadService?.startSingleTask(mDownload)
        }
        /* viewModel.mBinder.getService().startQueueDownload(
             listener = listener
         )*/
        return downloadData
    }

    private fun unZipDialog(context: Context, item: MDownloadItemBean?, isShowCloseButton: MutableState<Boolean>): AlertDialog {
        val dialog = MDialog.show(
            cancelable = false,
            context = context,
            title = context.getString(R.string.unzip),
            customView = { dialog ->
                val modifier: Modifier = Modifier
                GtaStartTheme {
                    Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                        Column(
                            modifier = modifier.padding(GtaStartTheme.spacing.medium),
                            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
                        ) {
                            Text(
                                stringResource(R.string.tip_not_support_background_unzip),
                                style = MaterialTheme.typography.bodySmall)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isShowCloseButton.value) {
                                    Text("✅")
                                } else {
                                    CircleProgressPlaceHolder()
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
                                ) {
                                    Text(
                                        text = item?.downloadStatusData?.downloadProgressStr?.value?: stringResource(
                                            R.string.ready_to_unzip
                                        ),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            if (isShowCloseButton.value) {
                                TextButton (onClick = {
                                    dialog.dismiss()
                                }) {
                                    Text("关闭")
                                }
                            }
                        }
                    }
                }
            }
        )
        return dialog
    }

    private fun unZipResource(context: Context, item: MDownloadItemBean?, gamePath: String) {
        val fileName = item?.mDownload?.getDownloadTask()?.file?.name
        val filePath = item?.mDownload?.getDownloadTask()?.file?.absolutePath
        val file = item?.mDownload?.getDownloadTask()?.file
        fileName?.let {
            if (fileName.endsWith(".7z")) {
                Log.d(TAG, "dealWithFileOperation: 文件格式.7z")
//                val pathTo = ModLocalDataSource.getGamePath()
                val pathTo = gamePath
                MDialog.show(
                    cancelable = true,
                    context = context,
                    title = context.getString(R.string.unzip_method),
                    positiveButtonText = "快速(新)",
                    onPositiveButtonClick = { _,_ ->
                        val dialog = unZipDialog(context, item, isShowCloseButton) // 解压弹窗
                        viewModelScope.launch(Dispatchers.IO) {
                            launch(Dispatchers.Main) {
                                clearUnZipStatus()
                            }
                            if (file!!.length() > 1024L * 1024L * 1024L) { // 大于1G提示大文件
                                item.downloadStatusData?.downloadProgressStr?.value = context.getString(R.string.tip_unziping_big_file)
                            } else {
                                item.downloadStatusData?.downloadProgressStr?.value = context.getString(R.string.tip_unzipping)
                            }
                            item.downloadStatusData?.downloadStatus?.value = DownloadStatus.Downloading
                            /*val result = ZipUtils.nativeUnZip(
                                pathFrom = filePath.orEmpty(),
                                pathTo = pathTo,
                            )*/

                            currentUnZipProgress = item.downloadStatusData?.downloadProgressStr
                            currentUnZipStatus = item.downloadStatusData?.downloadStatus
                            M7ZipService.startExtractService(
                                context,
                                from = filePath.orEmpty(),
                                to = pathTo,
                                unZipConnection
                            )
                            /*if (result != 0) {
                                item.downloadStatusData?.downloadProgressStr?.value = context.getString(R.string.error_happened)
                                item.downloadStatusData?.downloadStatus?.value = DownloadStatus.Finished
                            } else {
                                item.downloadStatusData?.downloadProgressStr?.value = context.getString(R.string.finish_installing)
                                item.downloadStatusData?.downloadStatus?.value = DownloadStatus.Finished
                            }*/
                        }
                    },
                )
            } else if (fileName.endsWith(".zip")) {
                Log.d(TAG, "dealWithFileOperation: 文件格式.zip")
                unZipDialog(context, item, isShowCloseButton) // 解压弹窗
                viewModelScope.launch(Dispatchers.IO) {
                    val pathTo = ModLocalDataSource.getGamePath()
                    ZipUtils.unZip(
                        pathFrom = filePath.orEmpty(),
                        pathTo = pathTo,
                        object : ZipUtils.Companion.ProgressListener {
                            override fun onProgressUpdate(percent: Int) {
//                                Log.d(TAG, "onProgressUpdate: $percent")
                                isShowCloseButton.value = false
                                item.downloadStatusData?.downloadProgressStr?.value = "$percent %"
                                item.downloadStatusData?.downloadStatus?.value = DownloadStatus.Downloading
                            }

                            override fun onCompleted() {
                                isShowCloseButton.value = true
                                item.downloadStatusData?.downloadProgressStr?.value = context.getString(R.string.install_finished)
                                item.downloadStatusData?.downloadStatus?.value = DownloadStatus.Finished
                            }

                            override fun onError(error: String) {
                                isShowCloseButton.value = true
                                item.downloadStatusData?.downloadProgressStr?.value = context.getString(R.string.unzip_failed)
                                item.downloadStatusData?.downloadStatus?.value = DownloadStatus.ERROR
                            }

                        }
                    )
                }
            } else { // 压缩包没有合适的方法解压，目前支持zip和7z
                context.MToast(context.getString(R.string.tip_cannot_recognize_zip_type))
            }
        } ?: also {
            context.MToast(context.getString(R.string.tip_file_type_get_failed))
        }
    }

    /**
     * 处理文件操作
     */
    private fun dealWithFileOperation(context: Context, item: MDownloadItemBean?) {
        val dataType = item?.gameResData?.dataType // 数据包类型

        dataType?.let { type ->
            if (type == DataType.GameDataPackage || type == DataType.GameDataPackageCM) { // MOS或CM游戏数据包
                MOSDialog.show(
                    context = context,
                    title = context.getString(R.string.setup_game_data_install_path),
                    customView = { dialog ->
                        val modifier = Modifier
                        val settingViewModel = hiltViewModel<SettingViewModel>(
                            viewModelStoreOwner = LocalViewModelStoreOwner.current!!
                        )
                        val serverViewModel = hiltViewModel<ServerViewModel>(
                            viewModelStoreOwner = LocalViewModelStoreOwner.current!!
                        )
                        val gameSettingViewModel = hiltViewModel<GameSettingViewModel>(
                            viewModelStoreOwner = LocalViewModelStoreOwner.current!!
                        )
                        // 默认文件夹，不知道有什么其它好方法
                        var etValue = rememberSaveable { mutableStateOf(LauncherActivity.getDefaultDir() + "/srceng") }
                        var selectedVersion by remember { mutableStateOf(CSVersionInfo()) }
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartActivityForResult()
                        ) { result ->
                            etValue.value = ModLocalDataSource.getGamePath()
                        }
                        fun SelectVersion() { // 选择版本弹窗
                            viewModelScope.launch {
                                val versions = serverViewModel.getExistVersion()
                                selectVersionDialog(
                                    context,
                                    list = versions,
                                    listText = { it.versionNameForShow?:it.versionName.orEmpty() },
                                    onItemClick = {
                                        val newPath = it.gamePath?.let { path ->
                                            File(
                                                File(
                                                    LauncherActivity.getDefaultDir(),
                                                    Constants.GAME_ROOT_PATH
                                                ), it.versionName?:"Default"
                                            ).path
                                        }?:etValue.value // TODO 暂时这样 it.gamePath?.let { path -> File(path, it.versionName.orEmpty()).path }?:etValue.value
                                        etValue.value = newPath
                                        ModLocalDataSource.setGamePath(newPath)
                                        selectedVersion = it
                                    }
                                )
                            }
                        }
                        LaunchedEffect(Unit) {
                            SelectVersion()
                        }
                        Column(modifier = modifier
                            .fillMaxWidth()
                            .padding(GtaStartTheme.spacing.normal),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)) {
                            Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small)) {
                                Text(stringResource(R.string.tip_please_select_an_empty_path) + "\n若无特殊情况，请不要修改，系统已自动分配",
                                    style = MaterialTheme.typography.labelMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small)) {
                                    Text("已选择为 ${selectedVersion.versionNameForShow?:selectedVersion.versionName.orEmpty()} 安装",
                                        style = MaterialTheme.typography.bodySmall)
                                    Text("重新选择",
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.clickable {
                                        SelectVersion()
                                    }, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small)) {
                                TextField(
                                    modifier = modifier.weight(1f),
                                    value = etValue.value,
                                    onValueChange = {
                                        etValue.value = it
                                        ModLocalDataSource.setGamePath(it)
                                    }
                                )
                                MButton(
                                    text = stringResource(R.string.change),
                                    onClick = {
                                        MOSDialog.show(
                                            context, title = "提示", message = "若无特殊情况，请不要修改，系统已自动分配。擅自修改可能会导致无法进入游戏！",
                                            positiveButtonText = "取消",
                                            onPositiveButtonClick = {d,_ -> d.dismiss()},
                                            negativeButtonText = "修改",
                                            onNegativeButtonClick = {d,_ ->
                                                val intent = Intent(context, DirchActivity::class.java)
                                                launcher.launch(intent)
                                            },
                                        )
                                    }
                                )
                            }
                            MButton(
                                text = stringResource(R.string.install),
                                onClick = {
                                    viewModelScope.launch {
                                        val gamePath = ModLocalDataSource.getGamePath() // 选择好的路径

                                        val file = File(gamePath)
                                        if (!file.exists()) { // 检测文件夹情况
                                            if (file.mkdirs() && file.exists()) {
                                                gameSettingViewModel.changeSpecficSettings(
                                                    currentVersion = selectedVersion,
                                                    info = selectedVersion.copy(gamePath = gamePath) // 保存路径到数据库
                                                )
                                                unZipResource(context, item, gamePath) // 解压文件
                                                dialog.dismiss()
                                            } else {
                                                context.MToast(context.getString(R.string.selected_path_not_exist))
                                            }
                                        } else if (!file.isDirectory) {
                                            context.MToast(context.getString(R.string.selected_path_not_a_folder))
                                        } else if (file.listFiles() != null && file.listFiles()?.size != 0) {
                                            //context.MToast(context.getString(R.string.selected_path_not_empty))
                                            val folderName = mutableStateOf(selectedVersion.versionName?.let { it + file.getNextFolderSuffix(it) }?:"Default${file.getNextFolderSuffix("Default")}")
                                            createSubFolderDialog(
                                                context,
                                                folderName = folderName,
                                                onClick = {
                                                    val newFile = File(file.parentFile, folderName.value)
                                                    newFile.mkdirs()
                                                    gameSettingViewModel.changeSpecficSettings(
                                                        currentVersion = selectedVersion,
                                                        info = selectedVersion.copy(gamePath = newFile.path) // 保存路径到数据库
                                                    )
                                                    unZipResource(context, item, newFile.path) // 解压文件
                                                    dialog.dismiss()
                                                }
                                            )
                                        }/*else if (file.canWrite()) {
                                                context.MToast("选择的路径没有写入权限")
                                            }*/ else {
                                            gameSettingViewModel.changeSpecficSettings(
                                                currentVersion = selectedVersion,
                                                info = selectedVersion.copy(gamePath = gamePath) // 保存路径到数据库
                                            )
                                            unZipResource(context, item, gamePath) // 解压文件
                                            dialog.dismiss()
                                        }
                                    }
                                }
                            )
                        }
                    }
                )
            }
        } ?: also {
            context.MToast(context.getString(R.string.unknown_file_type))
        }
    }


    fun createSubFolderDialog(
        context: Context,
        folderName: MutableState<String>,
        onClick: () -> Unit
    ): AlertDialog {
        var dialog: AlertDialog? = null
        val value by folderName
        dialog = MOSDialog.show(
            context = context,
            title = context.getString(R.string.create_folder),
            customView = {
                Column {
                    Text(context.getString(R.string.selected_path_not_empty) + "，是否创建新文件夹并开始安装？文件夹请不要包含空格和符号",
                        style = MaterialTheme.typography.bodySmall)
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusable(),
                        value = value,
                        onValueChange = {
                            folderName.value = it
                        }
                    )
                }
            },
            positiveButtonText = context.getString(R.string.ok),
            onPositiveButtonClick = { dialogInterface, i ->
                onClick.invoke()
                dialogInterface.dismiss()
            }
        )
        return dialog
    }

    fun <T> selectVersionDialog(
        context: Context,
        list: List<T>,
        listText: (T) -> String,
        onItemClick: (T) -> Unit
    ): AlertDialog {
        var dialog: AlertDialog? = null
        fun dismissDialog() {
            dialog?.dismiss()
        }
         dialog = MOSDialog.show(
            context,
             cancelable = false,
            title = "请选择为哪个版本安装",
            customView = {
                LazyColumn {
                    items(list) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onItemClick(it)
                                    dismissDialog()
                                }
                                .padding(GtaStartTheme.spacing.medium)
                        ) {
                            Text(listText(it))
                        }
                    }
                }
            }
        )
        return dialog
    }

    fun downloadedContentOperation(context: Context, item: MDownloadItemBean?) {
        lateinit var builder: AlertDialog
        val view = LayoutInflater.from(context).inflate(R.layout.layout_compose, null)
        view.layoutParams = ViewGroup.LayoutParams(-1, -1)
        view.findViewById<ComposeView>(R.id.composeView).setContent {
            val modifier = Modifier
            GtaStartTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Column(modifier = modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier
                            .fillMaxWidth()
                            .clickable {
                                /*if (item?.downloadStatusData?.downloadStatus?.value == DownloadStatus.Downloading) {
                                    unZipDialog(context, item, isShowCloseButton)
                                    return@clickable
                                }*/
                                val url = item?.mDownload?.url.orEmpty()
                                item?.mDownload?.getDownloadTask()?.file?.let { file: File ->
                                    dealWithFileOperation(context, item) // 执行处理函数
                                } ?: also {
                                    context.MToast(context.getString(R.string.file_not_exist))
                                }
                                builder.dismiss()
                            }
                            .padding(GtaStartTheme.spacing.medium)) {
                            Text(
                                text = stringResource(R.string.install),
                                modifier = modifier.fillMaxWidth())
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier
                            .fillMaxWidth()
                            .clickable {
                                if (item?.downloadStatusData?.downloadStatus?.value == DownloadStatus.Downloading) {
                                    context.MToast(context.getString(R.string.tip_unzipping_plz_try_later))
                                    unZipDialog(context, item, isShowCloseButton)
                                    return@clickable
                                }
                                val url = item?.mDownload?.url.orEmpty()
                                Log.d(TAG, "downloadedContentOperation1: $url")

                                File(
                                    item?.mDownload?.parentPath ?: "",
                                    item?.mDownload?.fileName ?: ""
                                ).let { // 會直接把同名文件刪掉
                                    viewModelScope.launch {
                                        Log.d(TAG, "downloadedContentOperation: $url")
                                        if (!it.exists()) {
                                            removeDownloadInfoDB(url = url)
//                                            context.MToast(context.getString(R.string.file_not_exist))
                                            return@launch
                                        }
                                        val isOk = it.delete() // 删除
                                        if (isOk) {
                                            removeDownloadInfoDB(url = url)
                                            context.MToast(context.getString(R.string.delete_success))
                                        } else {
                                            context.MToast(context.getString(R.string.delete_failed))
                                        }
                                    }
                                } ?: also {
                                    context.MToast(context.getString(R.string.delete_failed))
                                }
                                builder.dismiss()
                            }
                            .padding(GtaStartTheme.spacing.medium)) {
                            Text(stringResource(R.string.delete), modifier = modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }

        builder = MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.operation))
            .setView(view)
            .show()
    }

}
package com.billflx.csgo.page.settings.game

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.billflx.csgo.data.AppLocalDataSource
import com.billflx.csgo.data.db.CSVersionInfo
import com.billflx.csgo.data.db.CSVersionInfoDatabase
import com.billflx.csgo.data.net.AppUpdateApi
import com.billflx.csgo.data.repo.CSVersionInfoRepository
import com.billflx.csgo.data.repo.paging.GameVersionRemoteMediator
import com.gtastart.common.util.MDownload
import com.gtastart.common.util.MSingleDownloadService
import com.gtastart.common.util.MToast
import com.gtastart.common.util.ZipUtils
import com.gtastart.common.util.extend.calculateMd5
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GameSettingViewModel @Inject constructor(
    private val app: Application,
    private val db: CSVersionInfoDatabase,
    private val api: AppUpdateApi,
    private val repository: CSVersionInfoRepository
): ViewModel() {

    companion object {
        private const val TAG = "GameSettingViewModel"
    }

    private val _currentVersion: MutableState<CSVersionInfo?> = mutableStateOf(null)
    var currentVersion: State<CSVersionInfo?> = _currentVersion
    val isInstalled = mutableStateOf(false)

    var mDownload: MDownload? = null

    val downloadProgress = mutableIntStateOf(0)
    val downloadIsFinish = mutableStateOf(false)
    val downloadGetError = mutableStateOf(false)
    val unZipProgress = mutableIntStateOf(0)
    val showDownloadButton = mutableStateOf(true)

    val isFilesMd5Passed = mutableStateOf(0) // 0 校验中 1 通过 -1 异常

    @OptIn(ExperimentalPagingApi::class)
    val pager: Pager<Int, CSVersionInfo> = Pager(
        config = PagingConfig(pageSize = 114),
        remoteMediator = GameVersionRemoteMediator(
            db = db,
            api = api,
            repo = repository
        ),
        pagingSourceFactory = {
            val pagingSource = db.getCSVersionInfoDao().pagingSource()
            Log.d(TAG, "pagingsource: inited")
            pagingSource
        }
    )
    var pagingFlow = pager
        .flow
        .cachedIn(viewModelScope)

    init {
        getLocalVersionList()
    }

    fun refreshVersionList() {
        val pagingSource = db.getCSVersionInfoDao().pagingSource()
        pagingSource.invalidate()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            _currentVersion.value?.let {
                repository.saveData(it)
            }
        }
    }

    fun changeSettings(info: CSVersionInfo) {
        val version = _currentVersion.value
        _currentVersion.value = _currentVersion.value?.copy(
            argv = info.argv?:version?.argv,
            env = info.env?:version?.env,
            gamePath = info.gamePath?:version?.gamePath,
            nickName = info.nickName?:version?.nickName,
        )
        saveSettings()
    }

    fun getLocalVersionList() {
        viewModelScope.launch {
            val list = repository.getAll().firstOrNull()
            list?.let { _currentVersion.value = it }
            isVersionExist()
        }
    }

    fun changeVersion(versionName: String) {
        viewModelScope.launch {
            val item = repository.getByVersionName(versionName)
            _currentVersion.value = item
            isVersionExist()
            showDownloadButton()
        }
    }

    fun checkSourceDataDialog() {

    }

    suspend fun verifyFileMd5(file: File, md5: String)
    = file.calculateMd5().lowercase() == md5.lowercase()

    private var verifyJob: Job? = null
    fun verifyAllFile() {
        verifyJob?.cancel()
        verifyJob = viewModelScope.launch(Dispatchers.IO) {
            isFilesMd5Passed.value = 0
            _currentVersion.value?.let b@ { version ->
                val parent = File(AppLocalDataSource.getLibParentPath(), version.libPath.orEmpty())
                val vpkFile = File(AppLocalDataSource.getLibParentPath(), version.vpkName.orEmpty())

                version.vpkMd5?.let {
                        if (!vpkFile.exists() ||
                            !verifyFileMd5(vpkFile, it)) {
                            isFilesMd5Passed.value = -1
                        return@b
                    }
                } ?: also {
                    isFilesMd5Passed.value = 2
                    return@b
                }

                version.fileList?.let a@ {
                    it.forEach {
                        val soFile = File(parent, it.fileName.orEmpty())
                        if (soFile.name.endsWith(".so") && (!soFile.exists() || !verifyFileMd5(soFile, it.md5.orEmpty()))) {
                            isFilesMd5Passed.value = -1
                            return@b
                        }
                    }
                } ?: also {
                    isFilesMd5Passed.value = 2
                    return@b
                }
                isFilesMd5Passed.value = 1
            }

        }
    }

    fun isVersionExist(): Boolean {
        _currentVersion.value?.let { version ->
            val parentPath = app.filesDir.path + version.libPath
            version.fileList?.forEach {
                val path = parentPath + File.separator + it.fileName
                Log.d(TAG, "isVersionExist: $path")
                if (!File(path).exists()) {
                    isInstalled.value = false
                    return false
                }
            }
        } ?: also {
            app.MToast("请选择一个版本")
            isInstalled.value = false
            return false
        }
        isInstalled.value = true
        return true
    }

    fun showDownloadButton() {
        showDownloadButton.value = true
        downloadProgress.value = 0
        downloadIsFinish.value = false
        downloadGetError.value = false
        unZipProgress.value = 0
    }

    fun deleteLibs() {
        viewModelScope.launch {
            _currentVersion.value?.let {
                File(app.filesDir.path + it.libPath).listFiles()?.forEach { file ->
                    if (file.exists()) {
                        file.delete()
                    }
                }
                app.MToast("删除完成")
                changeVersion(_currentVersion.value?.versionName.orEmpty())
            }
        }
    }

    val downloadConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val myService = (service as MSingleDownloadService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
    fun downloadLibs(context: Context) {
        _currentVersion.value?.let { version ->
            if (version.libPackUrl == null) {
                showDownloadButton()
                app.MToast("链接获取失败")
                return
            }
            val fileName = version.libPackUrl.substringAfterLast("/")
            val parentPath = app.cacheDir.path + version.libPath
            val file = File(parentPath, fileName)
            if (file.exists()) file.delete()
            if (mDownload != null) {
                mDownload?.stop()
            }
            mDownload = MDownload(
                url = version.libPackUrl,
                parentPath = parentPath,
                fileName = fileName,
                connectionCount = 1
            )
            MSingleDownloadService.startDownloadService(
                context = context,
                url = version.libPackUrl,
                parentPath = parentPath,
                fileName = fileName,
                connection = downloadConnection
            )
            mDownload?.setListener(downloadListener())
            mDownload?.start()
        } ?: also {
            app.MToast("下载前请选择一个游戏版本")
        }
    }

    fun downloadVpk() {
        _currentVersion.value?.let { version ->
            if (version.vpkUrl == null) {
                app.MToast("链接获取失败")
                return
            }
            val fileName = version.vpkUrl.substringAfterLast("/")
            if (mDownload != null) {
                mDownload?.getDownloadTask()?.cancel()
            }
            mDownload = MDownload(
                url = version.vpkUrl,
                parentPath = app.filesDir.path,
                fileName = version.vpkName,
                connectionCount = 1
            )
            mDownload?.setListener(downloadListener("vpk"))
            mDownload?.start()
        } ?: also {
            app.MToast("下载前请选择一个游戏版本")
        }
    }

    fun extractLibs() {
        _currentVersion.value?.let { version ->
            val fileName = version.libPackUrl.orEmpty().substringAfterLast("/")
            val pathFrom = app.cacheDir.path + version.libPath + File.separator + fileName
            val pathTo = app.filesDir.path + version.libPath

            Log.d(TAG, "extractLibs: $pathFrom | $pathTo")
            File(pathTo).listFiles()?.forEach {
                setPermission(it, false)
                if (it.isFile) { it.delete() } // 解压前先清理文件
            }
            ZipUtils.unZip(
                pathFrom = pathFrom,
                pathTo = pathTo,
                listener = unZipListener()
            )
            File(pathTo).listFiles()?.forEach { if (it.isFile) { setPermission(it, true) } }
        } ?: also {
            app.MToast("解压失败，请选择一个游戏版本")
        }
    }

    fun setPermission(file: File, readOnly: Boolean) {
        if (readOnly) {
            file.setReadable(true)
            file.setWritable(false)
            file.setExecutable(false)
        } else {
            file.setReadable(true)
            file.setWritable(true)
            file.setExecutable(false) // 如果为true，会导致目录不安全，从而无法执行run-as命令
        }
    }

    private fun unZipListener(): ZipUtils.Companion.ProgressListener {
        return object : ZipUtils.Companion.ProgressListener {
            override fun onProgressUpdate(percent: Int) {
                Log.d(TAG, "onProgressUpdate: $percent")
                unZipProgress.value = percent
            }

            override fun onCompleted() {
                if (isVersionExist()) {
                    app.MToast("解压完成")
                    isInstalled.value = true
                } else {
                    showDownloadButton()
                    app.MToast("安装失败")
                }
            }

            override fun onError(error: String) {
                Log.d(TAG, "解压失败 onError: $error")
                app.MToast("解压失败")
            }

        }
    }

    private fun downloadListener(whatToDownload: String = "libs"): MDownload.MDownloadListener {
        return object : MDownload.MDownloadListener {
            override fun onStart(task: DownloadTask) {
                downloadIsFinish.value = false
                downloadGetError.value = false
            }

            override fun onConnected(
                task: DownloadTask,
                blockCount: Int,
                currentOffset: Long,
                totalLength: Long
            ) {

            }

            override fun onProgress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
                val progress = MDownload.toProgress(currentOffset, totalLength)
                Log.d(TAG, "onProgress: ${progress}")
                downloadProgress.intValue = progress
            }

            override fun onStop(task: DownloadTask, cause: EndCause, realCause: Exception?) {
                if (cause == EndCause.COMPLETED) {
                    downloadIsFinish.value = true
                    downloadGetError.value = false
                    if (whatToDownload == "vpk") {
                        extractLibs()
                    } else {
                        downloadVpk()
                    }
                } else {
                    downloadIsFinish.value = false
                    downloadGetError.value = true
                    showDownloadButton()
                    app.MToast("下载失败")
                }
            }

            override fun onRetry(task: DownloadTask, cause: ResumeFailedCause) {

            }

        }
    }

}
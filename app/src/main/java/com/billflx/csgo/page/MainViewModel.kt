package com.billflx.csgo.page

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.billflx.csgo.bean.DownloadStatus
import com.billflx.csgo.bean.GameResItemBean
import com.billflx.csgo.bean.GameResLocalItemBean
import com.billflx.csgo.bean.MDownloadItemBean
import com.billflx.csgo.bean.MDownloadStatusBean
import com.billflx.csgo.bean.SampQueryInfoBean
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.gtastart.common.util.Coroutines
import com.gtastart.common.util.CsMosQuery
import com.gtastart.common.util.MDownload
import com.gtastart.common.util.isBlank
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import dagger.hilt.android.lifecycle.HiltViewModel
import me.nillerusr.LauncherActivity
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"


    }

    var noticeContent = mutableStateOf("公告加载中...")
    var openInstallSelectionDialog = mutableStateOf(false)
    var selectionDialogContent = mutableStateOf("当前目录\n${ModLocalDataSource.getGamePath()}\n为空，请选择下载游戏数据，或选择正确的游戏目录")

    var openDownloadDialog = mutableStateOf(false)

    var mDownloadList = mutableStateListOf<MDownloadItemBean>()
/*    var mDownload = mutableStateOf(MDownload(
        "http://106.52.5.176:5244/d/GTADE2024/1.8x/apk/GTA_III_1.83_Netflix%E7%BD%91%E9%A3%9E%E7%89%88(AML%2BXLog%2BMT%E6%9C%AC%E5%9C%B0%E5%AD%98%E5%82%A8).apk",
        LauncherActivity.getDefaultDir() + Constants.DOWNLOAD_PATH + Constants.GAME_PKG_CACHE_PATH
    ))*/

    var gameResList = mutableStateListOf<GameResItemBean>()

    val downloadPath = LauncherActivity.getDefaultDir() + Constants.DOWNLOAD_PATH + Constants.GAME_PKG_CACHE_PATH

    var gameDownloadFinishList = mutableStateListOf<GameResLocalItemBean>()

    init {
        loadFinishList()

        checkGameStatus()
        getGameResList()
    }

    fun loadFinishList() {
        gameDownloadFinishList.clear()
        val file = File(downloadPath)
        file.listFiles()?.forEach {
            if (!it.isDirectory) {
                gameDownloadFinishList.add(GameResLocalItemBean(
                    name = it.name,
                    fileSize = it.length().toString(),
                    path = it.absolutePath
                )
                )
            }
        }
    }

    fun getNotice() {

    }

    fun checkGameStatus() {
        if (!isGamePathEmpty()) {
            return
        }
    }

    fun isGamePathEmpty(): Boolean {
        val gameFile = File(ModLocalDataSource.getGamePath())
        if (gameFile.exists() && gameFile.isDirectory && gameFile.listFiles()?.size != 0) {
            return true
        }
        return false
    }

    fun getGameResList() {
        gameResList.add(GameResItemBean("v6.5游戏资源.zip", "3G", "http://106.52.5.176:5244/d/GTADE2024/termux-packages-fix-build-all.zip"))
        gameResList.add(GameResItemBean("v7.0游戏资源.zip", "3G", "http://106.52.5.176:5244/d/GTADE2024/termux-packages-fix-build-all.zip"))
    }

    fun addToDownloadList(gameResItemBean: GameResItemBean) {
        val mDownload = MDownload(
            url = gameResItemBean.downloadLink.orEmpty(),
            parentPath = downloadPath,
            fileName = gameResItemBean.title.orEmpty()
        )
        val downloadStatusData = MDownloadStatusBean(
            downloadStatus = mutableStateOf(DownloadStatus.IDLE)
        )
        mDownloadList.add(MDownloadItemBean(
            mDownload = mDownload,
            gameResData = gameResItemBean,
            downloadStatusData = downloadStatusData
        ))
        addDownloadListener(mDownload, downloadStatusData)
        mDownload.start()
    }

    fun addDownloadListener(mDownload: MDownload?, downloadStatusData: MDownloadStatusBean) {
        mDownload?.setListener(object : MDownload.MDownloadListener {
            override fun onStart(task: DownloadTask) {
                downloadStatusData.downloadStatus.value = DownloadStatus.Started
            }

            override fun onProgress(
                task: DownloadTask,
                currentOffset: Long,
                totalLength: Long
            ) {
                downloadStatusData.downloadStatus.value = DownloadStatus.Downloading
                downloadStatusData.downloadProgressStr.value = MDownload.getProgressDisplayLine(currentOffset, totalLength)
            }

            override fun onStop(task: DownloadTask, cause: EndCause, realCause: Exception?) {
                Log.d(TAG, "看看如何触发这个EndCause不同情况: ${cause.name}")
                if (cause == EndCause.COMPLETED) {
                    downloadStatusData.downloadStatus.value = DownloadStatus.Finished
                } else if (cause == EndCause.CANCELED) {
                    downloadStatusData.downloadStatus.value = DownloadStatus.PAUSE
                } else {
                    downloadStatusData.downloadStatus.value = DownloadStatus.ERROR
                }
            }

            override fun onRetry(task: DownloadTask, cause: ResumeFailedCause) {
                downloadStatusData.downloadStatus.value = DownloadStatus.ERROR
            }

        })
    }

    fun addToDownloadFinishList(name: String, size: String, path: String) {
        gameDownloadFinishList.add(GameResLocalItemBean(name, size, path))
    }

}
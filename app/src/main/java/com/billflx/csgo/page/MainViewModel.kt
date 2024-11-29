package com.billflx.csgo.page

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.billflx.csgo.bean.AppUpdateBean
import com.billflx.csgo.bean.DownloadExtraInfoBean
import com.billflx.csgo.bean.GameResLocalItemBean
import com.billflx.csgo.bean.MDownloadItemBean
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.repo.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import me.nillerusr.LauncherActivity
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"


    }

    var noticeContent = mutableStateOf("公告加载中...")
    var openInstallSelectionDialog = mutableStateOf(false)
    var selectionDialogContent = mutableStateOf("当前目录\n${ModLocalDataSource.getGamePath()}\n为空，请选择下载游戏数据，或选择正确的游戏目录")

    var openDownloadDialog = mutableStateOf(false)

    var mDownloadList = mutableStateListOf<MDownloadItemBean>()

    var gameResList = mutableStateListOf<DownloadExtraInfoBean>()

    val downloadPath = LauncherActivity.getDefaultDir() + Constants.DOWNLOAD_PATH + Constants.GAME_PKG_CACHE_PATH

    var gameDownloadFinishList = mutableStateListOf<GameResLocalItemBean>()

    var addDownloadText = mutableStateOf("下载数据包")

    init {
        checkGameStatus()
        getGameResList()
    }


    suspend fun getNotice(): AppUpdateBean? {
        try {
            val updateInfo = repository.getUpdateInfo()
            return updateInfo
        } catch (e: Exception) {
            Log.d(TAG, "getNotice: $e")
            return null
        }
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

    }


}
package com.billflx.csgo.page

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.billflx.csgo.bean.DownloadExtraInfoBean
import com.billflx.csgo.bean.GameResLocalItemBean
import com.billflx.csgo.bean.MDownloadItemBean
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.repo.AppRepository
import com.gtastart.data.bean.cs.AppUpdateBean
import com.valvesoftware.source.R
import dagger.hilt.android.internal.Contexts.getApplication
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

    var noticeContent = mutableStateOf(R.string.notice_is_loading)
    var openInstallSelectionDialog = mutableStateOf(false)

    var selectionDialogContent = mutableStateOf(String.format("当前目录\n%s\n为空，请选择下载游戏数据，或选择正确的游戏目录", ModLocalDataSource.getGamePath()))

    var openDownloadDialog = mutableStateOf(false)

    var mDownloadList = mutableStateListOf<MDownloadItemBean>()

    var gameResList = mutableStateListOf<DownloadExtraInfoBean>()

    val downloadPath = LauncherActivity.getDefaultDir() + Constants.DOWNLOAD_PATH + Constants.GAME_PKG_CACHE_PATH

    var gameDownloadFinishList = mutableStateListOf<GameResLocalItemBean>()

    var addDownloadText = mutableStateOf(R.string.download_game_data)

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
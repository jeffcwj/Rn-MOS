package com.billflx.csgo.constant

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.gtastart.data.bean.cs.AppUpdateBean
import com.valvesoftware.source.BuildConfig

class Constants {

    companion object {
        var appUpdateInfo: MutableState<AppUpdateBean?> = mutableStateOf(null)
        val appVersion = "r9.0"

        val customRoomVersion = 3
        val isAppUpdateInfoFailed = mutableStateOf(false)

        const val SOURCE_HOST = "135.125.188.162"
        const val SOURCE_PORT = 27010

        const val CONFIG_PATH = "/%s/cfg/config.cfg"
        const val AUTOEXEC_CONFIG_PATH = "/%s/cfg/autoexec.cfg"
        const val DOWNLOAD_PATH = "/Download/CSMOSDownload"

        const val GAME_PKG_CACHE_PATH = "/gameZip"

        const val CHECK_UPDATE_URL = "https://samp.fun/RnSAMP/CSMOS/checkUpdate.php"
        const val AUTO_EXEC_CMD_URL = "https://samp.fun/RnSAMP/CSMOS/autoExecCmds.php"

        val IS_DEBUG_MODE = BuildConfig.DEBUG

    }
}
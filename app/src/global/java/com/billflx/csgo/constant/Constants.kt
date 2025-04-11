package com.billflx.csgo.constant

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.billflx.csgo.bean.AppUpdateBean

class Constants {

    companion object {
        var appUpdateInfo: MutableState<AppUpdateBean?> = mutableStateOf(null)
        val appVersion = "r6.2"

        const val SOURCE_HOST = "135.125.188.162"
        const val SOURCE_PORT = 27010

        const val CONFIG_PATH = "/csmos/cfg/config.cfg"
        const val AUTOEXEC_CONFIG_PATH = "/csmos/cfg/autoexec.cfg"
        const val DOWNLOAD_PATH = "/Download/CSMOSDownload"

        const val GAME_PKG_CACHE_PATH = "/gameZip"

        const val DownloadFullVerRnCSLink = "https://github.com/jeffcwj/RnCS/releases"
        const val CHECK_UPDATE_URL = "https://samp.fun/RnSAMP/CSMOS/checkLiteUpdate.php"
        const val CHECK_UPDATE_URL_GITHUB = "https://raw.githubusercontent.com/jeffcwj/RnCS/refs/heads/android-fixes/update.json"

        val CheckUpdateUrlList = listOf(
            CHECK_UPDATE_URL,
            CHECK_UPDATE_URL_GITHUB
        )
    }
}
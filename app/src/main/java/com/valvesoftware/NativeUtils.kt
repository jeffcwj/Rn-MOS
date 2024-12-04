package com.valvesoftware

import com.billflx.csgo.constant.Constants.Companion.appUpdateInfo
import com.valvesoftware.source.BuildConfig

class NativeUtils {
    companion object {
        fun getFlavor(): String {
            return BuildConfig.FLAVOR.replace("-", ".")
        }

        fun getMasterServers(): List<String> {
            return appUpdateInfo.value?.link?.serverRootLink?: emptyList()
        }
    }
}
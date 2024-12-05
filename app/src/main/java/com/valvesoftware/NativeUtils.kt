package com.valvesoftware

import com.billflx.csgo.constant.Constants.Companion.appUpdateInfo
import com.valvesoftware.source.BuildConfig

class NativeUtils {
    companion object {
        @JvmStatic
        fun getFlavor(): String {
            return BuildConfig.FLAVOR.replace("-", ".")
        }

        @JvmStatic
        fun getMasterServers(): List<String> {
            return appUpdateInfo.value?.link?.serverRootLink?: emptyList()
        }
    }
}
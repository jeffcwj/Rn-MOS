package com.valvesoftware

import com.billflx.csgo.constant.Constants.Companion.appUpdateInfo
import com.billflx.csgo.data.ModLocalDataSource
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

        @JvmStatic
        fun isAllowNativeInject(): Boolean {
            return ModLocalDataSource.getAllowNativeInject()
        }
    }
}
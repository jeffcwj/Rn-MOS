package com.valvesoftware

import com.billflx.csgo.constant.Constants.Companion.appUpdateInfo
import com.billflx.csgo.data.ModLocalDataSource

class NativeUtils {
    companion object {
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
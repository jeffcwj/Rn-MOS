package com.valvesoftware

import android.util.Log
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.valvesoftware.source.BuildConfig

class NativeUtils {
    companion object {
        private const val TAG = "NativeUtils"

        @JvmStatic
        fun getFlavor(): String {
            return ModLocalDataSource.getCurrentCSVersion()
        }

        @JvmStatic
        fun getMasterServers(): List<String> { // TODO 实际上获取的内容为null
            Log.d(TAG, "getMasterServers: ${Constants.appUpdateInfo.value?.link?.serverRootLink}")
            return Constants.appUpdateInfo.value?.link?.serverRootLink?: emptyList()
//            return listOf("103.8.71.148:33852")
        }

        @JvmStatic
        fun isAllowNativeInject(): Boolean {
            return ModLocalDataSource.getAllowNativeInject()
        }
    }
}
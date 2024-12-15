package com.billflx.csgo

import android.app.Application
import android.content.Context
import com.billflx.csgo.data.ModLocalDataSource
import com.gtastart.GtaStartApplication
import com.gtastart.common.util.Logcat
import com.liulishuo.okdownload.OkDownload
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class CSApplication : GtaStartApplication() {

    companion object {

    }

//    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
//        context = applicationContext
        val logcat = Logcat(context, "RnMOSLog.txt")
        logcat.saveLog()

        ModLocalDataSource.init(context)

    }

}
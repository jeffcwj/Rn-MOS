package com.billflx.csgo

import android.app.Application
import android.content.Context
import com.billflx.csgo.data.AppLocalDataSource
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.db.CSVersionInfoDao
import com.billflx.csgo.data.repo.CSVersionInfoRepository
import com.gtastart.GtaStartApplication
import com.gtastart.common.util.Coroutines
import com.gtastart.common.util.Logcat
import com.liulishuo.okdownload.OkDownload
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class CSApplication : GtaStartApplication() {

    companion object {

    }

    @Inject
    lateinit var csVersionInfoRepository: CSVersionInfoRepository

//    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
//        context = applicationContext
        val logcat = Logcat(context, "RnMOSLog.txt")
        logcat.saveLog()

        ModLocalDataSource.init(context)
        AppLocalDataSource.init(context)

        Coroutines.main {
            ModLocalDataSource.migrateDataToDb(csVersionInfoRepository) // 从老版本升级到新版本的，初始化数据库
        }

    }

}
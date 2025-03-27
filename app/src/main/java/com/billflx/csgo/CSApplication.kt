package com.billflx.csgo

import com.billflx.csgo.data.AppLocalDataSource
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.repo.CSVersionInfoRepository
import com.gtastart.GtaStartApplication
import com.gtastart.common.util.Logcat
import com.gtastart.common.util.MHelpers
import com.heyanle.okkv2.MMKVStore
import com.heyanle.okkv2.core.Converter
import com.heyanle.okkv2.core.Okkv
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel
import com.valvesoftware.source.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import loli.ball.okkv2.composeInterceptor
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

        initOkkv()

        ModLocalDataSource.init(context)
        AppLocalDataSource.init(context)

        // 初始化 Bugly
        val strategy = UserStrategy(applicationContext)
        strategy.setDeviceID(AppLocalDataSource.getUUID())
        strategy.setDeviceModel(MHelpers.getDeviceModel())
        CrashReport.setIsDevelopmentDevice(context, BuildConfig.DEBUG)
        CrashReport.initCrashReport(applicationContext, "c6203bec1d", false, strategy)

        /*Coroutines.main {
            ModLocalDataSource.migrateDataToDb(csVersionInfoRepository) // 从老版本升级到新版本的，初始化数据库
        }*/

    }

    private fun initOkkv() {
        Okkv.Builder(MMKVStore(this))
            .cache()
            .composeInterceptor()
//            .fallbackKotlinxSerializationConverter()
            .fallbackConverter(object : Converter<Any, String> {
                override fun deserialize(data: String, clazz: Class<Any>): Any {
                    if (!clazz.isEnum) error("not support")
                    @Suppress("UNCHECKED_CAST")
                    return java.lang.Enum.valueOf(clazz as Class<out Enum<*>>, data)
                }

                override fun serialize(data: Any, clazz: Class<Any>): String {
                    if (!clazz.isEnum) error("not support")
                    return (data as Enum<*>).name
                }
            })
            .build()
            .init()
            .default()
        MMKV.setLogLevel(MMKVLogLevel.LevelNone)
    }

}
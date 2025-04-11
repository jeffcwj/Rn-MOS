package com.billflx.csgo

import android.app.Application
import com.billflx.csgo.data.ModLocalDataSource
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class CSApplication : Application() {

    companion object {

    }

    override fun onCreate() {
        super.onCreate()

        ModLocalDataSource.init(applicationContext)

    }

}
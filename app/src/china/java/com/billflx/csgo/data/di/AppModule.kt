package com.billflx.csgo.data.di

import com.billflx.csgo.data.net.AppUpdateApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAppUpdateApi(): AppUpdateApi {
        return AppUpdateApi.invoke()
    }

}
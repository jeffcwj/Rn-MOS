package com.billflx.csgo.data.di

import android.content.Context
import androidx.room.Room
import com.billflx.csgo.data.db.DownloadInfo
import com.billflx.csgo.data.db.DownloadInfoDao
import com.billflx.csgo.data.db.DownloadInfoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // 单例注入database
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): DownloadInfoDatabase {
        return Room.databaseBuilder(
            context,
            DownloadInfoDatabase::class.java,
            "DownloadInfoDatabase"
        ).build()
    }

    @Provides
    fun provideDownloadInfoDao(database: DownloadInfoDatabase): DownloadInfoDao {
        return database.getDownloadInfoDao()
    }
}
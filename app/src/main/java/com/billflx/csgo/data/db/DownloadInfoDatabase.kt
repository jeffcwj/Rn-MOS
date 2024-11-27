package com.billflx.csgo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized


@Database(
    entities = [DownloadInfo::class],
    version = 1,
    exportSchema = false // 禁用架构导出，懒得配置没啥用
)
abstract class DownloadInfoDatabase : RoomDatabase() {

    abstract fun getDownloadInfoDao(): DownloadInfoDao

    // 直接用依赖注入管理单例
    /*companion object {
        @Volatile private var instance: DownloadInfoDatabase? = null
        private val LOCK = Any()

        @OptIn(InternalCoroutinesApi::class)
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            DownloadInfoDatabase::class.java,
            "DownloadInfoDatabase"
        ).build()
    }*/

}
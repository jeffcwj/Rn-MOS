package com.billflx.csgo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized


@Database(
    entities = [CSVersionInfo::class],
    version = 2,
    exportSchema = false // 禁用架构导出，懒得配置没啥用
)
@TypeConverters(GsonConverters::class)
abstract class CSVersionInfoDatabase : RoomDatabase() {

    abstract fun getCSVersionInfoDao(): CSVersionInfoDao

}
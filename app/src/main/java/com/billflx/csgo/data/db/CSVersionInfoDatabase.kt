package com.billflx.csgo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized


@Database(
    entities = [CSVersionInfo::class],
    version = 1,
    exportSchema = false // 禁用架构导出，懒得配置没啥用
)
abstract class CSVersionInfoDatabase : RoomDatabase() {

    abstract fun getCSVersionInfoDao(): CSVersionInfoDao

}
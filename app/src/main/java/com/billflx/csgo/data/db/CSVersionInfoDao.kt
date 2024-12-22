package com.billflx.csgo.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CSVersionInfoDao {
    @Query("select * from csversioninfo where version_name = :versionName")
    suspend fun getVersionInfo(versionName: String): CSVersionInfo

    @Query("select argv from csversioninfo where version_name = :versionName")
    suspend fun getArgv(versionName: String): String?

    @Query("select game_path from csversioninfo where version_name = :versionName")
    suspend fun getGamePath(versionName: String): String?

    @Query("select nick_name from csversioninfo where version_name = :versionName")
    suspend fun getNickName(versionName: String): String?

    @Query("select env from csversioninfo where version_name = :versionName")
    suspend fun getEnv(versionName: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrInsertInfo(csVersionInfo: CSVersionInfo)

    @Insert
    suspend fun addInfo(csVersionInfo: CSVersionInfo)

    @Query("select count(*) from csversioninfo")
    suspend fun getRowCount(): Int
}
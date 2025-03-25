package com.billflx.csgo.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery

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

    @Query("select * from csversioninfo")
    suspend fun getAll(): List<CSVersionInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE) // 跳过
    suspend fun insertAll(csVersionInfos: List<CSVersionInfo>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(csVersionInfo: CSVersionInfo)

    @RawQuery(observedEntities = [CSVersionInfo::class])
    fun invalidatePagingSource(query: SupportSQLiteQuery): Int

    @Query("select * from csversioninfo")
    fun pagingSource(): PagingSource<Int, CSVersionInfo>

    @Query("DELETE FROM csversioninfo")
    fun clearAll()
}
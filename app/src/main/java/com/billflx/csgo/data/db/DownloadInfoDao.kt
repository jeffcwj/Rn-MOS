package com.billflx.csgo.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DownloadInfoDao {

    @Insert
    suspend fun addInfo(downloadInfo: DownloadInfo)

    @Query("select * from downloadinfo")
    suspend fun getAllInfos(): List<DownloadInfo>

    @Query("select * from downloadinfo where is_finished = 0")
    suspend fun getDownloadingInfos(): List<DownloadInfo>

    @Query("select * from downloadinfo where is_finished = 1")
    suspend fun getDownloadedInfos(): List<DownloadInfo>

    @Update
    suspend fun updateInfo(downloadInfo: DownloadInfo) // 需要测试传url为依据，是否正常更新数据

    @Query("select * from downloadinfo where url = :url")
    suspend fun getInfoByUrl(url: String): DownloadInfo

    @Query("delete from downloadinfo where id = :id")
    suspend fun deleteInfoById(id: Int)

    @Query("delete from downloadinfo where url = :url")
    suspend fun deleteInfoByUrl(url: String)
}
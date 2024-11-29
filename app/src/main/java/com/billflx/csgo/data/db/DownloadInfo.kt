package com.billflx.csgo.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["url"], unique = true)] // url 作为唯一约束，防止产生多次下载
)
data class DownloadInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0, // 主键
    @ColumnInfo(name = "file_name")
    val fileName: String,
/*    @ColumnInfo(name = "file_size")
    val fileSize: Long,*/
    @ColumnInfo(name = "parent_path")
    val parentPath: String,
    val url: String,
    @ColumnInfo(name = "downloaded_bytes")
    val downloadedBytes: Long,
    @ColumnInfo(name = "total_bytes")
    val totalBytes: Long,
    @ColumnInfo(name = "is_finished")
    val isFinished: Boolean, // 用于获取下载中/已完成列表
    @ColumnInfo(name = "data_type")
    val dataType: String, // 文件类型，用于区分按钮的操作，比如是解压游戏数据包还是什么
) {

}
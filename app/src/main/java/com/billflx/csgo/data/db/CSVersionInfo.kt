package com.billflx.csgo.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["version_name"], unique = true)]
)
data class CSVersionInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0, // 主键
    @ColumnInfo(name = "version_name")
    val versionName: String? = null,
    val env: String? = null,
    val argv: String? = null,
    @ColumnInfo(name = "game_path")
    val gamePath: String? = null,
    @ColumnInfo(name = "nick_name")
    val nickName: String? = null,
) {

}
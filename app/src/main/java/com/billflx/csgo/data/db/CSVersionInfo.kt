package com.billflx.csgo.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.billflx.csgo.bean.CsRemoteVersionInfo.FileDetail
import com.gtastart.data.bean.cs.AppUpdateBean

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
    // version 2
    val libPackUrl: String? = null,
    val libPath: String? = null,
    val vpkName: String? = null,
    val vpkUrl: String? = null,
    val csType: String? = null,
    val versionNameForShow: String? = null,
    val fileList: List<FileDetail>? = emptyList(),
    val vpkMd5: String? = null,
    val dataLink: List<AppUpdateBean.DataLink>? = null
) {

}
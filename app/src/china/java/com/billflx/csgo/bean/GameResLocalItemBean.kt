package com.billflx.csgo.bean

data class GameResLocalItemBean (
    val name: String? = null,
    val fileSize: String? = null,
    val path: String? = null,
    var unZipStatusData: UnZipStatusBean? = null
)
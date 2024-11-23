package com.billflx.csgo.bean

import com.gtastart.common.util.MDownload

data class MDownloadItemBean (
    var mDownload: MDownload? = null,
    var gameResData: GameResItemBean? = null,
    var downloadStatusData: MDownloadStatusBean? = null
)
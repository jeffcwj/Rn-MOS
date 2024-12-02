package com.billflx.csgo.bean

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

data class MDownloadStatusBean (
    var downloadStatus: MutableState<DownloadStatus> = mutableStateOf(DownloadStatus.IDLE),
    var currentOffset: Long = 0,
    var totalLength: Long = 0,
    var downloadProgressStr: MutableState<String> = mutableStateOf(""),
    var retryCount: MutableState<Int> = mutableIntStateOf(0)
)
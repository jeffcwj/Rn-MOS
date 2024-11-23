package com.billflx.csgo.bean

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class UnZipStatusBean (
    var progress: MutableState<Int> = mutableStateOf(0),
    var unZipStatus: MutableState<UnZipStatus> = mutableStateOf(UnZipStatus.Idle)
) {
    enum class UnZipStatus {
        UnZipping,
        Error,
        Done,
        Idle
    }
}
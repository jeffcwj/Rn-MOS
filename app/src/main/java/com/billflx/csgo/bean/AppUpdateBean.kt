package com.billflx.csgo.bean

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.io.Serializable

data class AppUpdateBean(
    val app: App,
    val link: Link
): Serializable {
    data class App(
        val version: String,
        val versionCode: String,
        val updateMsg: String,
        val allowVersions: String,
        val notice: String,
        val noticeVersion: Int,
        val isPopOutNotice: Int,
        val link: String,
        val isDirectLink: Int,
        val enableApp: Int,
        val enableLink: Int,
        val disableReason: String,
        var hasUpdate: MutableState<Boolean> = mutableStateOf(false)
    )

    data class Link(
        val dataLink: List<DataLink>,
        val serverRootLink: List<String>,
        val cmRootLink: List<String>,
    )

    data class DataLink(
        val title: String,
        val url: String,
        val type: String
    )

}





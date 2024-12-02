package com.billflx.csgo.bean

data class AppUpdateBean(
    val app: App,
    val link: Link
) {
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
        val disableReason: String
    )

    data class Link(
        val dataLink: List<DataLink>
    )

    data class DataLink(
        val title: String,
        val url: String
    )
}





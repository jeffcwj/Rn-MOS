package com.billflx.csgo.bean

data class CsRemoteVersionInfo(
        val versionName: String?,
        val libPackUrl: String?,
        val libPath: String?,
        val vpkName: String?,
        val vpkUrl: String?,
        val csType: String?,
        val versionNameForPath: String?,
        val defaultArgs: String?,
        val defaultEnv: String?,
        val defaultRelativeGamePath: String?,
        val defaultNickName: String?,
        val fileList: List<FileDetail>?
    ) {
        data class FileDetail(
            val fileName: String?,
            val md5: String?
        )
    }
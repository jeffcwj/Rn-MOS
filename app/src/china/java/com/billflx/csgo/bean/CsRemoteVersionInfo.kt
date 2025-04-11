package com.billflx.csgo.bean

import com.gtastart.data.bean.cs.AppUpdateBean

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
        val fileList: List<FileDetail>?,
        val vpkMd5: String?,
        val dataLink: List<AppUpdateBean.DataLink>?,
    ) {
        data class FileDetail(
            val fileName: String?,
            val md5: String?
        )
    }
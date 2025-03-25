package com.billflx.csgo.data.mapper

import com.billflx.csgo.bean.CsRemoteVersionInfo
import com.billflx.csgo.data.db.CSVersionInfo
import me.nillerusr.LauncherActivity
import java.io.File

fun CsRemoteVersionInfo.toEntity(): CSVersionInfo {
    return CSVersionInfo(
        versionName = this.versionNameForPath,
        env = this.defaultEnv,
        argv = this.defaultArgs,
        gamePath = LauncherActivity.getDefaultDir() + this.defaultRelativeGamePath,
        nickName = this.defaultNickName,
        libPackUrl = this.libPackUrl,
        libPath = this.libPath,
        vpkName = this.vpkName,
        vpkUrl = this.vpkUrl,
        csType = this.csType,
        versionNameForShow = this.versionName,
        fileList = this.fileList,
        vpkMd5 = this.vpkMd5,
        dataLink = this.dataLink
    )
}

fun CSVersionInfo.toRemoteEntity(): CsRemoteVersionInfo {
    return CsRemoteVersionInfo(
        versionName = this.versionNameForShow,
        libPackUrl = this.libPackUrl,
        libPath = this.libPath,
        vpkName = this.vpkName,
        vpkUrl = this.vpkUrl,
        csType = this.csType,
        versionNameForPath = this.versionName,
        defaultArgs = this.argv,
        defaultEnv = this.env,
        defaultRelativeGamePath = File.separator + File(this.gamePath.orEmpty()).name,
        defaultNickName = this.nickName,
        fileList = this.fileList,
        vpkMd5 = this.vpkMd5,
        dataLink = this.dataLink
    )
}
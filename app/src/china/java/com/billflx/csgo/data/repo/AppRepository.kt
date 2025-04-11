package com.billflx.csgo.data.repo

import androidx.annotation.Keep
import com.billflx.csgo.data.net.AppUpdateApi
import javax.inject.Inject

@Keep
class AppRepository @Inject constructor(
    private val api: AppUpdateApi
) : BaseRepository() {

    suspend fun getUpdateInfo() = apiRequest { api.getUpdateInfo() }

    suspend fun getAutoExecCmds() = apiRequest { api.getAutoExecCmds() }
}
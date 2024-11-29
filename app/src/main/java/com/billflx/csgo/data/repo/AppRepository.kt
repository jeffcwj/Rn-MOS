package com.billflx.csgo.data.repo

import com.billflx.csgo.data.net.AppUpdateApi
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val api: AppUpdateApi
) : BaseRepository() {

    suspend fun getUpdateInfo() = apiRequest { api.getUpdateInfo() }
}
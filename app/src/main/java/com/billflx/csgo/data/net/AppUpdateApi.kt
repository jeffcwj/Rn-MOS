package com.billflx.csgo.data.net

import com.billflx.csgo.bean.AppUpdateBean
import com.billflx.csgo.constant.Constants
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface AppUpdateApi {

    companion object {
        operator fun invoke() : AppUpdateApi {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://samp.fun")
                .build()
                .create(AppUpdateApi::class.java)
        }
    }

    @GET(Constants.CHECK_UPDATE_URL)
    suspend fun getUpdateInfo(): Response<AppUpdateBean>

}
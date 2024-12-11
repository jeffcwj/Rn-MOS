package com.billflx.csgo.data.net

import android.util.Log
import com.billflx.csgo.bean.AppUpdateBean
import com.billflx.csgo.bean.AutoExecCmdBean
import com.billflx.csgo.constant.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

interface AppUpdateApi {

    companion object {
        operator fun invoke() : AppUpdateApi {
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                    Log.d("RetrofitLog", message)

            }
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // 添加日志
                .build()

            return Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://samp.fun")
                .client(okHttpClient) // 添加自定义 okHttpClient
                .build()
                .create(AppUpdateApi::class.java)
        }
    }

    @GET(Constants.CHECK_UPDATE_URL)
    suspend fun getUpdateInfo(): Response<AppUpdateBean>


    @GET(Constants.AUTO_EXEC_CMD_URL)
    suspend fun getAutoExecCmds(): Response<List<AutoExecCmdBean>>

}
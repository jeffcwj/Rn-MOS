package com.billflx.csgo.data.repo

import android.util.Log
import androidx.annotation.Keep
import com.billflx.csgo.data.net.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@Keep
abstract class BaseRepository {

    suspend fun<T: Any> apiRequest(call: suspend () -> Response<T>): T {
        try {
            val response = call.invoke()
            if (response.isSuccessful) {
                return response.body()!!
            } else {
                Log.d("TAG", "apiRequest: failed")
                throw ApiException(response.code().toString())
            }
        } catch (e: Exception) {
            Log.d("", "apiRequest: ${e}")
            throw ApiException("fucked up")
        }
    }

    class ApiException(message: String): IOException(message)

    suspend fun <T> safeApiCall(
        maxRetry: Int = 3,
        delayMs: Long = 1000,
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            var currentRetry = 0
            var result: Resource<T>

            while (true) {
                try {
                    result = Resource.Success(apiCall.invoke())
                    break
                } catch (throwable: Throwable) {
                    if (currentRetry >= maxRetry) {
                        result = when (throwable) {
                            is HttpException -> {
                                Resource.Failure(false, throwable.code(),
                                    throwable.response()?.errorBody()
                                )
                            }
                            else -> {
                                Resource.Failure(true, null, null)
                            }
                        }
                        break
                    }
                    // 增加延迟并递增重试次数
                    currentRetry++
                    delay(delayMs * currentRetry) // 指数退避
                }
            }
            result
        }
    }
}
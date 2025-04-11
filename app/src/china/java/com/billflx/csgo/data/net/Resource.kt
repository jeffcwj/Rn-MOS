package com.billflx.csgo.data.net

import okhttp3.ResponseBody

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
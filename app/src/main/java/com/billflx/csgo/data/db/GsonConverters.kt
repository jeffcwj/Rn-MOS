package com.billflx.csgo.data.db

import androidx.room.TypeConverter
import com.billflx.csgo.bean.CsRemoteVersionInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GsonConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromFileDetailList(value: List<CsRemoteVersionInfo.FileDetail>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toFileDetailList(value: String): List<CsRemoteVersionInfo.FileDetail> {
        val type = object : TypeToken<List<CsRemoteVersionInfo.FileDetail>>() {}.type
        return gson.fromJson(value, type)
    }
}

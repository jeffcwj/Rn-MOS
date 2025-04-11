package com.billflx.csgo.data.db

import androidx.room.TypeConverter
import com.billflx.csgo.bean.CsRemoteVersionInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gtastart.data.bean.cs.AppUpdateBean

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

    @TypeConverter
    fun fromDataLinkList(value: List<AppUpdateBean.DataLink>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDataLinkList(value: String): List<AppUpdateBean.DataLink> {
        val type = object : TypeToken<List<AppUpdateBean.DataLink>>() {}.type
        return gson.fromJson(value, type)
    }


}

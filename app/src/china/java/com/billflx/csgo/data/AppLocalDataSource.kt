package com.billflx.csgo.data

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.billflx.csgo.bean.CSVersionInfoEnum
import com.billflx.csgo.data.db.CSVersionInfo
import com.billflx.csgo.data.repo.CSVersionInfoRepository
import com.billflx.csgo.nav.MainPageDestination
import com.gtastart.common.util.MHelpers
import me.nillerusr.LauncherActivity
import java.util.UUID

@SuppressLint("StaticFieldLeak")
object AppLocalDataSource {

    const val SP_NAME = "app"
    lateinit var sp: SharedPreferences
    lateinit var context: Context

    fun init(context: Context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_MULTI_PROCESS)
        this.context = context
    }

    // SharedPreferences 扩展函数
    inline fun <reified T> SharedPreferences.getValue(key: String, defaultValue: T): T {
        return when (T::class) {
            Boolean::class -> getBoolean(key, defaultValue as Boolean) as T
            Int::class -> getInt(key, defaultValue as Int) as T
            Float::class -> getFloat(key, defaultValue as Float) as T
            Long::class -> getLong(key, defaultValue as Long) as T
            String::class -> getString(key, defaultValue as String) as T
            else -> throw IllegalArgumentException("不支持的类型")
        }
    }

    fun SharedPreferences.setValue(key: String, value: Any) {
        with(edit()) {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is String -> putString(key, value)
                else -> throw IllegalArgumentException("不支持的类型")
            }
            apply()
        }
    }

    fun getNoticeVersion() = sp.getValue("NoticeVersion", 0)
    fun setNoticeVersion(value: Int) {
        sp.setValue("NoticeVersion", value)
    }

    fun isExperimental() = sp.getValue("experimental", false)
    fun setExperimental(value: Boolean) {
        sp.setValue("experimental", value)
    }
    fun getLibParentPath() = sp.getValue("libParentPath", context.filesDir.path)
    fun setLibParentPath(value: String) {
        sp.setValue("libParentPath", value)
    }
    fun getLastLaunchPage() = sp.getValue("lastLaunchPage", MainPageDestination.AHome.route)
    fun setLastLaunchPage(value: String) {
        sp.setValue("lastLaunchPage", value)
    }

    fun getUUID(sp: SharedPreferences? = null): String {
        var spp: SharedPreferences? = sp
        if (sp == null) {
            spp = this.sp
        }
        var id = spp?.getString("uuid", null)
        if (id == null) {
            id = MHelpers.generateShortId()
            setUUID(id)
        }
        return id
    }
    fun setUUID(value: String, sp: SharedPreferences? = null) {
        var spp: SharedPreferences? = sp
        if (sp == null) {
            spp = this.sp
        }
        spp?.setValue("uuid", value)
    }
}

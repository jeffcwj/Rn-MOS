package com.billflx.csgo.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.billflx.csgo.bean.CSVersionInfoEnum
import com.billflx.csgo.data.db.CSVersionInfo
import com.billflx.csgo.data.repo.CSVersionInfoRepository
import me.nillerusr.LauncherActivity

object ModLocalDataSource {

    const val SP_NAME = "mod"
    lateinit var sp: SharedPreferences

    fun init(context: Context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_MULTI_PROCESS)
    }

    suspend fun migrateDataToDb(csVersionInfoRepository: CSVersionInfoRepository) {
        if (csVersionInfoRepository.isDBEmpty()) {
            val info = CSVersionInfo(
                versionName = CSVersionInfoEnum.getDefaultName(),
                env = getEnv(),
                argv = getArgv(),
                gamePath = getGamePath(),
                nickName = getNickName()
            )
            csVersionInfoRepository.addInfo(info)
        }
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

    fun getImmersiveMode(): Boolean {
        return sp.getBoolean("immersive_mode", true)
    }

    fun setImmersiveMode(mode: Boolean) {
        sp.edit().putBoolean("immersive_mode", mode).apply()
    }

    fun setEnv(env: String) {
        sp.edit().putString("env", env).apply()
    }
    fun getEnv(): String {
        return sp.getString("env", "LIBGL_USEVBO=0")?:"LIBGL_USEVBO=0"
    }
    fun getPakVersion(): Int {
        return sp.getInt("pakversion", 24)
    }
    fun setPakVersion(version: Int) {
        sp.setValue("pakversion", version)
    }

    fun getArgv() = sp.getValue("argv", "-console -game csmos")
    fun setArgv(value: String) {
        sp.setValue("argv", value)
    }

    fun getGamePath() = sp.getValue("gamepath", LauncherActivity.getDefaultDir() + "/srceng")
    fun setGamePath(value: String) {
        sp.setValue("gamepath", value)
    }

    fun getNickName() = sp.getValue("nickname", "")
    fun setNickName(value: String) {
        sp.setValue("nickname", value)
    }
    fun getAllowNativeInject() = sp.getValue("allow_native_inject", true)
    fun setAllowNativeInject(value: Boolean) {
        sp.setValue("allow_native_inject", value)
    }

    fun getCurrentCSVersion(): String {
        return sp.getValue("current_cs_version", CSVersionInfoEnum.getDefaultName())
    }
    fun setCurrentCSVersion(value: String) {
        sp.setValue("current_cs_version", value)
    }

}

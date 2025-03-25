package com.billflx.csgo.data.repo

import android.util.Log
import androidx.room.withTransaction
import com.billflx.csgo.bean.CSVersionInfoEnum
import com.billflx.csgo.data.db.CSVersionInfo
import com.billflx.csgo.data.db.CSVersionInfoDao
import com.billflx.csgo.data.db.CSVersionInfoDatabase
import com.billflx.csgo.page.SettingDataBean
import javax.inject.Inject

class CSVersionInfoRepository @Inject constructor(
    private val csVersionInfoDao: CSVersionInfoDao,
    private val db: CSVersionInfoDatabase
) {

    // TODO 是不是可以把 try catch 换成异常处理的 BaseRepository 类方法
    companion object {
        private const val TAG = "CSVersionInfoRepository"
    }

    suspend fun setArgv(versionName: String, argv: String) {
        try {
            val existingInfo = csVersionInfoDao.getVersionInfo(versionName)
            val info = existingInfo.copy(
                versionName = versionName,
                argv = argv
            )
            csVersionInfoDao.updateOrInsertInfo(info)
        } catch (e: Exception) {
            Log.e(TAG, "setArgv: ", e)
        }
    }

    suspend fun getArgv(versionName: String): String {
        try {
            val data = csVersionInfoDao.getArgv(versionName) ?: CSVersionInfoEnum.getDefaultArgsByName(versionName)
            return data
        } catch (e: Exception) {
            Log.e(TAG, "getArgv: ", e)
            return CSVersionInfoEnum.getDefaultArgsByName(versionName)
        }
    }

    suspend fun setGamePath(versionName: String, gamePath: String) {
        try {
            val existingInfo = csVersionInfoDao.getVersionInfo(versionName)
            val info = existingInfo.copy(
                versionName = versionName,
                gamePath = gamePath
            )
            csVersionInfoDao.updateOrInsertInfo(info)
        } catch (e: Exception) {
            Log.e(TAG, "setGamePath: ", e)
        }
    }

    suspend fun getGamePath(versionName: String): String {
        try {
            val data = csVersionInfoDao.getGamePath(versionName) ?: CSVersionInfoEnum.getDefaultGamePathByName(versionName)
            return data
        } catch (e: Exception) {
            Log.e(TAG, "getGamePath: $e")
            return CSVersionInfoEnum.getDefaultGamePathByName(versionName)
        }
    }

    suspend fun setEnv(versionName: String, env: String) {
        try {
            val existingInfo = csVersionInfoDao.getVersionInfo(versionName)
            val info = existingInfo.copy(
                versionName = versionName,
                env = env
            )
            csVersionInfoDao.updateOrInsertInfo(info)
        } catch (e: Exception) {
            Log.e(TAG, "setEnv: ", e)
        }
    }

    suspend fun getEnv(versionName: String): String {
        try {
            val data = csVersionInfoDao.getEnv(versionName) ?: CSVersionInfoEnum.getDefaultEnvByName(versionName)
            return data
        } catch (e: Exception) {
            Log.e(TAG, "getEnv: $e")
            return CSVersionInfoEnum.getDefaultEnvByName(versionName)
        }
    }

    suspend fun setNickName(versionName: String, nickName: String) {
        try {
            val existingInfo = csVersionInfoDao.getVersionInfo(versionName)
            val info = existingInfo.copy(
                versionName = versionName,
                nickName = nickName
            )
            csVersionInfoDao.updateOrInsertInfo(info)
        } catch (e: Exception) {
            Log.e(TAG, "setNickName: ", e)
        }
    }

    suspend fun getNickName(versionName: String): String {
        try {
            val data = csVersionInfoDao.getNickName(versionName) ?: CSVersionInfoEnum.getDefaultNickNameByName(versionName)
            return data
        } catch (e: Exception) {
            Log.e(TAG, "getNickName: $e")
            return CSVersionInfoEnum.getDefaultNickNameByName(versionName)
        }
    }

    suspend fun saveAllData(list: List<SettingDataBean>) {
        try {
            list.forEach {
                val info = CSVersionInfo(
                    versionName = it.versionEnum.name,
                    env = it.env.value,
                    argv = it.argv.value,
                    gamePath = it.gamePath.value,
                    nickName = it.nickName.value
                )
                csVersionInfoDao.updateOrInsertInfo(info)
            }
        } catch (e: Exception) {
            Log.e(TAG, "saveAllData: ", e)
        }
    }

    suspend fun saveData(info: CSVersionInfo) {
        try {
            csVersionInfoDao.updateOrInsertInfo(info)
        } catch (e: Exception) {
            Log.e(TAG, "saveAllData: ", e)
        }
    }

    suspend fun addInfo(info: CSVersionInfo) {
        try {
            csVersionInfoDao.updateOrInsertInfo(info)
        } catch (e: Exception) {
            Log.e(TAG, "addInfo: ", e)
        }
    }

    suspend fun isDBEmpty(): Boolean {
        try {
            val count = csVersionInfoDao.getRowCount()
            if (count == 0)
                return true
            return false
        } catch (e: Exception) {
            Log.e(TAG, "isDBEmpty: ", e)
            return false
        }
    }

    suspend fun insertIfEmpty(csVersionInfoDao: CSVersionInfoDao, versions: List<CSVersionInfo>) {
        runCatching {
            val list = csVersionInfoDao.getAll()
            csVersionInfoDao.clearAll()
            versions.forEach a@ { version ->
                val item = list.find { it.versionName == version.versionName }
                if (item == null) {
                    csVersionInfoDao.upsert(version)
                    return@a
                }
                val clazz = version.javaClass
                clazz.declaredFields.forEach b@ { member ->
                    member.isAccessible = true
                    if (member.name in listOf("env", "argv", "gamePath", "nickName")) {
                        member.set(version, member.get(item))
                    } else {
                        return@b
                    }
                }
                Log.d(TAG, "insertIfEmpty: $version")
                csVersionInfoDao.upsert(version)
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    suspend fun getAll(): List<CSVersionInfo> {
        runCatching {
            return csVersionInfoDao.getAll()
        }.onFailure {
            it.printStackTrace()
        }
        return emptyList()
    }

    suspend fun getByVersionName(versionName: String): CSVersionInfo {
        runCatching {
            return csVersionInfoDao.getVersionInfo(versionName = versionName)
        }.onFailure {
            it.printStackTrace()
        }
        return CSVersionInfo()
    }

}
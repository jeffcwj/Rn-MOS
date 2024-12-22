package com.billflx.csgo.data.repo

import android.util.Log
import com.billflx.csgo.bean.CSVersionInfoEnum
import com.billflx.csgo.data.db.CSVersionInfo
import com.billflx.csgo.data.db.CSVersionInfoDao
import com.billflx.csgo.page.SettingDataBean
import javax.inject.Inject

class CSVersionInfoRepository @Inject constructor(
    private val csVersionInfoDao: CSVersionInfoDao
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
                    nickName = it.nickName.value,
                )
                csVersionInfoDao.updateOrInsertInfo(info)
            }
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

}
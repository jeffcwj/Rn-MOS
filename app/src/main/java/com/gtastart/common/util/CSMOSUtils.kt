package com.gtastart.common.util

import android.util.Log
import com.billflx.csgo.bean.CSVersionInfoEnum
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.db.CSVersionInfo
import com.gtastart.common.util.extend.safeReadLines
import com.gtastart.common.util.extend.safeReadText
import com.gtastart.common.util.extend.safeWriteText
import java.io.File

class CSMOSUtils {

    companion object {
        private const val TAG = "CSMOSUtils"

        fun stringToArgsMap(str: String): LinkedHashMap<String, String> {
            val map: LinkedHashMap<String, String> = LinkedHashMap()
            val stripped = str.trim().replace(Regex("\\s+"), " ")
            var keyTmp = ""
            stripped.split(" ").forEach { item ->
                if (item.startsWith("-")) {
                    keyTmp = item
                    map[keyTmp] = ""
                } else {
                    map[keyTmp] = item
                }
            }
            return map
        }

        fun argsMapToString(map: LinkedHashMap<String, String>): String {
            val fixedMap = fixArgsOrder(map)
            val sb = StringBuilder()
            fixedMap.entries.forEach {
                Log.d(TAG, "argsMapToString: ${it.key}, ${it.value}")
                sb.append(it.key)
                sb.append(" ")
                if (!it.value.isBlank()) {
                    sb.append(it.value)
                    sb.append(" ")
                }
            }
            return sb.toString().trim()
        }

        fun fixArgsOrder(map: LinkedHashMap<String, String>): LinkedHashMap<String, String> {
            val fixedMap: LinkedHashMap<String, String> = LinkedHashMap()
            val suffixMap: LinkedHashMap<String, String>  = LinkedHashMap()
            map.entries.forEach {
                if (it.key == "-game") { // -game参数 要求放在最后面
                    Log.d(TAG, "fixArgsOrder: key: ${it.key}")
                    suffixMap[it.key] = it.value
                } else {
                    fixedMap[it.key] = it.value
                }
            }
            fixedMap.putAll(suffixMap)
            return fixedMap
        }

        fun saveNickName(nickName: String) {
            val version = ModLocalDataSource.getCurrentCSVersion()
            val csType = CSVersionInfoEnum.getCsTypeByName(version)
            val configFile = File(ModLocalDataSource.getGamePath(), String.format(Constants.CONFIG_PATH, csType.lowercase()))
            val configText = configFile.safeReadText()
            val sb = StringBuilder()
            configText.split("\n").forEach { line ->
                if (line.startsWith("name ")) {
                    sb.append("name \"").append(nickName).append("\"\n")
                } else {
                    sb.append(line).append("\n")
                }
            }
            configFile.safeWriteText(sb.toString())
        }

        fun readAutoExecText(): String {
            val version = ModLocalDataSource.getCurrentCSVersion()
            val csType = CSVersionInfoEnum.getCsTypeByName(version)
            val configFile = File(ModLocalDataSource.getGamePath(), String.format(Constants.AUTOEXEC_CONFIG_PATH, csType))
            return configFile.safeReadText()
        }

        fun writeAutoExecText(text: String) {
            val version = ModLocalDataSource.getCurrentCSVersion()
            val csType = CSVersionInfoEnum.getCsTypeByName(version)
            val configFile = File(ModLocalDataSource.getGamePath(), String.format(Constants.AUTOEXEC_CONFIG_PATH, csType))
            configFile.safeWriteText(text)
        }

        fun addCustomAutoExecCmd(cmd: String) {
            val version = ModLocalDataSource.getCurrentCSVersion()
            val csType = CSVersionInfoEnum.getCsTypeByName(version)
            val configFile = File(ModLocalDataSource.getGamePath(), String.format(Constants.AUTOEXEC_CONFIG_PATH, csType))
            val configList = configFile.safeReadLines().toMutableList()
            configList.add(cmd)
            configFile.safeWriteText(configList.joinToString("\n"))
        }

        fun removeCustomAutoExecCmd(cmd: String): List<String> {
            val version = ModLocalDataSource.getCurrentCSVersion()
            val csType = CSVersionInfoEnum.getCsTypeByName(version)
            val configFile = File(ModLocalDataSource.getGamePath(), String.format(Constants.AUTOEXEC_CONFIG_PATH, csType))
            val configLines = configFile.safeReadLines().toMutableList()
            val updatedLines = configLines.filterNot { it.trimStart().startsWith(cmd) }
            configFile.safeWriteText(updatedLines.joinToString("\n"))
            return updatedLines
        }

        fun saveAutoConnectInfo(serverIP: String) {
            removeCustomAutoExecCmd("connect ")
            addCustomAutoExecCmd("connect $serverIP")
        }

        fun removeAutoConnectInfo(): String {
            val list = removeCustomAutoExecCmd("connect ")
            return list.joinToString("\n")
        }

        fun addSvPure() {
            removeCustomAutoExecCmd("sv_pure")
            addCustomAutoExecCmd("sv_pure -1")
        }

        fun removeSvPure() {
            removeCustomAutoExecCmd("sv_pure")
        }

        fun importMapMod(zipPath: String) {

        }

        /**
         * 粗略判断游戏数据是否存在 仅检查关键文件
         */
        fun isCsSourceInstalled(versionName: String): Boolean {
            if (versionName.contains(CSVersionInfoEnum.CSMOSV65.getCsType())) { // csmos
                return checkCSMOSKeyFileExists()
            } else if (versionName.contains(CSVersionInfoEnum.CM.getCsType())) { // cm
                return checkCMKeyFileExists()
            }
            return false
        }

        fun checkCSMOSKeyFileExists(): Boolean {
            val gamePath = ModLocalDataSource.getGamePath()
            val requiredFiles = listOf(
                "csmos/gameinfo.txt",
                "hl2/gameinfo.txt",
                "cstrike/gameinfo.txt",
                "csmos/mos_pak_dir.vpk",
                "hl2/hl2_misc_dir.vpk",
                "cstrike/cstrike_pak_dir.vpk",
                "platform/platform_misc_dir.vpk",
            )
            return requiredFiles.all { filePath ->
                File(gamePath, filePath).exists()
            }
        }
        fun checkCMKeyFileExists(): Boolean {
            val gamePath = ModLocalDataSource.getGamePath()
            val requiredFiles = listOf(
                "cm/gameinfo.txt",
                "hl2/gameinfo.txt",
                "cstrike/gameinfo.txt",
                "cm/clientmod_base/cm_resources_dir.vpk",
                "hl2/hl2_misc_dir.vpk",
                "cstrike/cstrike_pak_dir.vpk",
                "platform/platform_misc_dir.vpk",
            )
            return requiredFiles.all { filePath ->
                File(gamePath, filePath).exists()
            }
        }
    }
}
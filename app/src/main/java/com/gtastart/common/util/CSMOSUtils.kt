package com.gtastart.common.util

import android.util.Log
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
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
            val configFile = File(ModLocalDataSource.getGamePath(), Constants.CONFIG_PATH)
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
            val configFile = File(ModLocalDataSource.getGamePath(), Constants.AUTOEXEC_CONFIG_PATH)
            return configFile.safeReadText()
        }

        fun writeAutoExecText(text: String) {
            val configFile = File(ModLocalDataSource.getGamePath(), Constants.AUTOEXEC_CONFIG_PATH)
            configFile.safeWriteText(text)
        }

        fun addCustomAutoExecCmd(cmd: String) {
            val configFile = File(ModLocalDataSource.getGamePath(), Constants.AUTOEXEC_CONFIG_PATH)
            val configList = configFile.safeReadLines().toMutableList()
            configList.add(cmd)
            configFile.safeWriteText(configList.joinToString("\n"))
        }

        fun removeCustomAutoExecCmd(cmd: String): List<String> {
            val configFile = File(ModLocalDataSource.getGamePath(), Constants.AUTOEXEC_CONFIG_PATH)
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
    }
}
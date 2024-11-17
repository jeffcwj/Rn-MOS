package com.gtastart.common.util

import android.util.Log
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
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
            val sb = StringBuilder()
            map.entries.forEach {
                sb.append(it.key)
                sb.append(" ")
                if (!it.value.isBlank()) {
                    sb.append(it.value)
                    sb.append(" ")
                }
            }
            return sb.toString()
        }

        fun saveNickName(nickName: String) {
            val configFile = File(ModLocalDataSource.getGamePath(), Constants.CONFIG_PATH)
            val configText = configFile.readText()
            val sb = StringBuilder()
            configText.split("\n").forEach { line ->
                if (line.startsWith("name ")) {
                    sb.append("name \"").append(nickName).append("\"\n")
                } else {
                    sb.append(line).append("\n")
                }
            }
            configFile.writeText(sb.toString())
        }

        fun saveAutoConnectInfo(serverIP: String) {
            val configFile = File(ModLocalDataSource.getGamePath(), Constants.AUTOEXEC_CONFIG_PATH)
            configFile.writeText("sv_pure -1\nconnect $serverIP")
        }
    }
}
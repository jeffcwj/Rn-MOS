package com.gtastart.common.util

import android.util.Log

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

        fun saveNickName() {

        }
    }
}
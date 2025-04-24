package com.gtastart.common.util

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.billflx.csgo.bean.CSVersionInfoEnum
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.db.CSVersionInfo
import com.billflx.csgo.page.settings.game.GameSettingViewModel
import com.billflx.csgo.page.settings.game.GameSettingViewModel.Companion
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
            val csType = ModLocalDataSource.getCsType()
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
            val csType = ModLocalDataSource.getCsType()
            val configFile = File(ModLocalDataSource.getGamePath(), String.format(Constants.AUTOEXEC_CONFIG_PATH, csType))
            return configFile.safeReadText()
        }

        fun writeAutoExecText(text: String) {
            val csType = ModLocalDataSource.getCsType()
            val configFile = File(ModLocalDataSource.getGamePath(), String.format(Constants.AUTOEXEC_CONFIG_PATH, csType))
            configFile.safeWriteText(text)
        }

        fun addCustomAutoExecCmd(cmd: String) {
            val csType = ModLocalDataSource.getCsType()
            val configFile = File(ModLocalDataSource.getGamePath(), String.format(Constants.AUTOEXEC_CONFIG_PATH, csType))
            val configList = configFile.safeReadLines().toMutableList()
            configList.add(cmd)
            configFile.safeWriteText(configList.joinToString("\n"))
        }

        fun removeCustomAutoExecCmd(cmd: String): List<String> {
            val csType = ModLocalDataSource.getCsType()
            val gamePath = ModLocalDataSource.getGamePath()
            Log.d(TAG, "removeCustomAutoExecCmd: $gamePath")
            val configFile = File(gamePath, String.format(Constants.AUTOEXEC_CONFIG_PATH, csType))
            val configLines = configFile.safeReadLines().toMutableList()
            Log.d(TAG, "removeCustomAutoExecCmd: $configLines")
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

        fun addCustomMainServers() {
            val servers = Constants.appUpdateInfo.value?.link?.serverRootLink
            if (servers?.size != 0)
                removeCustomAutoExecCmd("addmaster ") // 移除老的
            servers?.forEach {
                val list = addCustomAutoExecCmd("addmaster \"$it\"") // 添加新的
            }
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

        fun addOrEditArgs(argv: String, key: String, value: String): String {
            val map = CSMOSUtils.stringToArgsMap(argv)
            map[key] = value
            val args = CSMOSUtils.argsMapToString(map)
            return args
        }

        fun setResolution(argv: String, width: Int, height: Int): String {
            var args = argv
            args = addOrEditArgs(args, "-w", width.toString())
            args = addOrEditArgs(args, "-h", height.toString())
            return args
        }

        val cliendModBasePaths = listOf(
            "cm",
            "cstrike",
            "hl2",
            "platform",
        )
        val csmosBasePaths = listOf(
            "csmos",
            "cstrike",
            "hl2",
            "platform",
        )

        val csmosKeyPaths = listOf(
            "csmos/cfg",
            "csmos/classes",
            "csmos/materials",
            "csmos/particles",
            "csmos/resource",
            "csmos/scripts",
            "csmos/sound",
            "csmos/gameinfo.txt",
            "csmos/mos_extras_dir.vpk",
            "csmos/mos_extras_000.vpk",
            "csmos/mos_pak_dir.vpk",
            "csmos/mos_pak_000.vpk",
            "cstrike/gameinfo.txt",
            "cstrike/cstrike_pak_000.vpk",
            "cstrike/cstrike_pak_dir.vpk",
            "hl2/gameinfo.txt",
            "hl2/hl2_misc_000.vpk",
            "hl2/hl2_misc_dir.vpk",
            "hl2/hl2_textures_000.vpk",
            "hl2/hl2_textures_dir.vpk",
            "platform/platform_misc_000.vpk",
            "platform/platform_misc_dir.vpk",
        )
        val cmKeyPaths = listOf(
            "cm/cfg",
            "cm/clientmod_base",
            "cm/extras",
            "cm/materials",
            "cm/resource",
            "cm/gameinfo.txt",
            "cstrike/madstray_lox",
            "cstrike/madstray_lox/clientmod_000.vpk",
            "cstrike/madstray_lox/clientmod_dir.vpk",
            "hl2/gameinfo.txt",
            "hl2/hl2_pak_000.vpk",
            "hl2/hl2_pak_dir.vpk",
            "hl2/hl2_misc_000.vpk",
            "hl2/hl2_misc_dir.vpk",
            "hl2/hl2_textures_000.vpk",
            "hl2/hl2_textures_dir.vpk",
            "platform/platform_misc_000.vpk",
            "platform/platform_misc_dir.vpk",
        )
        val csmosAutoExecCfgPath = listOf(
            "csmos/cfg/autoexec.cfg"
        )
        val cmAutoExecCfgPath = listOf(
            "cm/cfg/autoexec.cfg"
        )

        val csmosCheckList = listOf(
            csmosBasePaths,
            csmosKeyPaths,
        )
        val cmCheckList = listOf(
            cliendModBasePaths,
            cmKeyPaths
        )
        suspend fun scanSourceData(gamePath: String, onDataUpdate: (String) -> Unit) {
            fun formatOutput(path: String, isDone: Boolean) = "$path ${if (isDone) "✅" else "❌"}"
            val targetFile = File(gamePath)
            var csmosResult = true
            var cmResult = true
            onDataUpdate("开始检测ClientMod特征")
            cmCheckList.forEach {
                it.forEach {
                    val file = File(targetFile, it)
                    val isExist = file.exists()
                    if (!isExist) cmResult = false
                    onDataUpdate(formatOutput(file.path, isExist))
                }
            }
            onDataUpdate("开始检测CSMOS特征")
            csmosCheckList.forEach {
                it.forEach {
                    val file = File(targetFile, it)
                    val isExist = file.exists()
                    if (!isExist) csmosResult = false
                    onDataUpdate(formatOutput(file.path, isExist))
                }
            }
            // 检测结果
            if (csmosResult) {
                onDataUpdate(formatOutput("检测完毕，CSMOS特征通过", true))
            } else if (cmResult) {
                onDataUpdate(formatOutput("检测完毕，ClientMod特征通过", true))
            } else {
                onDataUpdate(formatOutput("检测完毕，特征检测不通过（暂时仅支持CSMOS和ClientMod特征检测，检测结果仅供参考，具体请运行游戏查看情况）", false))
            }
        }

        fun isAssetExist(context: Context, folderPath: String) =
            AssetsUtils.Companion.listFileNames(
                context = context,
                folderPath = folderPath.removePrefix("/")
            ).isNotEmpty()

        /**
         * @return -1 数据不存在 0 数据正常 2 数据缺失
         */
        fun checkSourceData(path: String): Int {
            val targetFile = File(path)
            val childFiles = targetFile.listFiles()
            if (!targetFile.exists() ||
                targetFile.isFile ||
                childFiles == null ||
                childFiles.isEmpty()
                ) {
                return -1
            }
            return 0
        }

        /**
         * 粗略判断游戏数据是否存在 仅检查关键文件
         */
        @Deprecated("老方法，已不再使用")
        fun isCsSourceInstalled(versionName: String): Boolean {
            if (versionName.contains(CSVersionInfoEnum.getMosDefault().getCsType())) { // csmos
                return checkCSMOSKeyFileExists()
            } else if (versionName.contains(CSVersionInfoEnum.getCmDefault().getCsType())) { // cm
                return checkCMKeyFileExists()
            }
            return false
        }

        private fun checkCSMOSKeyFileExists(): Boolean {
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
        private fun checkCMKeyFileExists(): Boolean {
            val gamePath = ModLocalDataSource.getGamePath()
            val requiredFiles = listOf(
                "cm/gameinfo.txt",
                "hl2/gameinfo.txt",
                "cstrike/gameinfo.txt",
                "cm/clientmod_base/cm_resources_dir.vpk",
                "hl2/hl2_misc_dir.vpk",
                "cstrike/madstray_lox/clientmod_dir.vpk",
                "platform/platform_misc_dir.vpk",
            )
            return requiredFiles.all { filePath ->
                File(gamePath, filePath).exists()
            }
        }
    }
}
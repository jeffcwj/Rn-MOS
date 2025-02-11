package com.billflx.csgo.bean

import com.billflx.csgo.data.ModLocalDataSource
import me.nillerusr.LauncherActivity

enum class CSVersionInfoEnum(
    private val libPath: String,
    private val vpkName: String,
    private val defaultArgs: String,
    private val defaultEnv: String,
    private val defaultGamePath: String,
    private val defaultNickName: String,
    private val csType: String
) {

    CSMOSV65("/libs/CSMOS_v65",
        "extras_dir_CSMOSv65.vpk",
        "-console -game csmos",
        "LIBGL_USEVBO=0",
        LauncherActivity.getDefaultDir() + "/srceng",
        "RnMOS Player",
        "CSMOS"),

    CSMOSV78("/libs/CSMOS_v78",
        "extras_dir_CSMOSv65.vpk", // 共用
        "-console -game csmos",
        "LIBGL_USEVBO=0",
        LauncherActivity.getDefaultDir() + "/srceng",
        "RnMOS Player",
        "CSMOS"),

    /*CMv1("/libs/CM_v1",
        "extras_dir_CM.vpk",
        "-console -game cm",
        "LIBGL_USEVBO=0",
        LauncherActivity.getDefaultDir() + "/CM",
        "ClientMod Player",
        "CM"),*/

    CM("/libs/CM",
        "extras_dir_CM.vpk",
        "-console -game cm",
        "LIBGL_USEVBO=0",
        LauncherActivity.getDefaultDir() + "/CM",
        "ClientMod Player",
        "CM");

    fun getLibPath(): String {
        return libPath
    }

    fun getVpkName(): String {
        return vpkName
    }
    fun getDefaultArgs(): String {
        return defaultArgs
    }
    fun getDefaultEnv(): String {
        return defaultEnv
    }
    fun getDefaultGamePath(): String {
        return defaultGamePath
    }
    fun getDefaultNickName(): String {
        return defaultNickName
    }
    fun getCsType(): String {
        return csType
    }

    override fun toString(): String {
        return name
    }

    companion object {

        fun getDefaultName(): String {
            return CSMOSV65.name
        }
        fun getDefaultLibPath(): String {
            return CSMOSV65.libPath
        }
        fun getDefaultVpkName(): String {
            return CSMOSV65.getVpkName()
        }
        fun getDefaultGamePath(): String {
            return CSMOSV65.getDefaultGamePath()
        }
        fun getDefaultArgs(): String {
            return CSMOSV65.getDefaultArgs()
        }
        fun getDefaultNickName(): String {
            return CSMOSV65.getDefaultNickName()
        }
        fun getDefaultEnv(): String {
            return CSMOSV65.getDefaultEnv()
        }
        fun getCsType(): String {
            return CSMOSV65.csType
        }
        fun getVpkNameByName(name: String): String {
            entries.forEach {
                if (it.name == name) {
                    return it.vpkName
                }
            }
            return getDefaultVpkName()
        }
        fun getLibPathByName(name: String): String {
            entries.forEach {
                if (it.name == name) {
                    return it.libPath
                }
            }
            return getDefaultLibPath()
        }
        fun getCurrentLibPath(): String {
            val version = ModLocalDataSource.getCurrentCSVersion()
            entries.forEach {
                if (it.name == version) {
                    return it.libPath
                }
            }
            return getDefaultLibPath()
        }

        fun getDefaultEnvByName(name: String): String {
            entries.forEach {
                if (it.name == name) {
                    return it.defaultEnv
                }
            }
            return getDefaultEnv()
        }
        fun getDefaultGamePathByName(name: String): String {
            entries.forEach {
                if (it.name == name) {
                    return it.defaultGamePath
                }
            }
            return getDefaultGamePath()
        }
        fun getDefaultArgsByName(name: String): String {
            entries.forEach {
                if (it.name == name) {
                    return it.defaultArgs
                }
            }
            return getDefaultArgs()
        }
        fun getDefaultNickNameByName(name: String): String {
            entries.forEach {
                if (it.name == name) {
                    return it.defaultNickName
                }
            }
            return getDefaultNickName()
        }
        fun getCsTypeByName(name: String): String {
            entries.forEach {
                if (it.name == name) {
                    return it.csType
                }
            }
            return getDefaultNickName()
        }
    }


}
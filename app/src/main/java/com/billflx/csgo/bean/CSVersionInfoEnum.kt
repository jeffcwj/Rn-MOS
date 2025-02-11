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
    CSMOSV79("/libs/CSMOS_v79",
        "extras_dir_CSMOSv65.vpk", // 共用
        "-console -game csmos",
        "LIBGL_USEVBO=0",
        LauncherActivity.getDefaultDir() + "/srceng",
        "RnCS Player",
        "CSMOS"),

    CSMOSV65("/libs/CSMOS_v65",
        "extras_dir_CSMOSv65.vpk",
        "-console -game csmos",
        "LIBGL_USEVBO=0",
        LauncherActivity.getDefaultDir() + "/srceng",
        "RnCS Player",
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

        // 默认MOS版本
        fun getMosDefault() = CSMOSV79
        // 默认CM版本
        fun getCmDefault() = CM

        fun getDefaultName(): String {
            return getMosDefault().name
        }
        fun getDefaultLibPath(): String {
            return getMosDefault().libPath
        }
        fun getDefaultVpkName(): String {
            return getMosDefault().getVpkName()
        }
        fun getDefaultGamePath(): String {
            return getMosDefault().getDefaultGamePath()
        }
        fun getDefaultArgs(): String {
            return getMosDefault().getDefaultArgs()
        }
        fun getDefaultNickName(): String {
            return getMosDefault().getDefaultNickName()
        }
        fun getDefaultEnv(): String {
            return getMosDefault().getDefaultEnv()
        }
        fun getDefaultCsType(): String {
            return getMosDefault().csType
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
package com.billflx.csgo.page

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billflx.csgo.bean.CSVersionInfoEnum
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.repo.CSVersionInfoRepository
import com.gtastart.common.util.CSMOSUtils
import com.valvesoftware.source.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.nillerusr.DirchActivity
import me.nillerusr.LauncherActivity
import javax.inject.Inject
import javax.inject.Singleton

data class SettingDataBean(
    var settingTitle: String,
    var versionEnum: CSVersionInfoEnum,
    var gamePath: MutableState<String>,
    var argv: MutableState<String>,
    var env: MutableState<String>,
    var nickName: MutableState<String>
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val csVersionRepo: CSVersionInfoRepository,
    private val application: Application
) : ViewModel() {

    private val _openDirchActivityEvent = MutableLiveData<String>()
    val openDirchActivityEvent: LiveData<String> = _openDirchActivityEvent

    val options = CSVersionInfoEnum.entries.toTypedArray()
    var selectedOption = mutableStateOf(options[0].name)

    val settingCacheList = mutableStateListOf<SettingDataBean>()
    val settingVersions = listOf("CSMOS ${application.getString(R.string.setting)}",
        "CM ${application.getString(R.string.setting)}")

    init {
        initSettingCache()
//        loadSettings()
    }

    /**
     * 初始化设置缓存（从数据库加载设置数据）
     */
    private fun initSettingCache() {
        settingCacheList.clear()
        applySettingsToModSP(ModLocalDataSource.getCurrentCSVersion())

        viewModelScope.launch {
            // CSMOS
            val csmosName = CSVersionInfoEnum.CSMOSV65.name
            val csmosData = SettingDataBean(
                settingTitle = settingVersions[0],
                versionEnum = CSVersionInfoEnum.CSMOSV65,
                gamePath = mutableStateOf(csVersionRepo.getGamePath(csmosName)),
                argv = mutableStateOf(csVersionRepo.getArgv(csmosName)),
                env = mutableStateOf(csVersionRepo.getEnv(csmosName)),
                nickName = mutableStateOf(csVersionRepo.getNickName(csmosName))
            )
            settingCacheList.add(csmosData)
            // CM
            val cmName = CSVersionInfoEnum.CM.name
            val cmData = SettingDataBean(
                settingTitle = settingVersions[1],
                versionEnum = CSVersionInfoEnum.CM,
                gamePath = mutableStateOf(csVersionRepo.getGamePath(cmName)),
                argv = mutableStateOf(csVersionRepo.getArgv(cmName)),
                env = mutableStateOf(csVersionRepo.getEnv(cmName)),
                nickName = mutableStateOf(csVersionRepo.getNickName(cmName))
            )
            settingCacheList.add(cmData)
        }
    }

    fun saveSettingsToDB() {
        viewModelScope.launch {
            csVersionRepo.saveAllData(list = settingCacheList.toList())
        }
    }

    fun addOrEditArgs(key: String, value: String, argv: MutableState<String>) {
        val map = CSMOSUtils.stringToArgsMap(argv.value)
        map[key] = value
        val args = CSMOSUtils.argsMapToString(map)
        argv.value = args
    }

    fun setResolution(width: Int, height: Int, argv: MutableState<String>) {
        addOrEditArgs("-w", width.toString(), argv)
        addOrEditArgs("-h", height.toString(), argv)
    }

    /**
     * 同步到sharedPref
     */
    fun applySettingsToModSP(versionName: String, isTempApply: Boolean = false) {
        settingCacheList.forEach {
            if (versionName.contains(it.versionEnum.getCsType())) {
                if (!isTempApply) {
                    ModLocalDataSource.setCurrentCSVersion(versionName) // 切换CS版本
                }
                ModLocalDataSource.setArgv(it.argv.value)
                ModLocalDataSource.setEnv(it.env.value)
                ModLocalDataSource.setGamePath(it.gamePath.value)
                return
            }
        }
    }
}
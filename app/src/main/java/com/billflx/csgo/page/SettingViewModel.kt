package com.billflx.csgo.page

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.billflx.csgo.data.ModLocalDataSource
import com.gtastart.common.util.CSMOSUtils
import com.gtastart.common.util.MToast
import dagger.hilt.android.lifecycle.HiltViewModel
import me.nillerusr.DirchActivity
import me.nillerusr.LauncherActivity
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {

    var argv = mutableStateOf("-console -game csmos")
    var env = mutableStateOf("LIBGL_USEVBO=0")
    var gamePath = mutableStateOf(LauncherActivity.getDefaultDir() + "/srceng")

    private val _openDirchActivityEvent = MutableLiveData<String>()
    val openDirchActivityEvent: LiveData<String> = _openDirchActivityEvent

    init {
        loadSettings()
    }

    fun loadSettings() {
        argv.value = ModLocalDataSource.getArgv()
        env.value = ModLocalDataSource.getEnv()
        gamePath.value = ModLocalDataSource.getGamePath()
    }

    fun saveSettings() {
        ModLocalDataSource.setArgv(argv.value)
        ModLocalDataSource.setEnv(env.value)
        ModLocalDataSource.setGamePath(gamePath.value)
    }

    fun addOrEditArgs(key: String, value: String) {
        val map = CSMOSUtils.stringToArgsMap(argv.value)
        map[key] = value
        val args = CSMOSUtils.argsMapToString(map)
        argv.value = args
    }

    fun setResolution(width: Int, height: Int) {
        addOrEditArgs("-w", width.toString())
        addOrEditArgs("-h", height.toString())
    }

    fun resetArgsToDefault() {
        argv.value = "-console -game csmos"
    }

    fun handleActivityResult(data: Intent?) {
        gamePath.value = ModLocalDataSource.getGamePath()
    }

    fun toast(context: Context) {
        context.MToast("caoniam ")
    }

    fun startDirchActivity() {
        _openDirchActivityEvent.value = "傻逼"
    }

}
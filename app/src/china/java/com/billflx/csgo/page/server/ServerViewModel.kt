package com.billflx.csgo.page.server

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billflx.csgo.bean.AutoExecCmdBean
import com.billflx.csgo.bean.SampQueryInfoBean
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.db.CSVersionInfo
import com.billflx.csgo.data.repo.AppRepository
import com.billflx.csgo.data.repo.CSVersionInfoRepository
import com.gtastart.common.util.CSMOSUtils
import com.gtastart.common.util.CsMosQuery
import com.gtastart.common.util.CsPayload
import com.gtastart.common.util.MToast
import com.gtastart.common.util.isBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val app: Application,
    private val repository: AppRepository,
    private val versionRepository: CSVersionInfoRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ServerViewModel"
    }

    var serverInfoList = listOf(
        mutableStateListOf<SampQueryInfoBean>(),
        mutableStateListOf(),
        mutableStateListOf()
    )
    var isRefreshing = mutableStateOf(false)
    private var refreshJob: Job? = null

    var nickName = mutableStateOf("RnCS Player")
    var password = mutableStateOf("")

    var autoExecCmdList = mutableStateListOf<AutoExecCmdBean>()
    var isAutoExecCmdLoading = mutableStateOf(false)

    var serverPayload = mutableStateOf(CsPayload.CSMOS.payload)
    var serverCsType = mutableStateOf(CsPayload.CSMOS.csType)

    val versionList = mutableStateListOf<CSVersionInfo>()
    val currentVersion = mutableStateOf(
        ModLocalDataSource.getCurrentCSVersion()
    )

    init {
        viewModelScope.launch {
            getExistVersion()
        }
    }

    fun loadNickName() {
        /*var version = CSVersionInfoEnum.getMosDefault().name
        if (serverPayload.value == CsPayload.CSMOS.payload) {
            version = CSVersionInfoEnum.getMosDefault().name
        } else {
            version = CSVersionInfoEnum.getCmDefault().name
        }*/
        viewModelScope.launch {
            nickName.value = ModLocalDataSource.getNickName()//versionRepository.getNickName(version)
        }
    }

    fun saveNickName(): Boolean {
        if (!nickName.value.isBlank()) {
            viewModelScope.launch {
                // versionRepository.setNickName(ModLocalDataSource.getCurrentCSVersion(), nickName.value)
                withContext(Dispatchers.Main) {
                    ModLocalDataSource.setNickName(nickName.value)
                }
            }
        } else {
            return false
        }
        return true
    }

    suspend fun getServerIPList(rootLink: String, maxRetryCount: Int = 3): List<String> {
        var retryCount = 0
        val (host, port) = rootLink.split(":").let { it[0] to it[1].toInt() }
        while (retryCount < maxRetryCount) {
            val samp = CsMosQuery(host, port)
            samp.setPayload(serverPayload.value)
            val ips = samp.serverIps
            ips?.let {
                if (ips.size != 0) {
                    Log.d(TAG, "主服务器${rootLink}: 的子ip ${ips}")
                    return ips
                } else {
                    Log.d(TAG, "主服务器${rootLink} 获取游戏服务器失败")
                }
            }
            delay(1000)
            retryCount++
        }
        return emptyList()
    }

    suspend fun getServerInfos(host: String, port: Int, maxRetryCount: Int = 5): SampQueryInfoBean {
        var retryCount = 0
        while (retryCount < maxRetryCount) {
            val samp = CsMosQuery(host, port)
            samp.setPayload(serverPayload.value)
            val infos = samp.infos
            if (!infos.serverName.isNullOrBlank()) {
                return infos
            }
            delay(1000)
            retryCount++
        }
        return SampQueryInfoBean()
    }

    fun refreshServerList(index: Int) {
        Log.d(TAG, "refreshServerList: 开始刷新")
        refreshJob?.let {
            if (it.isActive) {
                it.cancel() // 取消当前正在运行的任务
                isRefreshing.value = false // 设置刷新状态为否
            }
        } ?: also {
            isRefreshing.value = false // 没在刷新
        }
        if (!isRefreshing.value) {
            isRefreshing.value = true
            // serverInfoList[index].clear() // 清除列表
            refreshJob = viewModelScope.launch(Dispatchers.IO) {
                var isListClear = false
                // listOf("cs.samp.fun:27010", "oreo922.cn:27010", "135.125.188.162:27010")
                Constants.appUpdateInfo.value?.link?.serverRootLink?.let { rootLinks ->
                    if (rootLinks.isEmpty()) isRefreshing.value = false
                    rootLinks.forEach { rootLink ->
                        launch {
                            val ipList = getServerIPList(rootLink)
                            if (ipList.isEmpty()) isRefreshing.value = false
                            withContext(Dispatchers.Main) {
                                if (!isListClear) {
                                    serverInfoList[index].clear() // 清除列表
                                    isListClear = true
                                }
                            }
                            ipList.forEach { ip ->
                                launch {
                                    val (host, port) = ip.split(":").let { it[0] to it[1].toInt() }
                                    val infos = getServerInfos(host, port)
                                    withContext(Dispatchers.Main) {
                                        if (serverInfoList[index].none { it.serverIP == infos.serverIP } && // 避免重复添加
                                            !infos.serverName.isNullOrBlank()) { // 避免获取空包
                                            serverInfoList[index].add(infos)
                                            serverInfoList[index].sortByDescending { it.players } // 按照玩家数量降序排序
                                        }
                                    }
                                }
                            }
                            withContext(Dispatchers.Main) {
                                if (ipList.isNotEmpty()) {
                                    isRefreshing.value = false
                                }
                            }
                        }
                    }
                } ?: also {
                    isRefreshing.value = false // 还没检测完更新
                }
            }
        }
    }

    suspend fun getAutoExecCmds() {
        isAutoExecCmdLoading.value = true
        autoExecCmdList.clear()
        viewModelScope.launch {
            try {
                val cmds = repository.getAutoExecCmds()
                cmds.forEach { autoExecCmdList.add(it) }
                Log.d(TAG, "getAutoExecCmds: $cmds")
                isAutoExecCmdLoading.value = false
            } catch (e: Exception) {
                isAutoExecCmdLoading.value = false
                Log.d(TAG, "getAutoExecCmds: $e")
            }
        }
    }

    suspend fun getLocalVersion(): List<CSVersionInfo> {
        val versions = versionRepository.getAll()
        return versions
    }

    suspend fun getExistVersion(): List<CSVersionInfo> {
        val versions = getLocalVersion()
        val localVersion = versions.filter { isVersionExist(it.versionName.orEmpty()) }
        versionList.clear()
        versionList.addAll(localVersion)
        return localVersion
    }

    suspend fun applySettingsToModSpNew() {
        val versionName = ModLocalDataSource.getCurrentCSVersion()
        val infos = versionRepository.getByVersionName(versionName)
        Log.d(TAG, "applySettingsToModSpNew: $infos")
        infos?.let { info ->
            ModLocalDataSource.setCurrentVpk(info.vpkName.orEmpty())
            ModLocalDataSource.setArgv(info.argv.orEmpty())
            ModLocalDataSource.setEnv(info.env.orEmpty())
            ModLocalDataSource.setGamePath(info.gamePath.orEmpty())
            ModLocalDataSource.setCurrentLibPath(info.libPath.orEmpty())
            ModLocalDataSource.setCsType(info.csType.orEmpty())
        } ?: {
            app.MToast("数据保存失败")
        }
    }

    suspend fun isCurrentVersionExist(): Boolean {
        val currentVersion = ModLocalDataSource.getCurrentCSVersion()
        return isVersionExist(currentVersion)
    }

    suspend fun getVersionForShow(versionName: String): String {
        return getLocalVersion().firstOrNull { it.versionName == versionName }?.versionNameForShow?:versionName
    }

    /**
     * 版本是否存在
     * 2025/04/22 更新：检测内置版本
     */
    suspend fun isVersionExist(versionName: String): Boolean {
        val infos = getLocalVersion().firstOrNull { it.versionName == versionName }
        if (infos != null) {
            val version = infos
            if (CSMOSUtils.isAssetExist(app, version.libPath.orEmpty())) {
                return true // 检测为内置动态库版本，直接判断存在
            }
            val parentPath = app.filesDir.path + version.libPath
            version.fileList?.forEach {
                val path = parentPath + File.separator + it.fileName
                Log.d(TAG, "isVersionExist: $path")
                if (!File(path).exists()) {
                    return false
                }
            }
        } else {
            return false
        }
        return true
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ServerViewModel被清理")
    }
}
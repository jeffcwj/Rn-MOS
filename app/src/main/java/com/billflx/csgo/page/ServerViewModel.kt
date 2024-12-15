package com.billflx.csgo.page

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billflx.csgo.bean.AppUpdateBean
import com.billflx.csgo.bean.AutoExecCmdBean
import com.billflx.csgo.bean.SampQueryInfoBean
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.repo.AppRepository
import com.billflx.csgo.page.MainViewModel.Companion
import com.gtastart.common.util.Coroutines
import com.gtastart.common.util.CsMosQuery
import com.gtastart.common.util.isBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ServerViewModel"
    }

    var serverInfoList = mutableStateListOf<SampQueryInfoBean>()
    var isRefreshing = mutableStateOf(false)
    private var refreshJob: Job? = null

    var nickName = mutableStateOf("RnMOS Player")

    var autoExecCmdList = mutableStateListOf<AutoExecCmdBean>()
    var isAutoExecCmdLoading = mutableStateOf(false)


    init {
//        refreshServerList()
        val name = ModLocalDataSource.getNickName()
        if (!name.isBlank()) {
            nickName.value = name
        }
    }

    fun saveNickName(): Boolean {
        if (!nickName.value.isBlank()) {
            ModLocalDataSource.setNickName(nickName.value)
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
            val ips = samp.serverIps
            if (ips.size != 0) {
                Log.d(TAG, "主服务器${rootLink}: 的子ip ${ips}")
                return ips
            } else {
                Log.d(TAG, "主服务器${rootLink} 获取游戏服务器失败")
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
            val infos = samp.infos
            if (!infos.serverName.isNullOrBlank()) {
                return infos
            }
            delay(1000)
            retryCount++
        }
        return SampQueryInfoBean()
    }

    fun refreshServerList() {
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
            serverInfoList.clear() // 清除列表
            refreshJob = viewModelScope.launch(Dispatchers.IO) {
                Constants.appUpdateInfo.value?.link?.serverRootLink?.let { rootLinks ->
                    if (rootLinks.isEmpty()) isRefreshing.value = false
                    rootLinks.forEach { rootLink ->
                        launch {
                            val ipList = getServerIPList(rootLink)
                            if (ipList.isEmpty()) isRefreshing.value = false
                            ipList.forEach { ip ->
                                launch {
                                    val (host, port) = ip.split(":").let { it[0] to it[1].toInt() }
                                    val infos = getServerInfos(host, port)
                                    withContext(Dispatchers.Main) {
                                        if (serverInfoList.none { it.serverIP == infos.serverIP } && // 避免重复添加
                                            !infos.serverName.isNullOrBlank()) { // 避免获取空包
                                            serverInfoList.add(infos)
                                            serverInfoList.sortByDescending { it.players } // 按照玩家数量降序排序
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

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: 别说真被清理了吧")
    }
}
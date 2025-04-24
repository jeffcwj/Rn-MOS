package com.billflx.csgo.page.server

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billflx.csgo.bean.SampQueryInfoBean
import com.billflx.csgo.constant.Constants
import com.gtastart.common.util.CsMosQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val app: Application,
) : ViewModel() {

    companion object {
        private const val TAG = "ServerViewModel"
    }

    private val _serverInfoList = MutableLiveData<List<SampQueryInfoBean>>()
    val serverInfoList: LiveData<List<SampQueryInfoBean>> = _serverInfoList

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private var refreshJob: Job? = null

    private val _nickName = MutableLiveData<String>()
    val nickName: LiveData<String> = _nickName

    init {
        _serverInfoList.value = emptyList()
        _isRefreshing.value = false
        _nickName.value = "RnCS Player"
    }

    fun refreshServerList() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _isRefreshing.value = true
                _serverInfoList.value = emptyList()
            }
            Log.d(TAG, "refreshServerList: ${Constants.appUpdateInfo.value?.link?.serverRootLink}")
            Constants.appUpdateInfo.value?.link?.serverRootLink?.let { rootLinks ->
                if (rootLinks.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        _isRefreshing.value = false
                    }
                    return@launch
                }
                rootLinks.forEach { rootLink ->
                    launch {
                        val ipList = getServerIPList(rootLink)
                        if (ipList.isEmpty()) {
                            withContext(Dispatchers.Main) {
                                _isRefreshing.value = false
                            }
                            return@launch
                        }
                        ipList.forEach { ip ->
                            launch {
                                val (host, port) = ip.split(":").let { it[0] to it[1].toInt() }
                                val infos = getServerInfos(host, port)
                                withContext(Dispatchers.Main) {
                                    val currentList = _serverInfoList.value?.toMutableList() ?: mutableListOf()
                                    if (currentList.none { it.serverIP == infos.serverIP } && 
                                        !infos.serverName.isNullOrBlank()) {
                                        currentList.add(infos)
                                        currentList.sortByDescending { it.players }
                                        _serverInfoList.value = currentList
                                    }
                                }
                            }
                        }
                        withContext(Dispatchers.Main) {
                            if (ipList.isNotEmpty()) {
                                _isRefreshing.value = false
                            }
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                _isRefreshing.value = false
            }
        }
    }

    suspend fun getServerIPList(rootLink: String, maxRetryCount: Int = 3): List<String> {
        var retryCount = 0
        val (host, port) = rootLink.split(":").let { it[0] to it[1].toInt() }
        while (retryCount < maxRetryCount) {
            try {
                val samp = CsMosQuery(host, port)
                val ips = samp.getServerIps()
                ips?.let {
                    if (ips.isNotEmpty()) {
                        Log.d(TAG, "主服务器${rootLink}: 的子ip ${ips}")
                        return ips
                    } else {
                        Log.d(TAG, "主服务器${rootLink} 获取游戏服务器失败")
                    }
                }
                delay(1000)
                retryCount++
            } catch (e: Exception) {
                Log.e(TAG, "获取服务器列表失败", e)
                delay(1000)
                retryCount++
            }
        }
        return emptyList()
    }

    suspend fun getServerInfos(host: String, port: Int, maxRetryCount: Int = 5): SampQueryInfoBean {
        var retryCount = 0
        while (retryCount < maxRetryCount) {
            try {
                val samp = CsMosQuery(host, port)
                val infos = samp.getInfos()
                if (infos != null && !infos.serverName.isNullOrBlank()) {
                    return infos
                }
                delay(1000)
                retryCount++
            } catch (e: Exception) {
                Log.e(TAG, "获取服务器信息失败", e)
                delay(1000)
                retryCount++
            }
        }
        return SampQueryInfoBean()
    }

    fun setNickName(name: String) {
        _nickName.value = name
    }

    fun saveNickName(): Boolean {
        if (nickName.value?.isNotBlank() == true) {
            return true
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: 别说真被清理了吧")
    }
} 
package com.billflx.csgo.page

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billflx.csgo.bean.SampQueryInfoBean
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.gtastart.common.util.Coroutines
import com.gtastart.common.util.CsMosQuery
import com.gtastart.common.util.isBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val TAG = "ServerViewModel"
    }

    var serverInfoList = mutableStateListOf<SampQueryInfoBean>()
    var isRefreshing = mutableStateOf(false)
    var nickName = mutableStateOf("CSMOS New Player")

    init {
        refreshServerList()
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

    suspend fun getServerIPList(rootLink: String): List<String> {
        val (host, port) = rootLink.split(":").let { it[0] to it[1].toInt() }
        val samp = CsMosQuery(host, port)
        val ips = samp.serverIps
        return ips
    }

    suspend fun getServerInfos(host: String, port: Int): SampQueryInfoBean {
        val samp = CsMosQuery(host, port)
        val infos = samp.infos
        return infos
    }

    fun refreshServerList() {
        if (!isRefreshing.value) {
            isRefreshing.value = true
            serverInfoList.clear() // 清除列表
            viewModelScope.launch(Dispatchers.IO) {
                Constants.appUpdateInfo.value?.link?.serverRootLink?.let { rootLinks ->
                    rootLinks.forEach { rootLink ->
                        val ipList = getServerIPList(rootLink)
                        ipList.forEach { ip ->
                            launch {
                                val (host, port) = ip.split(":").let { it[0] to it[1].toInt() }
                                val infos = getServerInfos(host, port)
                                withContext(Dispatchers.Main) {
                                    if (serverInfoList.none { it.serverIP == infos.serverIP }) { // 避免重复添加
                                        serverInfoList.add(infos)
                                        serverInfoList.sortByDescending { it.players } // 按照玩家数量排序
                                    }
                                }
                            }
                        }
                        withContext(Dispatchers.Main) {
                            isRefreshing.value = false
                        }
                    }
                }
            }
        }
    }
}
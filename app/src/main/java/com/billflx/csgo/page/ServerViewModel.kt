package com.billflx.csgo.page

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.billflx.csgo.bean.SampQueryInfoBean
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.gtastart.common.util.Coroutines
import com.gtastart.common.util.CsMosQuery
import com.gtastart.common.util.isBlank
import dagger.hilt.android.lifecycle.HiltViewModel
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

    suspend fun getServerIPList(): List<String> {
        val samp = CsMosQuery(
            Constants.SOURCE_HOST,
            Constants.SOURCE_PORT
        )
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
            serverInfoList.clear()
            Coroutines.ioThenMain(
                work = {
                    val ipList = getServerIPList()
                    ipList.forEach { ip ->
                        val host = ip.split(":")[0]
                        val port = ip.split(":")[1]
                        val infos = getServerInfos(host, port.toInt())
                        serverInfoList.add(infos)
                    }
                }, callback = {
                    isRefreshing.value = false
                }
            )
        }
    }
}
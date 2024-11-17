package com.billflx.csgo.bean

data class SampQueryInfoBean(
    var serverIP: String? = null,
    var serverName: String? = null,
    var players: Int = 0,
    var maxPlayers: Int = 0,
    var ping: Long = 0,
    var robotCount: Int = 0,
    var serverMap: String? = null

) {
    var playerCountInfo: String? = "$players / $maxPlayers"
}
//
// Created by 33688 on 2024/12/4.
//

#ifndef RNMOS_ADDRESSES_H
#define RNMOS_ADDRESSES_H
#include "main.h"

extern Java* g_java;

/*static char supportedVersions[][16] {
    "v6.5",
    "v7.5",
};*/

class CSVersion {
public:
    static inline std::string CM = "CM";
    static inline std::string CSMOSV65 = "CSMOSV65";
    static inline std::string CSMOSV75 = "CSMOSV75";
    static inline std::string CSMOSV77 = "CSMOSV77";
    static inline std::string CSMOSV78 = "CSMOSV78";
    static inline std::string CSMOSV80 = "CSMOSV80";
    static inline std::string CSSOV1 = "CSSOV1";
};

class AddressManager {
public:
    static AddressManager& instance() {
        static AddressManager instance;
        return instance;
    }

    bool initialize() {
        std::string flavor = g_java->getFlavor();
        spdlog::info("CSMOS version: {}", flavor);

        if (flavor == CSVersion::CSMOSV65) {
            // engine
            VMT_Master = 0x970EE0;
            FUNC_NET_StringToAdr = 0x5B3B0C;
            FUNC_CMaster_AddServer = 0x620CC4;
            FUNC_CMaster_RequestInternetServerList = 0x6210EC;
            FUNC_GetSteamInfIDVersionInfo = 0x5FAAFC;

            // GameUI
            FUNC_AddUrlButton = 0x48D550;
            FUNC_CBasePanel = 0x48D798;

            // ServerBrowser
            FUNC_CDialogGameInfo_Run = 0x1DA6A8;
            FUNC_CDialogGameInfo_ConnectToServer = 0x1DAEC0;
            FUNC_ServerResponded = 0x1D2284;
            return true;
        } else if (flavor == CSVersion::CSMOSV75) {
            // engine
            VMT_Master = 0x971480; // ok
            FUNC_NET_StringToAdr = 0x5B3CB0; // ok
            FUNC_CMaster_AddServer = 0x620E90; // ok
            FUNC_CMaster_RequestInternetServerList = 0x6212E0; // ok

            // GameUI
            FUNC_AddUrlButton = 0x48E0E8; // ok
            FUNC_CBasePanel = 0x48E488; // ok

            // ServerBrowser 懒得支持v7.5了
            return true;
        } else if (flavor == CSVersion::CSMOSV78) {
            // engine
            FUNC_GetSteamInfIDVersionInfo = 0x626C10;
            return true;
        }  else if (flavor == CSVersion::CSMOSV80) {
            // engine
            FUNC_NET_Init = 0x5E4CE0; // string: Found -NoQueuedPacketThread
            FUNC_CGameServer_Init = 0x53E758; // string: m_FullSendTables
            FUNC_CSteam3Server_Activate = 0x5345F8; // string: -steamport
            FUNC_CMaster_SendHeartbeat = 0x6526B4; // string: Master Join或unexpected master server info,然后在上下函数的伪代码里找带 < 15.0 的就是
            FUNC_NET_StringToAdr = 0x5DE704; // string: localhost:
            STR_listenserver = 0x3CE0E0; // string: connect localhost:%d listenserver

            return true;
        } else if (flavor == CSVersion::CSSOV1) {
            // engine
            FUNC_NET_Init = 0x5E3338;
            FUNC_CGameServer_Init = 0x54353C;
            FUNC_CSteam3Server_Activate = 0x5393CC;
            FUNC_CMaster_SendHeartbeat = 0x64A940;
            FUNC_NET_StringToAdr = 0x5DD974;
            STR_listenserver = 0x3B555F;
            return true;
        } else {
            return false;
        }
    }

    static inline bool bDedicated = false; // 是否将客户端的listenserver改为服务端的server


    static inline uintptr_t VMT_Master = 0;
    static inline uintptr_t FUNC_NET_StringToAdr = 0;
    static inline uintptr_t FUNC_CMaster_AddServer = 0;
    static inline uintptr_t FUNC_CMaster_RequestInternetServerList = 0;
    static inline uintptr_t FUNC_GetSteamInfIDVersionInfo = 0;
    static inline uintptr_t FUNC_NET_Init = 0;
    static inline uintptr_t FUNC_CGameServer_Init = 0;
    static inline uintptr_t FUNC_CSteam3Server_Activate = 0;
    static inline uintptr_t FUNC_CMaster_SendHeartbeat = 0;
    static inline uintptr_t STR_listenserver = 0;

    static inline uintptr_t FUNC_AddUrlButton = 0;
    static inline uintptr_t FUNC_CBasePanel = 0;

    static inline uintptr_t FUNC_CDialogGameInfo_Run = 0;
    static inline uintptr_t FUNC_CDialogGameInfo_ConnectToServer = 0;
    static inline uintptr_t FUNC_ServerResponded = 0;

private:
    AddressManager() = default;

};

#define g_Addr AddressManager::instance()
#define Addr AddressManager
#endif //RNMOS_ADDRESSES_H

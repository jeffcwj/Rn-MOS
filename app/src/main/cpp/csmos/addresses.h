//
// Created by 33688 on 2024/12/4.
//

#ifndef RNMOS_ADDRESSES_H
#define RNMOS_ADDRESSES_H
#include "main.h"

extern Java* g_java;

static char supportedVersions[][16] {
    "v6.5"
};

class AddressManager {
public:
    static AddressManager& instance() {
        static AddressManager instance;
        return instance;
    }

    bool initialize() {
        std::string flavor = g_java->getFlavor();

        if (flavor == "v6.5") {
            // engine
            VMT_Master = 0x970EE0;
            FUNC_NET_StringToAdr = 0x5B3B0C;
            FUNC_CMaster_AddServer = 0x620CC4;
            FUNC_CMaster_RequestInternetServerList = 0x6210EC;

            // GameUI
            FUNC_AddUrlButton = 0x48D550;
            FUNC_CBasePanel = 0x48D798;
            return true;
        } else {
            return false;
        }
    }

    static inline uintptr_t VMT_Master = 0;
    static inline uintptr_t FUNC_NET_StringToAdr = 0;
    static inline uintptr_t FUNC_CMaster_AddServer = 0;
    static inline uintptr_t FUNC_CMaster_RequestInternetServerList = 0;
    static inline uintptr_t FUNC_AddUrlButton = 0;
    static inline uintptr_t FUNC_CBasePanel = 0;

private:
    AddressManager() = default;

};

#define g_Addr AddressManager::instance()
#define Addr AddressManager
#endif //RNMOS_ADDRESSES_H

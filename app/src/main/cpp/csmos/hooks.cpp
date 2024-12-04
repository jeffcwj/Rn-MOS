//
// Created by 33688 on 2024/12/3.
//
#include "main.h"
#include "structs.h"

DECL_HOOK(bool, NET_StringToAdr, const char* ip, netadr_t *adr) {
    uintptr_t dwRetAddr = 0;
    __asm__ volatile("mov %0, lr" : "=r"(dwRetAddr)); // 获取调用处的返回地址
    dwRetAddr = g_libEngine->ToLibAddr(dwRetAddr);
    spdlog::info("hooking NET_StringToAdr... from {:#x}", dwRetAddr);
    spdlog::info("NET_StringToAdr... ip: {} or addr of ip: {:#x}", ip, g_libEngine->ToLibAddr((uintptr_t)ip));
    return NET_StringToAdr(ip, adr);
}

/**
 * 添加主服务器，这个函数钩子勾不住的，包括CMaster::UseDefault, CMaster::Init 等都是静态初始化
 * @param thiz
 * @param adr
 */
DECL_HOOK(void, CMaster_AddServer, uintptr_t thiz, netadr_t *adr) {
    LOGD("hooking CMaster_AddServer");
    spdlog::info("hooking CMaster_AddServer... from {}.{}.{}.{}:{}",
                 (int)adr->ip[0], (int)adr->ip[1], (int)adr->ip[2], (int)adr->ip[3], adr->port);
    CMaster_AddServer(thiz, adr);
}

/**
 * 请求加载服务器列表
 * @param thiz
 * @param gamedir 命令行 -game 后面的参数 一般是csmos
 * @param response
 */
static bool masterServersAdded = false;
DECL_HOOK(void, CMaster_RequestInternetServerList, uintptr_t thiz, const char *gamedir, void *response) {
    spdlog::info("hooking CMaster_RequestInternetServerList... gamedir: {}", gamedir);

    if (!masterServersAdded) {
        netadr_t adr;
        std::vector<std::string> servers = g_java->getMasterServers();
        for( int i = 0; i < servers.size(); i++ )
        {
            // Convert to netadr_t
            if (NET_StringToAdr(servers[i].c_str(), &adr)) // ip字符串转adr对象
            {
                spdlog::info("extra master server added: {}", servers[i]);
                // Add to master list
                g_libEngine->CallVmtFunction(g_libEngine->Abs(Addr::VMT_Master), 6, thiz, &adr);
            }
        }
        masterServersAdded = true;
    }

    CMaster_RequestInternetServerList(thiz, gamedir, response);
}

/**
 * 主页面板UI，又是没法勾住 啊啊真麻烦
 * @param thiz
 */
DECL_HOOK(void, CBasePanel, uintptr_t thiz) {
    LOGD("hooking CBasePanel...");
    CBasePanel(thiz);
    g_libGameUI->CallFunction(g_libGameUI->Abs(Addr::FUNC_AddUrlButton), thiz,
                              "vgui/qq_logo",
                              "https://qm.qq.com/q/D1ZjNyko8g");
}

/**
 * 添加Url按钮
 * @param parent
 * @param imgName
 * @param url
 */
static bool isFirstCallAddUrlButton = true;
DECL_HOOK(void, AddUrlButton, void *parent, const char *imgName, const char *url ) {
    LOGD("hooking AddUrlButton...");
    if (isFirstCallAddUrlButton) {
        LOGD("Adding UrlButton..."); // I will remove all of them for tidy
/*        AddUrlButton(parent, "vgui/qq_logo", "https://qm.qq.com/q/DIf4WwMZX2"); // CNSR主群
        AddUrlButton(parent, "vgui/discord_logo", "https://discord.gg/mfsNXfua9w"); // Discord
        AddUrlButton(parent, "vgui/rn_logo", "https://qm.qq.com/q/D1ZjNyko8g"); // RnMOS群*/
        isFirstCallAddUrlButton = false;
    }
}

void installHooks() {
    spdlog::info("Installing global hooks...");

    // engine
    HOOK_ENGINE_ADDR(Addr::FUNC_NET_StringToAdr, NET_StringToAdr); // ip字符串转adr对象
    HOOK_ENGINE_ADDR(Addr::FUNC_CMaster_AddServer, CMaster_AddServer); // 添加主服务器
    HOOK_ENGINE_ADDR(Addr::FUNC_CMaster_RequestInternetServerList, CMaster_RequestInternetServerList); // 加载服务器列表

    // GameUI
    HOOK_GAMEUI_ADDR(Addr::FUNC_CBasePanel, CBasePanel); // UI面板构造函数
    HOOK_GAMEUI_ADDR(Addr::FUNC_AddUrlButton, AddUrlButton); // 添加URL按钮
}

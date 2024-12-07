//
// Created by 33688 on 2024/12/3.
//
#include "main.h"
#include "structs.h"

std::string ipToString(uint32_t ip) {
    return std::to_string((ip >> 24) & 0xFF) + "." +
           std::to_string((ip >> 16) & 0xFF) + "." +
           std::to_string((ip >> 8) & 0xFF) + "." +
           std::to_string(ip & 0xFF);
}

std::string ipToString(const char* ip) {
    return std::to_string(ip[0] & 0xFF) + "." +
           std::to_string(ip[1] & 0xFF) + "." +
           std::to_string(ip[2] & 0xFF) + "." +
           std::to_string(ip[3] & 0xFF);
}

unsigned short portToBigEndian(unsigned short port) {
    return (unsigned short)((port >> 8) | (port << 8));
}

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


/**
 * CDialogServerPassword 类构造
 * @param parent
 * @return
 */
DECL_HOOK(void*, CDialogServerPassword, void* parent) {
    spdlog::info("hooking CDialogServerPassword...");
    return CDialogServerPassword(parent);
}

/**
 * CDialogServerPassword::Activate 显示密码窗口
 * @param thiz
 * @param serverName
 * @param serverID
 * @return
 */
DECL_HOOK(void*, CDialogServerPassword_Activate, void* thiz, const char *serverName, unsigned int serverID) {
    spdlog::info("hooking CDialogServerPassword::Activate...");
    return CDialogServerPassword_Activate(thiz, serverName, serverID);
}

/**
 * 服务器返回的内容
 * @param thiz
 * @param server
 * @return
 */
static std::unordered_map<std::string, bool> hasPasswordMap;
DECL_HOOK(void*, ServerResponded, void* thiz, newgameserver_t &server) {
    spdlog::info("hooking ServerResponded...");
    char* host = (char*)server.m_NetAdr.ip;
    unsigned short port = portToBigEndian(server.m_NetAdr.port);
    bool hasPassword = server.m_bPassword;
    std::string ip = ipToString(host) + ":" + std::to_string(port);
    hasPasswordMap.insert_or_assign(ip, hasPassword);
    spdlog::info("gamepath: {}, hostname: {}, ip: {}, hasPassword: {}",
                 server.m_szGameDir, server.m_szServerName, ip, hasPassword);
    return ServerResponded(thiz, server);
}

// 废
DECL_HOOK(void*, CDialogGameInfo_ServerResponded, void* thiz, gameserveritem_t &server) {
    spdlog::info("hooking CDialogGameInfo_ServerResponded...");
    spdlog::info("gamepath: {}, hostname: {}", server.m_szGameDir, server.m_szServerName);
    return CDialogGameInfo_ServerResponded(thiz, server);
}
// 废
DECL_HOOK(void*, CBaseGamesPage_OnBeginConnect, void* thiz) {
    spdlog::info("hooking CBaseGamesPage_OnBeginConnect...");
    return CBaseGamesPage_OnBeginConnect(thiz);
}

// 废
DECL_HOOK(void*, CDialogGameInfo_ChangeGame, void* thiz, int serverIP, int queryPort, unsigned short connectionPort ) {
    spdlog::info("hooking CDialogGameInfo_ChangeGame...");
    return CDialogGameInfo_ChangeGame(thiz, serverIP, queryPort, connectionPort);
}

/**
 * 服务器详情窗口 设置内容
 * @param thiz
 */
DECL_HOOK(void, CDialogGameInfo_PerformLayout, void* thiz) {
    spdlog::info("hooking CDialogGameInfo_PerformLayout...");
    CDialogGameInfo_PerformLayout(thiz);
}

/**
 * 弹出服务器详情窗口
 * @param thiz
 * @param titleName
 */
DECL_HOOK(void, CDialogGameInfo_Run, void* thiz, const char *titleName) {
    spdlog::info("hooking CDialogGameInfo_Run...");
//    Memory::nop(g_libServerBrowser->Abs(0x1DAA48), 2);
//    CDialogGameInfo_Run(thiz, titleName); // 避免弹出详情
}

/**
 * 点击连接按钮 执行的事件
 * @param thiz
 */
static void* cDialogGameInfoInstance = nullptr;
DECL_HOOK(void, CDialogGameInfo_ConnectToServer, void* thiz) {
    // 谁调用了
    cDialogGameInfoInstance = thiz;
    uintptr_t dwRetAddr = 0;
    __asm__ volatile("mov %0, lr" : "=r"(dwRetAddr)); // 获取调用处的返回地址
    dwRetAddr = g_libServerBrowser->ToLibAddr(dwRetAddr);
    LOGD("who call me: %p", dwRetAddr);

    spdlog::info("Hooking ConnectToServer OnClick..."); // 这个gameserveritem_t基本是没有数据的，检查了源码没看到有赋值的地方

    gameserveritem_t* serverItem = (gameserveritem_t*)((uintptr_t)thiz + 1120); // 只能获取个ip和端口
    std::string ip = ipToString(serverItem->m_NetAdr.m_unIP) + ":" + std::to_string(serverItem->m_NetAdr.m_usConnectionPort);
    spdlog::info("full ip:{}", ip);
    bool b_hasPassword = false;
    for (const auto& pair : hasPasswordMap) {
        spdlog::info("hasPasswordMap: {}", pair.first);
    }
    auto it = hasPasswordMap.find(ip);
    if (it != hasPasswordMap.end()) {
        spdlog::info("server {} is in list!", ip);
        b_hasPassword = it->second;
    }
    if (b_hasPassword) { // 有密码
        spdlog::info("server {} has password!", ip);
        // 调用输入密码弹窗
        g_java->showPasswordDialog();
        return;
    }


    // 进入服务器
//    g_libServerBrowser->CallFunction(g_libServerBrowser->Abs(0x1DB5E8), thiz, (uintptr_t)thiz + 1120);

//    void* box = g_libServerBrowser->CallFunction<void*>(g_libServerBrowser->Abs(0x1DBF38), thiz);
//    void* box = CDialogServerPassword(thiz);

    // 获取偏移位置的函数指针
/*    auto AddActionSignalTarget = (void (*)(void*, void*))(*(void**)((uint8_t*)*(void**)box + 360));
    // 调用目标函数
    AddActionSignalTarget(box, thiz);

    spdlog::info("哈1");*/
//    CDialogServerPassword_Activate(box, name, 0);
//    g_libServerBrowser->CallFunction<void>(g_libServerBrowser->Abs(0x1DC140), box, name, 0);

//    return;
    CDialogGameInfo_ConnectToServer(thiz);
}

/**
 *  密码输入完毕回调
 */
extern "C" JNIEXPORT void JNICALL Java_org_libsdl_app_SDLActivity_onPasswordCallBack(JNIEnv* env, jobject sampObj, jstring jpassword)
{
    if (cDialogGameInfoInstance == nullptr) {
        return;
    }
    const char* passwordCStr = env->GetStringUTFChars(jpassword, nullptr);
    char* password = (char*)((uint8_t*)cDialogGameInfoInstance + 1033);
    const char* newPassword = passwordCStr; //"RnMOM6789";
    size_t maxPasswordLength = 64;
    std::strncpy((char*)password, (char*)newPassword, maxPasswordLength - 1);
    password[std::min(std::strlen(newPassword), maxPasswordLength - 1)] = '\0';

    spdlog::info("password: {}", password);
    CDialogGameInfo_ConnectToServer(cDialogGameInfoInstance);
//    g_libServerBrowser->CallFunction(g_libServerBrowser->Abs(0x1DB5E8), cDialogGameInfoInstance, (uintptr_t)cDialogGameInfoInstance + 1120);
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

    // ServerBrowser
    HOOK_SERVERBROWSER_ADDR(0x1DAEC0, CDialogGameInfo_ConnectToServer); // 但是连接服务器执行的却是这里 构造1D9088 ChangeGame: 1DA78C
    HOOK_SERVERBROWSER_ADDR(0x1DBF38, CDialogServerPassword); //
    HOOK_SERVERBROWSER_ADDR(0x1DC140, CDialogServerPassword_Activate); //
    HOOK_SERVERBROWSER_ADDR(0x1D2284, ServerResponded); // 真正执行的服务器返回信息
    HOOK_SERVERBROWSER_ADDR(0x1DB3E8, CDialogGameInfo_ServerResponded); // 压根没执行，有信息就怪了
    HOOK_SERVERBROWSER_ADDR(0x1D20A4, CBaseGamesPage_OnBeginConnect);
    HOOK_SERVERBROWSER_ADDR(0x1DA78C, CDialogGameInfo_ChangeGame);
    HOOK_SERVERBROWSER_ADDR(0x1DAAA4, CDialogGameInfo_PerformLayout);
//    HOOK_SERVERBROWSER_ADDR(0x1DA6A8, CDialogGameInfo_Run);
}

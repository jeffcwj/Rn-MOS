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
 * 服务器返回的内容
 * @param thiz
 * @param server
 * @return
 */
static std::unordered_map<std::string, bool> hasPasswordMap; // 非线程安全
static std::mutex mapMutex;  // 添加互斥锁
DECL_HOOK(void*, ServerResponded, void* thiz, newgameserver_t &server) {
    std::lock_guard<std::mutex> lock(mapMutex);  // 通过 lock_guard 加锁
    spdlog::info("hooking ServerResponded...");
    char* host = (char*)server.m_NetAdr.ip;
    if (host == nullptr || server.m_NetAdr.port == 0) {
        return ServerResponded(thiz, server);
    }
    unsigned short port = portToBigEndian(server.m_NetAdr.port);
    if (port <= 0 || port > 65535)
        return ServerResponded(thiz, server);
    bool hasPassword = server.m_bPassword;
    std::string ip = ipToString(host) + ":" + std::to_string(port);
    hasPasswordMap.insert_or_assign(ip, hasPassword);
    spdlog::info("gamepath: {}, hostname: {}, ip: {}, hasPassword: {}",
                 server.m_szGameDir, server.m_szServerName, ip, hasPassword);
    return ServerResponded(thiz, server);
}

/**
 * 点击连接按钮 执行的事件
 * @param thiz
 */
static void* cDialogGameInfoInstance = nullptr;
DECL_HOOK(void, CDialogGameInfo_ConnectToServer, void* thiz) {
    spdlog::info("Hooking ConnectToServer OnClick...");
    cDialogGameInfoInstance = thiz;
    gameserveritem_t* serverItem = (gameserveritem_t*)((uintptr_t)thiz + 1120); // 这个 gameserveritem_t 基本是没有数据的 只能获取个ip和端口
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
        const char* password = (char*)((uint8_t*)thiz + 1033); // TODO 偏移不知道会不会变化
        // 调用输入密码弹窗
        g_java->showPasswordDialog(password);
        return;
    }
    CDialogGameInfo_ConnectToServer(thiz); // 调用原函数
}

/**
 *  密码输入完毕回调
 */
extern "C" JNIEXPORT void JNICALL Java_org_libsdl_app_SDLActivity_onPasswordCallBack(JNIEnv* env, jobject sdlObj, jstring jpassword)
{
    if (cDialogGameInfoInstance == nullptr) {
        return;
    }
    const char* newPassword = env->GetStringUTFChars(jpassword, nullptr); // 获得弹窗输入的密码
    char* password = (char*)((uint8_t*)cDialogGameInfoInstance + 1033); // TODO 偏移不知道会不会变化
    size_t maxPasswordLength = 64;
    std::strncpy((char*)password, (char*)newPassword, maxPasswordLength - 1);
    password[std::min(std::strlen(newPassword), maxPasswordLength - 1)] = '\0';

    spdlog::info("password: {}", password);
    CDialogGameInfo_ConnectToServer(cDialogGameInfoInstance); // 加入游戏
}

void installHooks() {
    spdlog::info("Installing global hooks...");

    // engine
    if (Addr::FUNC_NET_StringToAdr)
    HOOK_ENGINE_ADDR(Addr::FUNC_NET_StringToAdr, NET_StringToAdr); // ip字符串转adr对象
    if (Addr::FUNC_CMaster_AddServer)
    HOOK_ENGINE_ADDR(Addr::FUNC_CMaster_AddServer, CMaster_AddServer); // 添加主服务器
    if (Addr::FUNC_CMaster_RequestInternetServerList)
    HOOK_ENGINE_ADDR(Addr::FUNC_CMaster_RequestInternetServerList, CMaster_RequestInternetServerList); // 加载服务器列表

    // GameUI
    if (Addr::FUNC_CBasePanel)
    HOOK_GAMEUI_ADDR(Addr::FUNC_CBasePanel, CBasePanel); // UI面板构造函数
    if (Addr::FUNC_AddUrlButton)
    HOOK_GAMEUI_ADDR(Addr::FUNC_AddUrlButton, AddUrlButton); // 添加URL按钮

    // ServerBrowser
    if (Addr::FUNC_CDialogGameInfo_ConnectToServer)
    HOOK_SERVERBROWSER_ADDR(Addr::FUNC_CDialogGameInfo_ConnectToServer, CDialogGameInfo_ConnectToServer); // 连接服务器

    if (Addr::FUNC_ServerResponded)
    HOOK_SERVERBROWSER_ADDR(Addr::FUNC_ServerResponded, ServerResponded); // 处理服务器返回信息
}

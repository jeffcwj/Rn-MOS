//
// Created by 33688 on 2024/12/3.
//
#include "main.h"

// 分析不动，还是太菜了

/*DECL_HOOK(void, LoadMainServers2, char* ip, int64_t a2) {
    uintptr_t dwRetAddr = 0;
    __asm__ volatile("mov %0, lr" : "=r"(dwRetAddr)); // 获取调用处的返回地址
    dwRetAddr = g_libEngine->ToLibAddr(dwRetAddr);
    spdlog::info("hooking LoadMainServers2... from {:#x}", dwRetAddr);
    spdlog::info("LoadMainServers2... ip: {} or addr of ip: {:#x}", ip, g_libEngine->ToLibAddr((uintptr_t)ip));
    spdlog::info("LoadMainServers2... param2: {:#x} or str: {}", g_libEngine->ToLibAddr((uintptr_t)a2), (char*)a2);
    LoadMainServers2(ip, a2);
}*/

void installHooks() {
    spdlog::info("Installing global hooks...");
//    HOOK_ENGINE_ADDR(0x5B3B0C, LoadMainServers2);
}

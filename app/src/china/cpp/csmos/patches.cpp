//
// Created by 33688 on 2024/12/3.
//
#include "main.h"

void installPatches() {
    if (Addr::FUNC_CDialogGameInfo_Run) Memory::ret(g_libServerBrowser->Abs(Addr::FUNC_CDialogGameInfo_Run)); // 禁止弹出服务器详情窗口

    if (Addr::bDedicated) {
        if (Addr::STR_listenserver)
            Memory::writeMemory(g_libEngine->Abs(Addr::STR_listenserver), (uintptr_t) "\x62\x79\x44\x65\x65\x70\x44\x43\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00", 33);

        // csso Host_NewGame
        Memory::nop(g_libEngine->Abs(0x5BCF74), 2); // SCR_BeginLoadingPlaque
        Memory::ret(g_libEngine->Abs(0x6CACD0)); // SCR_BeginLoadingPlaque
        Memory::nop(g_libEngine->Abs(0x5BD0F0), 2); // SCR_EndLoadingPlaque
        Memory::ret(g_libEngine->Abs(0x6CACD0)); // SCR_EndLoadingPlaque
        // Memory::nop(g_libEngine->Abs(0x5BD198), 8); // EngineVGui stuffs
//        Memory::nop(g_libEngine->Abs(0x72E850), 12); // savestore stuffs
//        Memory::nop(g_libEngine->Abs(0x72E868), 32); // savestore stuffs
    }
}
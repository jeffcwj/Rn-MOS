//
// Created by 33688 on 2024/12/3.
//
#include "main.h"

void installPatches() {
    if (Addr::FUNC_CDialogGameInfo_Run) Memory::ret(g_libServerBrowser->Abs(Addr::FUNC_CDialogGameInfo_Run)); // 禁止弹出服务器详情窗口
}
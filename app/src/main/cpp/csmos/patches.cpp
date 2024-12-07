//
// Created by 33688 on 2024/12/3.
//
#include "main.h"

void installPatches() {
    Memory::ret(g_libServerBrowser->Abs(0x1DA6A8));
}
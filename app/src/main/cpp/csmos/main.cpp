#include "main.h"
#include "spdlog/sinks/android_sink.h"


JavaVM* g_java_vm = nullptr;
Java* g_java = nullptr;
SymUtils* g_libEngine = new SymUtils();
SymUtils* g_libGameUI = new SymUtils();
SymUtils* g_libServerBrowser = new SymUtils();
SymUtils* g_libSDL2 = new SymUtils();
SymUtils* g_libLauncher = new SymUtils();

void installHooks();
void installPatches();

jint JNI_OnLoad(JavaVM* vm, [[maybe_unused]] void* reserved)
{
	JNIEnv* env;
	if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK) {
		return JNI_ERR;
	}

    // init spdlog
    try {
        auto android_logger = spdlog::android_logger_mt("android", LOG_TAG);
        android_logger->set_level(spdlog::level::info);
        android_logger->set_pattern("[%n] [%^%l%$] %v");
        spdlog::set_default_logger(android_logger);
    }
    catch (const spdlog::spdlog_ex& ex) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Log initialization failed: %s", ex.what());
        return 0;
    }

	g_java_vm = vm;

    g_java = new Java(g_java_vm, env); // 初始化java
    if (g_java->getFlavor().empty()) {
        return env->GetVersion();
    }

    // 直接硬编码路径
    std::string libEnginePath = "/data/data/rn.csgo.game/files/libs/CSMOS_v65/libengine.so";
    std::string libGameUIPath = "/data/data/rn.csgo.game/files/libs/CSMOS_v65/libGameUI.so";
    std::string libServerBrowserPath = "/data/data/rn.csgo.game/files/libs/CSMOS_v65/libServerBrowser.so";
    if (g_java->getFlavor() == CSVersion::CSMOSV65) {
        libEnginePath = "/data/data/rn.csgo.game/files/libs/CSMOS_v65/libengine.so";
        libGameUIPath = "/data/data/rn.csgo.game/files/libs/CSMOS_v65/libGameUI.so";
        libServerBrowserPath = "/data/data/rn.csgo.game/files/libs/CSMOS_v65/libServerBrowser.so";
    } else if (g_java->getFlavor() == CSVersion::CSMOSV75) { // abandoned
        libEnginePath = "/data/data/rn.csgo.game/files/libs/CSMOS_v75/libengine.so";
        libGameUIPath = "/data/data/rn.csgo.game/files/libs/CSMOS_v75/libGameUI.so";
        libServerBrowserPath = "/data/data/rn.csgo.game/files/libs/CSMOS_v75/libServerBrowser.so";
    } else if (g_java->getFlavor() == CSVersion::CSMOSV78) {
        libEnginePath = "/data/data/rn.csgo.game/files/libs/CSMOS_v78/libengine.so";
        libGameUIPath = "/data/data/rn.csgo.game/files/libs/CSMOS_v78/libGameUI.so";
        libServerBrowserPath = "/data/data/rn.csgo.game/files/libs/CSMOS_v78/libServerBrowser.so";
    } // TODO: not done

    spdlog::info("CSMOS version: {}", g_java->getFlavor());

    GHandle engineHandle = g_libEngine->Open(libEnginePath.c_str());
    if (!engineHandle) {
        spdlog::info("Cannot open libengine.so");
        return env->GetVersion();
    }
    GHandle GameUIHandle = g_libGameUI->Open(libGameUIPath.c_str());
    if (!GameUIHandle) {
        spdlog::info("Cannot open libGameUI.so");
        return env->GetVersion();
    }
    GHandle ServerBrowserHandle = g_libServerBrowser->Open(libServerBrowserPath.c_str());
    if (!ServerBrowserHandle) {
        spdlog::info("Cannot open libServerBrowser.so");
        return env->GetVersion();
    }

    bool isOk = g_Addr.initialize(); // 初始化地址
    if (!isOk) {
        spdlog::error("Unsupported version detected"); // 动态库版本不支持
        return env->GetVersion();
    }

    // 应用钩子和补丁
    installHooks();
    installPatches();

    spdlog::info("Rn:CS library loaded! Build time: " __DATE__ " " __TIME__);
	return env->GetVersion();
}

void JNI_OnUnload([[maybe_unused]] JavaVM* vm, [[maybe_unused]] void* reserved)
{
    spdlog::info("Rn:CS library unloaded!");
}


extern "C" JNIEXPORT void JNICALL Java_org_libsdl_app_SDLActivity_initRnCS(JNIEnv* env, jobject obj)
{
    g_java->setupContext(obj, env);
}



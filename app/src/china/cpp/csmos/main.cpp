#include "main.h"
#include "spdlog/sinks/android_sink.h"
#include "BackTrace.h"


JavaVM* g_java_vm = nullptr;
Java* g_java = nullptr;
SymUtils* g_libEngine = new SymUtils();
SymUtils* g_libGameUI = new SymUtils();
SymUtils* g_libServerBrowser = new SymUtils();
SymUtils* g_libSDL2 = new SymUtils();
SymUtils* g_libLauncher = new SymUtils();

void installHooks();
void installPatches();

typedef int (*LauncherMain_t)( int argc, char **argv );

int launch_main_android(int argc, char* argv[])
{
   /* void *SDL2Handle = dlopen("/data/data/rn.csgo.game/files/libs/CSMOS_v80/libSDL2.so", RTLD_NOW);
    if (!SDL2Handle) {
        LOGE("Failed to load SDL2: %s", dlerror());
        return -1;
    }
    void *steam_apiHandle = dlopen("/data/data/rn.csgo.game/files/libs/CSMOS_v80/libsteam_api.so", RTLD_NOW);
    if (!steam_apiHandle) {
        LOGE("Failed to load steam_api: %s", dlerror());
        return -1;
    }
    void *tier0Handle = dlopen("/data/data/rn.csgo.game/files/libs/CSMOS_v80/libtier0.so", RTLD_NOW);
    if (!tier0Handle) {
        LOGE("Failed to load tier0: %s", dlerror());
        return -1;
    }
    void *vstdlibHandle = dlopen("/data/data/rn.csgo.game/files/libs/CSMOS_v80/libvstdlib.so", RTLD_NOW);
    if (!vstdlibHandle) {
        LOGE("Failed to load vstdlib: %s", dlerror());
        return -1;
    }
    void *toglHandle = dlopen("/data/data/rn.csgo.game/files/libs/CSMOS_v80/libtogl.so", RTLD_NOW);
    if (!toglHandle) {
        LOGE("Failed to load togl: %s", dlerror());
        return -1;
    }

    void *filesystem_stdioHandle = dlopen("/data/data/rn.csgo.game/files/libs/CSMOS_v80/libfilesystem_stdio.so", RTLD_NOW);
    if (!filesystem_stdioHandle) {
        LOGE("Failed to load filesystem_stdio: %s", dlerror());
        return -1;
    }*/

    const char *lib_path = "/data/data/rn.csgo.game/files/libs/CSMOS_v80/liblauncher.so";

    void *handle = dlopen(lib_path, RTLD_NOW);
    if (!handle) {
        LOGE("Failed to load launcher: %s", dlerror());
        return -1;
    }

    LauncherMain_t mainFunc = (LauncherMain_t)dlsym(handle, "LauncherMain");
    if (!mainFunc) {
        LOGE("Failed to find LauncherMain: %s", dlerror());
        dlclose(handle);
        return -2;
    }

    // 可选调试日志
    LOGI("Calling LauncherMain...");
    int result = mainFunc(argc, argv);
    // dlclose(handle);
    return result;
}

extern "C"
JNIEXPORT jint JNICALL
//Java_com_billflx_csgo_MainActivity_00024Companion_nativeMain(JNIEnv *env, jobject thiz, jobjectArray jargs)
 Java_org_libsdl_app_SDLActivity_nativeMain(JNIEnv *env, jclass thiz, jobjectArray jargs)
{
    int argc = env->GetArrayLength(jargs);
    char* argv[argc + 1];

    for (int i = 0; i < argc; i++) {
        jstring str = (jstring)env->GetObjectArrayElement(jargs, i);
        const char* utf = env->GetStringUTFChars(str, 0);
        argv[i] = strdup(utf);
        env->ReleaseStringUTFChars(str, utf);
    }
    argv[argc] = NULL;

    LOGD("测试试试水");
    int result = launch_main_android(argc, argv);

    for (int i = 0; i < argc; i++) {
        free(argv[i]);
    }

    return result;
}


jint JNI_OnLoad(JavaVM* vm, [[maybe_unused]] void* reserved)
{
	JNIEnv* env;
	if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK) {
		return JNI_ERR;
	}

    // signal handler
    struct sigaction sig_action{};
    sig_action.sa_sigaction = [](int signal, siginfo_t* info, void* ctx) {
        dump_register(signal, info, ctx);
        dump_stack(2);
        exit(signal);
    };
    sigemptyset(&sig_action.sa_mask);
    sig_action.sa_flags = SA_SIGINFO;
    sigaction(SIGSEGV, &sig_action, nullptr);

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
    } else if (g_java->getFlavor() == CSVersion::CSMOSV80) {
        libEnginePath = "/data/data/rn.csgo.game/files/libs/CSMOS_v80/libengine.so";
        libGameUIPath = "/data/data/rn.csgo.game/files/libs/CSMOS_v80/libGameUI.so";
        libServerBrowserPath = "/data/data/rn.csgo.game/files/libs/CSMOS_v80/libServerBrowser.so";
    } else if (g_java->getFlavor() == CSVersion::CSSOV1) {
        libEnginePath = "/data/data/rn.csgo.game/files/libs/CSSOV1/libengine.so";
        libGameUIPath = "";
        libServerBrowserPath = "/data/data/rn.csgo.game/files/libs/CSSOV1/libServerBrowser.so";
    }

    spdlog::info("CS version: {}", g_java->getFlavor());

    GHandle engineHandle = g_libEngine->Open(libEnginePath.c_str());
    if (!engineHandle) {
        spdlog::info("Cannot open libengine.so");
        return env->GetVersion();
    }
    if (!libGameUIPath.empty()) {
        GHandle GameUIHandle = g_libGameUI->Open(libGameUIPath.c_str());
        if (!GameUIHandle) {
            spdlog::info("Cannot open libGameUI.so");
            return env->GetVersion();
        }
    } else {
        spdlog::info("Skip libGameUI.so");
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



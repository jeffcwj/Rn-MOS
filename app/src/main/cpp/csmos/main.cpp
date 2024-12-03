#include "main.h"
#include "spdlog/sinks/android_sink.h"


JavaVM* g_java_vm = nullptr;
SymUtils* g_libEngine = new SymUtils();

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

    GHandle handle = g_libEngine->Open("libengine.so");
    LOGD("libengine handle: %p", handle);
    LOGD("libengine addr: %p", g_libEngine->Abs(0));
    if (!handle) {
        spdlog::info("Cannot open libengine.so");
        return env->GetVersion();
    }

    installHooks();
    installPatches();

    spdlog::info("Rn:MOS library loaded! Build time: " __DATE__ " " __TIME__);
	return env->GetVersion();
}

void JNI_OnUnload([[maybe_unused]] JavaVM* vm, [[maybe_unused]] void* reserved)
{
    spdlog::info("Rn:MOS library unloaded!");
}




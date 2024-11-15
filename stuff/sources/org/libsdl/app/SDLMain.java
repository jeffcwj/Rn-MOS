package org.libsdl.app;

import android.os.Process;
import android.util.Log;

/* compiled from: SDLActivity.java */
/* loaded from: classes.dex */
class SDLMain implements Runnable {
    SDLMain() {
    }

    @Override // java.lang.Runnable
    public void run() {
        String library = SDLActivity.mSingleton.getMainSharedObject();
        String function = SDLActivity.mSingleton.getMainFunction();
        String[] arguments = SDLActivity.mSingleton.getArguments();
        try {
            Process.setThreadPriority(-4);
        } catch (Exception e) {
            Log.v("SDL", "modify thread properties failed " + e.toString());
        }
        Log.v("SDL", "Running main function " + function + " from library " + library);
        SDLActivity.nativeRunMain(library, function, arguments);
        Log.v("SDL", "Finished main function");
        if (SDLActivity.mSingleton != null && !SDLActivity.mSingleton.isFinishing()) {
            SDLActivity.mSDLThread = null;
            SDLActivity.mSingleton.finish();
        }
    }
}

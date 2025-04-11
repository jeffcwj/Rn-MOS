package org.libsdl.app;

import android.content.Context;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class SDL {
    protected static Context mContext;

    public static void setupJNI() {
        SDLActivity.nativeSetupJNI();
        SDLAudioManager.nativeSetupJNI();
        SDLControllerManager.nativeSetupJNI();
    }

    public static void initialize() {
        setContext(null);
        SDLActivity.initialize();
        SDLAudioManager.initialize();
        SDLControllerManager.initialize();
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void loadLibrary(String libraryName) throws UnsatisfiedLinkError, SecurityException, NullPointerException {
        if (libraryName == null) {
            throw new NullPointerException("No library name provided.");
        }
        try {
            Class<?> relinkClass = mContext.getClassLoader().loadClass("com.getkeepsafe.relinker.ReLinker");
            Class<?> relinkListenerClass = mContext.getClassLoader().loadClass("com.getkeepsafe.relinker.ReLinker$LoadListener");
            Class<?> contextClass = mContext.getClassLoader().loadClass("android.content.Context");
            Class<?> stringClass = mContext.getClassLoader().loadClass("java.lang.String");
            Method forceMethod = relinkClass.getDeclaredMethod("force", new Class[0]);
            Object relinkInstance = forceMethod.invoke(null, new Object[0]);
            Class<?> relinkInstanceClass = relinkInstance.getClass();
            Method loadMethod = relinkInstanceClass.getDeclaredMethod("loadLibrary", contextClass, stringClass, stringClass, relinkListenerClass);
            loadMethod.invoke(relinkInstance, mContext, libraryName, null, null);
        } catch (Throwable th) {
            try {
                System.loadLibrary(libraryName);
            } catch (SecurityException se) {
                throw se;
            } catch (UnsatisfiedLinkError ule) {
                throw ule;
            }
        }
    }
}

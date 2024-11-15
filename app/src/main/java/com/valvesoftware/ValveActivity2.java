package com.valvesoftware;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import java.io.File;
import java.util.Locale;
import me.nillerusr.ExtractAssets;
import me.nillerusr.LauncherActivity;

/* loaded from: classes.dex */
public class ValveActivity2 {
    public static SharedPreferences mPref;
    private static Activity mSingleton;

    private static native void nativeOnActivityResult(Activity activity, int i, int i2, Intent intent);

    public static native void setArgs(String str);

    public static native int setenv(String str, String str2, int i);

    public static int findGameinfo(String path) {
        File dir = new File(path);
        boolean havePlatform = false;
        boolean haveGameinfo = false;
        if (!dir.isDirectory()) {
            return 0;
        }
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if (f.getName().toLowerCase().equals("gameinfo.txt")) {
                        haveGameinfo = true;
                    }
                }
            }
            if (file.getName().toLowerCase().equals("platform")) {
                havePlatform = true;
            }
        }
        if (!haveGameinfo) {
            return 0;
        }
        if (!havePlatform) {
            return -1;
        }
        return 1;
    }

    public static boolean isModGameinfoExists(String path) {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().toLowerCase().equals("gameinfo.txt")) {
                return true;
            }
        }
        return false;
    }

    public static int preInit(Context context, Intent intent) {
        mPref = context.getSharedPreferences("mod", 0);
        String gamepath = mPref.getString("gamepath", LauncherActivity.getDefaultDir() + "/srceng");
        String gamedir = intent.getStringExtra("gamedir");
        if (gamedir == null || gamedir.isEmpty()) {
            gamedir = "hl2";
        }
        if (isModGameinfoExists(gamepath + "/" + gamedir)) {
            return findGameinfo(gamepath);
        }
        return 0;
    }

    public static void initNatives(Context context, Intent intent) {
        mPref = context.getSharedPreferences("mod", 0);
        ApplicationInfo appinf = context.getApplicationInfo();
        String gamepath = mPref.getString("gamepath", LauncherActivity.getDefaultDir() + "/srceng");
        String argv = intent.getStringExtra("argv");
        String gamedir = intent.getStringExtra("gamedir");
        String gamelibdir = intent.getStringExtra("gamelibdir");
        String customVPK = intent.getStringExtra("vpk");
        Log.v("SRCAPK", "argv=" + argv);
        if (gamedir == null || gamedir.isEmpty()) {
            gamedir = "hl2";
        }
        if (argv == null || argv.isEmpty()) {
            argv = mPref.getString("argv", "-console");
        }
        String argv2 = "-game " + gamedir + " " + argv;
        if (gamelibdir != null && !gamelibdir.isEmpty()) {
            setenv("APP_MOD_LIB", gamelibdir, 1);
        }
        ExtractAssets.extractAssets(context);
        String vpks = context.getFilesDir().getPath() + "/" + ExtractAssets.VPK_NAME;
        if (customVPK != null && !customVPK.isEmpty()) {
            vpks = customVPK + "," + vpks;
        }
        Log.v("SRCAPK", "vpks=" + vpks);
        setenv("EXTRAS_VPK_PATH", vpks, 1);
        setenv("LANG", Locale.getDefault().toString(), 1);
        setenv("APP_DATA_PATH", appinf.dataDir, 1);
        setenv("APP_LIB_PATH", appinf.nativeLibraryDir, 1);
        if (mPref.getBoolean("rodir", false)) {
            setenv("VALVE_GAME_PATH", LauncherActivity.getAndroidDataDir(), 1);
        } else {
            setenv("VALVE_GAME_PATH", gamepath, 1);
        }
        Log.v("SRCAPK", "argv=" + argv2);
        setArgs(argv2);
    }
}

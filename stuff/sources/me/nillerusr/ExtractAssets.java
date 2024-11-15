package me.nillerusr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class ExtractAssets {
    public static final String VPK_NAME = "extras_dir.vpk";
    static SharedPreferences mPref;
    public static String TAG = "ExtractAssets";
    public static int PAK_VERSION = 24;

    private static int chmod(String path, int mode) {
        try {
            int ret = Runtime.getRuntime().exec("chmod " + Integer.toOctalString(mode) + " " + path).waitFor();
            Log.d(TAG, "chmod " + Integer.toOctalString(mode) + " " + path + ": " + ret);
        } catch (Exception e) {
            Log.d(TAG, "chmod: Runtime not worked: " + e.toString());
        }
        try {
            Class fileUtils = Class.forName("android.os.FileUtils");
            Method setPermissions = fileUtils.getMethod("setPermissions", String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            int ret2 = ((Integer) setPermissions.invoke(null, path, Integer.valueOf(mode), -1, -1)).intValue();
            return ret2;
        } catch (Exception e2) {
            Log.d(TAG, "chmod: FileUtils not worked: " + e2.toString());
            return -1;
        }
    }

    public static void extractAsset(Context context, String asset, Boolean force) {
        File asset_file;
        Boolean asset_exists;
        AssetManager am = context.getAssets();
        try {
            asset_file = new File(context.getFilesDir().getPath() + "/" + asset);
            asset_exists = Boolean.valueOf(asset_file.exists());
        } catch (Exception e) {
            Log.e("SRCAPK", "Failed to extract vpk:" + e.toString());
        }
        if (force.booleanValue() || !asset_exists.booleanValue()) {
            InputStream is = am.open(asset);
            FileOutputStream os = new FileOutputStream(context.getFilesDir().getPath() + "/tmp");
            byte[] buffer = new byte[8192];
            while (true) {
                int length = is.read(buffer);
                if (length <= 0) {
                    break;
                } else {
                    os.write(buffer, 0, length);
                }
            }
            os.close();
            File tmp = new File(context.getFilesDir().getPath() + "/tmp");
            if (asset_exists.booleanValue()) {
                asset_file.delete();
            }
            tmp.renameTo(new File(context.getFilesDir().getPath() + "/" + asset));
            chmod(context.getFilesDir().getPath() + "/" + asset, 511);
        }
    }

    public static void extractAssets(Context context) {
        ApplicationInfo appinf = context.getApplicationInfo();
        chmod(appinf.dataDir, 511);
        chmod(context.getFilesDir().getPath(), 511);
        extractVPK(context);
        extractAsset(context, "DroidSansFallback.ttf", false);
        extractAsset(context, "LiberationMono-Regular.ttf", false);
        extractAsset(context, "dejavusans-boldoblique.ttf", false);
        extractAsset(context, "dejavusans-bold.ttf", false);
        extractAsset(context, "dejavusans-oblique.ttf", false);
        extractAsset(context, "dejavusans.ttf", false);
        extractAsset(context, "Itim-Regular.otf", false);
    }

    public static void extractVPK(Context context) {
        if (mPref == null) {
            mPref = context.getSharedPreferences("mod", 0);
        }
        Boolean force = Boolean.valueOf(mPref.getInt("pakversion", 0) != PAK_VERSION);
        extractAsset(context, VPK_NAME, force);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt("pakversion", PAK_VERSION);
        editor.commit();
    }
}

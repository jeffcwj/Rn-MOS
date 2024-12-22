package me.nillerusr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.billflx.csgo.bean.CSVersionInfoEnum;
import com.billflx.csgo.data.ModLocalDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

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
        File asset_file = null;
        Boolean asset_exists = null;
        AssetManager am = context.getAssets();
        try {
            asset_file = new File(context.getFilesDir().getPath() + "/" + asset);
            asset_exists = Boolean.valueOf(asset_file.exists());
        } catch (Exception e) {
            Log.e("SRCAPK", "Failed to extract vpk:" + e.toString());
        }
        if (force.booleanValue() || !asset_exists.booleanValue()) {
            try {
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
            } catch (Exception e) {

            }
        }
    }

    /**
     * 解压字体资源
     * @param context
     */
    public static void extractAssets(Context context) {
        ApplicationInfo appinf = context.getApplicationInfo();
        chmod(appinf.dataDir, 511);
        chmod(context.getFilesDir().getPath(), 511);
        extractVPK(context);
        extractLibs(context); // 解压动态库
        extractAsset(context, "DroidSansFallback.ttf", false);
        extractAsset(context, "LiberationMono-Regular.ttf", false);
        extractAsset(context, "dejavusans-boldoblique.ttf", false);
        extractAsset(context, "dejavusans-bold.ttf", false);
        extractAsset(context, "dejavusans-oblique.ttf", false);
        extractAsset(context, "dejavusans.ttf", false);
        extractAsset(context, "Itim-Regular.otf", false);
    }

    /**
     * 复制文件到SD卡
     * @param context
     * @param fileName 复制的文件名
     * @param path 保存的目录路径
     */
    public static void copyAssetsFile(Context context, String fileName, String path) {
        try {
            // 打开 Assets 文件
            InputStream mInputStream = context.getAssets().open(fileName);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs(); // 创建目标目录
            }
            File mFile = new File(path, fileName);
            if (!mFile.exists()) {
                mFile.getParentFile().mkdirs(); // 补创建目录
                Log.d(TAG, "copyAssetsFile: " + mFile.getAbsolutePath());
                mFile.createNewFile(); // 创建目标文件
            }
            Log.d(TAG, "开始拷贝文件：" + fileName);

            // 拷贝文件内容
            FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = mInputStream.read(buffer)) > 0) {
                mFileOutputStream.write(buffer, 0, bytesRead);
            }

            // 关闭流
            mInputStream.close();
            mFileOutputStream.close();

            Log.d(TAG, "文件拷贝成功：" + mFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "文件拷贝失败：" + fileName, e);
        }
    }

    public static void extractExecAsset(Context context, String asset, String targetPath) {
        File targetFile = new File(targetPath, asset);
//        targetFile.setReadable(true);
//        targetFile.setWritable(true);
//        targetFile.setExecutable(true);
        if (targetFile.exists()) {
            boolean isOk = targetFile.delete();
            Log.d(TAG, "remove old lib: " + isOk);
        }
        copyAssetsFile(context, asset, targetPath);
        targetFile.setReadable(true);
        targetFile.setWritable(false);
        targetFile.setExecutable(false);
    }


    /**
     * 解压内置 vpk
     * @param context
     */
    public static void extractVPK(Context context) {
        if (mPref == null) {
            mPref = context.getSharedPreferences("mod", Context.MODE_MULTI_PROCESS);
        }
        Boolean force = Boolean.valueOf(mPref.getInt("pakversion", 0) != PAK_VERSION);
//        String versionName = ModLocalDataSource.INSTANCE.getCurrentCSVersion();
        String versionName = mPref.getString("current_cs_version", CSVersionInfoEnum.Companion.getDefaultName());

        // 选择 pak版本 解压
        extractAsset(context, CSVersionInfoEnum.Companion.getVpkNameByName(versionName), true);
//        extractAsset(context, VPK_NAME, force); // 原版解压
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt("pakversion", PAK_VERSION);
        editor.commit();
    }

    public static void extractLibs(Context context) {
//        String versionName = ModLocalDataSource.INSTANCE.getCurrentCSVersion();
        String versionName = context.getSharedPreferences("mod", Context.MODE_MULTI_PROCESS).getString("current_cs_version", CSVersionInfoEnum.Companion.getDefaultName());
//        String libRelativePath = CSVersionInfoEnum.Companion.getLibPathByName(versionName);
        String libRelativePath = CSVersionInfoEnum.Companion.getCurrentLibPath();
        Log.d(TAG, "extractLibs: " + versionName + "  " + libRelativePath);
        String targetPath = context.getFilesDir() + libRelativePath;
        File targetFile = new File(targetPath);
        if (!targetFile.exists()) targetFile.getParentFile().mkdirs();

        libRelativePath = libRelativePath.startsWith("/")?libRelativePath.substring(1):libRelativePath;
        try {
            String[] fileNames = context.getAssets().list(libRelativePath);
            Log.d(TAG, "extractLibs: " + Arrays.toString(fileNames));
            for (String fileName: fileNames) {
                extractExecAsset(context, libRelativePath + "/" + fileName, context.getFilesDir().getPath());
            }
        } catch (IOException e) {
            Log.d(TAG, "extractLibs: " + e);
        }
    }

}

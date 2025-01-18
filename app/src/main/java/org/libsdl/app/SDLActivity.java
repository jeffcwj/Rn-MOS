package org.libsdl.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.UiModeManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.VibrationEffect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.billflx.csgo.bean.CSVersionInfoEnum;
import com.billflx.csgo.data.ModLocalDataSource;
import com.gtastart.common.util.MToast;
import com.pika.sillyboy.util.LoadLibUtils;
import com.valvesoftware.ValveActivity2;
import com.valvesoftware.source.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import me.nillerusr.DirchActivity;
import me.nillerusr.ExtractAssets;


/* loaded from: classes.dex */
public class SDLActivity extends Activity implements View.OnSystemUiVisibilityChangeListener {
    static final int COMMAND_CHANGE_TITLE = 1;
    static final int COMMAND_CHANGE_WINDOW_STYLE = 2;
    static final int COMMAND_SET_KEEP_SCREEN_ON = 5;
    static final int COMMAND_TEXTEDIT_HIDE = 3;
    protected static final int COMMAND_USER = 32768;
    static final int REQUEST_PERMISSIONS = 42;
    protected static final int SDL_ORIENTATION_LANDSCAPE = 1;
    protected static final int SDL_ORIENTATION_LANDSCAPE_FLIPPED = 2;
    protected static final int SDL_ORIENTATION_PORTRAIT = 3;
    protected static final int SDL_ORIENTATION_PORTRAIT_FLIPPED = 4;
    protected static final int SDL_ORIENTATION_UNKNOWN = 0;
    private static final int SDL_SYSTEM_CURSOR_ARROW = 0;
    private static final int SDL_SYSTEM_CURSOR_CROSSHAIR = 3;
    private static final int SDL_SYSTEM_CURSOR_HAND = 11;
    private static final int SDL_SYSTEM_CURSOR_IBEAM = 1;
    private static final int SDL_SYSTEM_CURSOR_NO = 10;
    private static final int SDL_SYSTEM_CURSOR_SIZEALL = 9;
    private static final int SDL_SYSTEM_CURSOR_SIZENESW = 6;
    private static final int SDL_SYSTEM_CURSOR_SIZENS = 8;
    private static final int SDL_SYSTEM_CURSOR_SIZENWSE = 5;
    private static final int SDL_SYSTEM_CURSOR_SIZEWE = 7;
    private static final int SDL_SYSTEM_CURSOR_WAIT = 2;
    private static final int SDL_SYSTEM_CURSOR_WAITARROW = 4;
    private static final String TAG = "SDL";
    public static boolean mBrokenLibraries;
    protected static SDLClipboardHandler mClipboardHandler;
    protected static Locale mCurrentLocale;
    public static NativeState mCurrentNativeState;
    protected static int mCurrentOrientation;
    protected static Hashtable<Integer, PointerIcon> mCursors;
    protected static boolean mFullscreenModeActive;
    public static boolean mHasFocus;
    public static final boolean mHasMultiWindow;
    public static boolean mIsInitCalled;
    public static boolean mIsResumedCalled;
    protected static int mLastCursorID;
    protected static ViewGroup mLayout;
    protected static SDLGenericMotionListener_API12 mMotionListener;
    public static NativeState mNextNativeState;
    protected static Thread mSDLThread;
    protected static boolean mScreenKeyboardShown;
    protected static SDLActivity mSingleton;
    protected static SDLSurface mSurface;
    protected static View mTextEdit;
    Handler commandHandler = new SDLCommandHandler();
    protected final int[] messageboxSelection = new int[1];
    private final Runnable rehideSystemUi = new Runnable() { // from class: org.libsdl.app.SDLActivity.9
        @Override // java.lang.Runnable
        public void run() {
            if (Build.VERSION.SDK_INT >= 19) {
                SDLActivity.this.getWindow().getDecorView().setSystemUiVisibility(5894);
            }
        }
    };

    public enum NativeState {
        INIT,
        RESUMED,
        PAUSED
    }

    public static native void nativeAddTouch(int i, String str);

    public static native void nativeFocusChanged(boolean z);

    public static native String nativeGetHint(String str);

    public static native void nativeLowMemory();

    public static native void nativePause();

    public static native void nativePermissionResult(int i, boolean z);

    public static native void nativeQuit();

    public static native void nativeResume();

    public static native int nativeRunMain(String str, String str2, Object obj);

    public static native void nativeSendQuit();

    public static native void nativeSetScreenResolution(int i, int i2, int i3, int i4, float f);

    public static native void nativeSetenv(String str, String str2);

    public static native int nativeSetupJNI();

    public static native void onNativeAccel(float f, float f2, float f3);

    public static native void onNativeClipboardChanged();

    public static native void onNativeDropFile(String str);

    public static native void onNativeKeyDown(int i);

    public static native void onNativeKeyUp(int i);

    public static native void onNativeKeyboardFocusLost();

    public static native void onNativeLocaleChanged();

    public static native void onNativeMouse(int i, int i2, float f, float f2, boolean z);

    public static native void onNativeOrientationChanged(int i);

    public static native void onNativeResize();

    public static native boolean onNativeSoftReturnKey();

    public static native void onNativeSurfaceChanged();

    public static native void onNativeSurfaceCreated();

    public static native void onNativeSurfaceDestroyed();

    public static native void onNativeTouch(int i, int i2, int i3, float f, float f2, float f3);

    static {
        mHasMultiWindow = Build.VERSION.SDK_INT >= 24;
        mBrokenLibraries = true;
    }

    public native void initRnMOS();
    public native void onPasswordCallBack(String password);

    public void passwordDialog(String password) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText et = new EditText(SDLActivity.this);
                et.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
                et.setHint("请输入服务器密码");
                if (!password.replace(" ", "").isEmpty()) {
                    et.setText(password); // 从内存中读取之前设置的密码
                }
                new AlertDialog.Builder(SDLActivity.this)
                        .setTitle("服务器密码")
                        .setView(et)
                        .setPositiveButton("进入", (dialog, b) -> {
                            String pswd = et.getText().toString();
                            if (pswd.replace(" ", "").isEmpty()) {
                                MToast.show(SDLActivity.this, "请不要为空");
                                return;
                            }
                            onPasswordCallBack(pswd); // 回调到jni
                        })
                        .show();
            }
        });
    }

    protected static SDLGenericMotionListener_API12 getMotionListener() {
        if (mMotionListener == null) {
            if (Build.VERSION.SDK_INT >= 26) {
                mMotionListener = new SDLGenericMotionListener_API26();
            } else if (Build.VERSION.SDK_INT >= 24) {
                mMotionListener = new SDLGenericMotionListener_API24();
            } else {
                mMotionListener = new SDLGenericMotionListener_API12();
            }
        }
        return mMotionListener;
    }

    protected String getMainSharedObject() {
        String library;
        String[] libraries = mSingleton.getLibraries();
        if (libraries.length > 0) {
            library = "lib" + libraries[libraries.length - 1] + ".so";
        } else {
            library = "liblauncher.so";
        }
        return getContext().getApplicationInfo().nativeLibraryDir + "/" + library;
    }

    protected String getMainFunction() {
        return "LauncherMainAndroid";
    }

    protected String[] getLibraries() {
        return new String[]{"SDL2", "launcher"};
    }

    public void loadLibraries() {
        /*for (String lib : getLibraries()) {
            SDL.loadLibrary(lib);
        }*/

        // 自定义加载动态库
        SharedPreferences mPref = getSharedPreferences("mod", Context.MODE_MULTI_PROCESS);
        String versionName = mPref.getString("current_cs_version", CSVersionInfoEnum.Companion.getDefaultName());
//        String versionName = ModLocalDataSource.INSTANCE.getCurrentCSVersion(); // 获取当前CS版本
        String libRelativePath = CSVersionInfoEnum.Companion.getLibPathByName(versionName);
        libRelativePath = libRelativePath.startsWith("/")?libRelativePath.substring(1):libRelativePath;

        LoadLibUtils.fromAssets(this, libRelativePath + "/libSDL2.so", getFilesDir().getPath());
        LoadLibUtils.fromAssets(this, libRelativePath + "/liblauncher.so", getFilesDir().getPath());

        try {
            System.loadLibrary("RnMOS"); // 最后加载，不然动态库打开失败
            initRnMOS(); // 马上初始化RnMOS
        } catch (Throwable e) {
            Log.e(TAG, "RnMOS init failed: " + e);
        }
    }

    protected String[] getArguments() {
        return new String[0];
    }

    public static void initialize() {
        mSingleton = null;
        mSurface = null;
        mTextEdit = null;
        mLayout = null;
        mClipboardHandler = null;
        mCursors = new Hashtable<>();
        mLastCursorID = 0;
        mSDLThread = null;
        mIsResumedCalled = false;
        mHasFocus = true;
        mNextNativeState = NativeState.INIT;
        mCurrentNativeState = NativeState.INIT;
    }

    public void applyPermissions(String[] permissions, int code) {
        List<String> requestPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions.add(permissions[i]);
                }
            }
        }
        if (!requestPermissions.isEmpty()) {
            String[] requestPermissionsArray = new String[requestPermissions.size()];
            for (int i2 = 0; i2 < requestPermissions.size(); i2++) {
                requestPermissionsArray[i2] = requestPermissions.get(i2);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(requestPermissionsArray, code);
            }
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == -1) {
                    Toast.makeText(this, R.string.srceng_launcher_error_no_permission, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
            init();
        }
    }

    public void init() {
        String filename;
        ExtractAssets.extractAssets(this); // TODO 在这解压试一下
        if (!mIsInitCalled) {
            mIsInitCalled = true;
            try {
                Thread.currentThread().setName("SDLActivity");
            } catch (Exception e) {
                Log.v(TAG, "modify thread properties failed " + e.toString());
            }
            String errorMsgBrokenLib = "";
            try {
                loadLibraries();
                mBrokenLibraries = false;
            } catch (Exception e2) {
                System.err.println(e2.getMessage());
                mBrokenLibraries = true;
                errorMsgBrokenLib = e2.getMessage();
            } catch (UnsatisfiedLinkError e3) {
                System.err.println(e3.getMessage());
                mBrokenLibraries = true;
                errorMsgBrokenLib = e3.getMessage();
            }
            if (mBrokenLibraries) {
                mSingleton = this;
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("An error occurred while trying to start the application. Please try again and/or reinstall." + System.getProperty("line.separator") + System.getProperty("line.separator") + "Error: " + errorMsgBrokenLib);
                dlgAlert.setTitle("SDL Error");
                dlgAlert.setPositiveButton("Exit", new DialogInterface.OnClickListener() { // from class: org.libsdl.app.SDLActivity.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int id) {
                        SDLActivity.mSingleton.finish();
                    }
                });
                dlgAlert.setCancelable(false);
                dlgAlert.create().show();
                return;
            }
            int result = ValveActivity2.preInit(this, getIntent());
            if (result != 1) {
                mBrokenLibraries = true;
                mSingleton = this;
                AlertDialog.Builder dlgAlert2 = new AlertDialog.Builder(this);
                dlgAlert2.setTitle(getResources().getString(R.string.srceng_launcher_error));
                if (result == 0) {
                    dlgAlert2.setMessage(getResources().getString(R.string.srceng_launcher_error_find_gameinfo));
                } else {
                    dlgAlert2.setMessage(getResources().getString(R.string.srceng_launcher_error_find_platform));
                }
                if (result == 0) {
                    dlgAlert2.setNegativeButton(R.string.srceng_launcher_set, new DialogInterface.OnClickListener() { // from class: org.libsdl.app.SDLActivity.2
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(SDLActivity.this, (Class<?>) DirchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            SDLActivity.this.startActivity(intent);
                            SDLActivity.mSingleton.finish();
                        }
                    });
                }
                dlgAlert2.setPositiveButton(R.string.srceng_launcher_ok, new DialogInterface.OnClickListener() { // from class: org.libsdl.app.SDLActivity.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int id) {
                        SDLActivity.mSingleton.finish();
                    }
                });
                dlgAlert2.setCancelable(false);
                dlgAlert2.create().show();
                return;
            }
            SDL.setupJNI();
            SDL.initialize();
            mSingleton = this;
            SDL.setContext(this);
            Intent intent = getIntent();
            ValveActivity2.initNatives(this, getIntent());
            mClipboardHandler = new SDLClipboardHandler();
            if (Build.VERSION.SDK_INT >= 19) {
                getWindow().getDecorView().setSystemUiVisibility(5894);
            }
            if (Build.VERSION.SDK_INT >= 28) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
            mSurface = new SDLSurface(getApplication());
            mLayout = new RelativeLayout(this);
            mLayout.addView(mSurface);
            mCurrentOrientation = getCurrentOrientation();
            onNativeOrientationChanged(mCurrentOrientation);
            try {
                if (Build.VERSION.SDK_INT < 24) {
                    mCurrentLocale = getContext().getResources().getConfiguration().locale;
                } else {
                    mCurrentLocale = getContext().getResources().getConfiguration().getLocales().get(0);
                }
            } catch (Exception e4) {
            }
            setContentView(mLayout);
            setWindowStyle(false);
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
            if (intent != null && intent.getData() != null && (filename = intent.getData().getPath()) != null) {
                Log.v(TAG, "Got filename: " + filename);
                onNativeDropFile(filename);
            }
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Device: " + Build.DEVICE);
        Log.v(TAG, "Model: " + Build.MODEL);
        Log.v(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        try {
            initRnMOS(); // 初始化RnMOS
        } catch (Throwable e) {
            Log.e(TAG, "RnMOS init failed: " + e);
        }

        mIsInitCalled = false;
        if (Build.VERSION.SDK_INT >= 23) {
            applyPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO"}, REQUEST_PERMISSIONS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.RECORD_AUDIO") == PackageManager.PERMISSION_GRANTED) {
                init();
            }
        }
    }

    protected void pauseNativeThread() {
        mNextNativeState = NativeState.PAUSED;
        mIsResumedCalled = false;
        if (!mBrokenLibraries) {
            handleNativeState();
        }
    }

    protected void resumeNativeThread() {
        mNextNativeState = NativeState.RESUMED;
        mIsResumedCalled = true;
        if (!mBrokenLibraries) {
            handleNativeState();
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();
        if (!mHasMultiWindow) {
            pauseNativeThread();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        Log.v(TAG, "onResume()");
        super.onResume();
        if (!mHasMultiWindow) {
            resumeNativeThread();
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        Log.v(TAG, "onStop()");
        super.onStop();
        if (mHasMultiWindow) {
            pauseNativeThread();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        Log.v(TAG, "onStart()");
        super.onStart();
        if (mHasMultiWindow) {
            resumeNativeThread();
        }
    }

    public static int getCurrentOrientation() {
        Context context = getContext();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        switch (display.getRotation()) {
            case 0:
                return 3;
            case 1:
                return 1;
            case 2:
                return 4;
            case 3:
                return 2;
            default:
                return 0;
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.v(TAG, "onWindowFocusChanged(): " + hasFocus);
        if (!mBrokenLibraries) {
            mHasFocus = hasFocus;
            if (hasFocus) {
                mNextNativeState = NativeState.RESUMED;
                getMotionListener().reclaimRelativeMouseModeIfNeeded();
                handleNativeState();
                nativeFocusChanged(true);
                return;
            }
            nativeFocusChanged(false);
            if (!mHasMultiWindow) {
                mNextNativeState = NativeState.PAUSED;
                handleNativeState();
            }
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        Log.v(TAG, "onLowMemory()");
        super.onLowMemory();
        if (!mBrokenLibraries) {
            nativeLowMemory();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        Log.v(TAG, "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        if (!mBrokenLibraries) {
            if (mCurrentLocale == null || !mCurrentLocale.equals(newConfig.locale)) {
                mCurrentLocale = newConfig.locale;
                onNativeLocaleChanged();
            }
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        if (mBrokenLibraries) {
            super.onDestroy();
            return;
        }
        if (mSDLThread != null) {
            nativeSendQuit();
            try {
                mSDLThread.join();
            } catch (Exception e) {
                Log.v(TAG, "Problem stopping SDLThread: " + e);
            }
        }
        nativeQuit();
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        String trapBack = nativeGetHint("SDL_ANDROID_TRAP_BACK_BUTTON");
        if ((trapBack == null || !trapBack.equals("1")) && !isFinishing()) {
            super.onBackPressed();
        }
    }

    public static void manualBackButton() {
        mSingleton.pressBackButton();
    }

    public void pressBackButton() {
        runOnUiThread(new Runnable() { // from class: org.libsdl.app.SDLActivity.4
            @Override // java.lang.Runnable
            public void run() {
                if (!SDLActivity.this.isFinishing()) {
                    SDLActivity.this.superOnBackPressed();
                }
            }
        });
    }

    public void superOnBackPressed() {
        super.onBackPressed();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode;
        if (mBrokenLibraries || (keyCode = event.getKeyCode()) == 25 || keyCode == 24 || keyCode == 27 || keyCode == 168 || keyCode == 169) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    public static void handleNativeState() {
        if (mNextNativeState != mCurrentNativeState) {
            if (mNextNativeState == NativeState.INIT) {
                mCurrentNativeState = mNextNativeState;
                return;
            }
            if (mNextNativeState == NativeState.PAUSED) {
                if (mSDLThread != null) {
                    nativePause();
                }
                if (mSurface != null) {
                    mSurface.handlePause();
                }
                mCurrentNativeState = mNextNativeState;
                return;
            }
            if (mNextNativeState == NativeState.RESUMED && mSurface.mIsSurfaceReady && mHasFocus && mIsResumedCalled) {
                if (mSDLThread == null) {
                    mSDLThread = new Thread(new SDLMain(), "SDLThread");
                    mSurface.enableSensor(1, true);
                    mSDLThread.start();
                } else {
                    nativeResume();
                }
                mSurface.handleResume();
                mCurrentNativeState = mNextNativeState;
            }
        }
    }

    protected boolean onUnhandledMessage(int command, Object param) {
        return false;
    }

    protected static class SDLCommandHandler extends Handler {
        protected SDLCommandHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Window window;
            Context context = SDL.getContext();
            if (context == null) {
                Log.e(SDLActivity.TAG, "error handling message, getContext() returned null");
            }
            switch (msg.arg1) {
                case 1:
                    if (context instanceof Activity) {
                        ((Activity) context).setTitle((String) msg.obj);
                        break;
                    } else {
                        Log.e(SDLActivity.TAG, "error handling message, getContext() returned no Activity");
                        break;
                    }
                case 2:
                    if (Build.VERSION.SDK_INT >= 19) {
                        if (context instanceof Activity) {
                            Window window2 = ((Activity) context).getWindow();
                            if (window2 != null) {
                                window2.getDecorView().setSystemUiVisibility(5894);
                                window2.addFlags(1024);
                                window2.clearFlags(2048);
                                SDLActivity.mFullscreenModeActive = true;
                                break;
                            }
                        } else {
                            Log.e(SDLActivity.TAG, "error handling message, getContext() returned no Activity");
                            break;
                        }
                    }
                    break;
                case 3:
                    if (SDLActivity.mTextEdit != null) {
                        SDLActivity.mTextEdit.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(SDLActivity.mTextEdit.getWindowToken(), 0);
                        SDLActivity.mScreenKeyboardShown = false;
                        SDLActivity.mSurface.requestFocus();
                        break;
                    }
                    break;
                case 4:
                default:
                    if ((context instanceof SDLActivity) && !((SDLActivity) context).onUnhandledMessage(msg.arg1, msg.obj)) {
                        Log.e(SDLActivity.TAG, "error handling message, command is " + msg.arg1);
                        break;
                    }
                    break;
                case 5:
                    if ((context instanceof Activity) && (window = ((Activity) context).getWindow()) != null) {
                        if ((msg.obj instanceof Integer) && ((Integer) msg.obj).intValue() != 0) {
                            window.addFlags(128);
                            break;
                        } else {
                            window.clearFlags(128);
                            break;
                        }
                    }
                    break;
            }
        }
    }

    boolean sendCommand(int command, Object data) {
        Message msg = this.commandHandler.obtainMessage();
        msg.arg1 = command;
        msg.obj = data;
        boolean result = this.commandHandler.sendMessage(msg);
        if (Build.VERSION.SDK_INT >= 19 && command == 2) {
            boolean bShouldWait = false;
            if (data instanceof Integer) {
                Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                DisplayMetrics realMetrics = new DisplayMetrics();
                display.getRealMetrics(realMetrics);
                boolean bFullscreenLayout = realMetrics.widthPixels == mSurface.getWidth() && realMetrics.heightPixels == mSurface.getHeight();
                if (((Integer) data).intValue() == 1) {
                    bShouldWait = !bFullscreenLayout;
                } else {
                    bShouldWait = bFullscreenLayout;
                }
            }
            if (bShouldWait && getContext() != null) {
                synchronized (getContext()) {
                    try {
                        getContext().wait(500L);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public static boolean setActivityTitle(String title) {
        return mSingleton.sendCommand(1, title);
    }

    public static void setWindowStyle(boolean fullscreen) {
        mSingleton.sendCommand(2, Integer.valueOf(fullscreen ? 1 : 0));
    }

    public static void setOrientation(int w, int h, boolean resizable, String hint) {
        if (mSingleton != null) {
            mSingleton.setOrientationBis(w, h, resizable, hint);
        }
    }

    @SuppressLint("WrongConstant")
    public void setOrientationBis(int w, int h, boolean resizable, String hint) {
        int req;
        int orientation_landscape = -1;
        int orientation_portrait = -1;
        if (hint.contains("LandscapeRight") && hint.contains("LandscapeLeft")) {
            orientation_landscape = SDL_SYSTEM_CURSOR_SIZENESW;
        } else if (hint.contains("LandscapeRight")) {
            orientation_landscape = 0;
        } else if (hint.contains("LandscapeLeft")) {
            orientation_landscape = SDL_SYSTEM_CURSOR_SIZENS;
        }
        if (hint.contains("Portrait") && hint.contains("PortraitUpsideDown")) {
            orientation_portrait = SDL_SYSTEM_CURSOR_SIZEWE;
        } else if (hint.contains("Portrait")) {
            orientation_portrait = 1;
        } else if (hint.contains("PortraitUpsideDown")) {
            orientation_portrait = SDL_SYSTEM_CURSOR_SIZEALL;
        }
        boolean is_landscape_allowed = orientation_landscape != -1;
        boolean is_portrait_allowed = orientation_portrait != -1;
        if (!is_portrait_allowed && !is_landscape_allowed) {
            if (resizable) {
                req = SDL_SYSTEM_CURSOR_NO;
            } else {
                req = w > h ? SDL_SYSTEM_CURSOR_SIZENESW : SDL_SYSTEM_CURSOR_SIZEWE;
            }
        } else if (resizable) {
            if (is_portrait_allowed && is_landscape_allowed) {
                req = SDL_SYSTEM_CURSOR_NO;
            } else {
                req = is_landscape_allowed ? orientation_landscape : orientation_portrait;
            }
        } else if (is_portrait_allowed && is_landscape_allowed) {
            req = w > h ? orientation_landscape : orientation_portrait;
        } else {
            req = is_landscape_allowed ? orientation_landscape : orientation_portrait;
        }
        Log.v(TAG, "setOrientation() requestedOrientation=" + req + " width=" + w + " height=" + h + " resizable=" + resizable + " hint=" + hint);
        mSingleton.setRequestedOrientation(req);
    }

    public static void minimizeWindow() {
        if (mSingleton != null) {
            Intent startMain = new Intent("android.intent.action.MAIN");
            startMain.addCategory("android.intent.category.HOME");
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mSingleton.startActivity(startMain);
        }
    }

    public static boolean shouldMinimizeOnFocusLoss() {
        return false;
    }

    public static boolean isScreenKeyboardShown() {
        if (mTextEdit == null || !mScreenKeyboardShown) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) SDL.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isAcceptingText();
    }

    public static boolean supportsRelativeMouse() {
        if (Build.VERSION.SDK_INT >= 27 || !isDeXMode()) {
            return getMotionListener().supportsRelativeMouse();
        }
        return false;
    }

    public static boolean setRelativeMouseEnabled(boolean enabled) {
        if (!enabled || supportsRelativeMouse()) {
            return getMotionListener().setRelativeMouseEnabled(enabled);
        }
        return false;
    }

    public static boolean sendMessage(int command, int param) {
        if (mSingleton == null) {
            return false;
        }
        return mSingleton.sendCommand(command, Integer.valueOf(param));
    }

    public static Context getContext() {
        return SDL.getContext();
    }

    public static boolean isAndroidTV() {
        UiModeManager uiModeManager = (UiModeManager) getContext().getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == 4) {
            return true;
        }
        if (Build.MANUFACTURER.equals("MINIX") && Build.MODEL.equals("NEO-U1")) {
            return true;
        }
        if (Build.MANUFACTURER.equals("Amlogic") && Build.MODEL.equals("X96-W")) {
            return true;
        }
        return Build.MANUFACTURER.equals("Amlogic") && Build.MODEL.startsWith("TV");
    }

    public static double getDiagonal() {
        DisplayMetrics metrics = new DisplayMetrics();
        Activity activity = (Activity) getContext();
        if (activity == null) {
            return 0.0d;
        }
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        double dWidthInches = metrics.widthPixels / metrics.xdpi;
        double dHeightInches = metrics.heightPixels / metrics.ydpi;
        return Math.sqrt((dWidthInches * dWidthInches) + (dHeightInches * dHeightInches));
    }

    public static boolean isTablet() {
        return getDiagonal() >= 7.0d;
    }

    public static boolean isChromebook() {
        if (getContext() == null) {
            return false;
        }
        return getContext().getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
    }

    public static boolean isDeXMode() {
        if (Build.VERSION.SDK_INT < 24) {
            return false;
        }
        try {
            Configuration config = getContext().getResources().getConfiguration();
            Class<?> configClass = config.getClass();
            return configClass.getField("SEM_DESKTOP_MODE_ENABLED").getInt(configClass) == configClass.getField("semDesktopModeEnabled").getInt(config);
        } catch (Exception e) {
            return false;
        }
    }

    public static DisplayMetrics getDisplayDPI() {
        return getContext().getResources().getDisplayMetrics();
    }

    public static boolean getManifestEnvironmentVariables() {
        try {
            if (getContext() == null) {
                return false;
            }
            ApplicationInfo applicationInfo = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), 128);
            Bundle bundle = applicationInfo.metaData;
            if (bundle == null) {
                return false;
            }
            int trimLength = "SDL_ENV.".length();
            for (String key : bundle.keySet()) {
                if (key.startsWith("SDL_ENV.")) {
                    String name = key.substring(trimLength);
                    String value = bundle.get(key).toString();
                    nativeSetenv(name, value);
                }
            }
            return true;
        } catch (Exception e) {
            Log.v(TAG, "exception " + e.toString());
            return false;
        }
    }

    public static View getContentView() {
        return mLayout;
    }

    static class ShowTextInputTask implements Runnable {
        static final int HEIGHT_PADDING = 15;
        public int h;
        public int w;
        public int x;
        public int y;

        public ShowTextInputTask(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            if (this.w <= 0) {
                this.w = 1;
            }
            if (this.h + HEIGHT_PADDING <= 0) {
                this.h = -14;
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(this.w, this.h + HEIGHT_PADDING);
            params.leftMargin = this.x;
            params.topMargin = this.y;
            if (SDLActivity.mTextEdit == null) {
                SDLActivity.mTextEdit = new DummyEdit(SDL.getContext());
                SDLActivity.mLayout.addView(SDLActivity.mTextEdit, params);
            } else {
                SDLActivity.mTextEdit.setLayoutParams(params);
            }
            SDLActivity.mTextEdit.setVisibility(View.VISIBLE);
            SDLActivity.mTextEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) SDL.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(SDLActivity.mTextEdit, 0);
            SDLActivity.mScreenKeyboardShown = true;
        }
    }

    public static boolean showTextInput(int x, int y, int w, int h) {
        return mSingleton.commandHandler.post(new ShowTextInputTask(x, y, w, h));
    }

    public static boolean isTextInputEvent(KeyEvent event) {
        if (event.isCtrlPressed()) {
            return false;
        }
        return event.isPrintingKey() || event.getKeyCode() == 62;
    }

    public static Surface getNativeSurface() {
        if (mSurface == null) {
            return null;
        }
        return mSurface.getNativeSurface();
    }

    public static void initTouch() {
        int[] ids = InputDevice.getDeviceIds();
        for (int id : ids) {
            InputDevice device = InputDevice.getDevice(id);
            if (device != null && (device.getSources() & 4098) != 0) {
                nativeAddTouch(device.getId(), device.getName());
            }
        }
    }

    public int messageboxShowMessageBox(int flags, String title, String message, int[] buttonFlags, int[] buttonIds, String[] buttonTexts, int[] colors) {
        int i = -1;
        this.messageboxSelection[0] = -1;
        if (buttonFlags.length == buttonIds.length || buttonIds.length == buttonTexts.length) {
            final Bundle args = new Bundle();
            args.putInt("flags", flags);
            args.putString("title", title);
            args.putString("message", message);
            args.putIntArray("buttonFlags", buttonFlags);
            args.putIntArray("buttonIds", buttonIds);
            args.putStringArray("buttonTexts", buttonTexts);
            args.putIntArray("colors", colors);
            runOnUiThread(new Runnable() { // from class: org.libsdl.app.SDLActivity.5
                @Override // java.lang.Runnable
                public void run() {
                    SDLActivity.this.messageboxCreateAndShow(args);
                }
            });
            synchronized (this.messageboxSelection) {
                try {
                    this.messageboxSelection.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            i = this.messageboxSelection[0];
        }
        return i;
    }

    protected void messageboxCreateAndShow(Bundle args) {
        int backgroundColor;
        int textColor;
        int buttonBorderColor;
        int buttonBackgroundColor;
        int buttonSelectedColor;
        int[] colors = args.getIntArray("colors");
        if (colors != null) {
            int i = (-1) + 1;
            backgroundColor = colors[i];
            int i2 = i + 1;
            textColor = colors[i2];
            int i3 = i2 + 1;
            buttonBorderColor = colors[i3];
            int i4 = i3 + 1;
            buttonBackgroundColor = colors[i4];
            buttonSelectedColor = colors[i4 + 1];
        } else {
            backgroundColor = 0;
            textColor = 0;
            buttonBorderColor = 0;
            buttonBackgroundColor = 0;
            buttonSelectedColor = 0;
        }
        final AlertDialog create = new AlertDialog.Builder(this).create();
        create.setTitle(args.getString("title"));
        create.setCancelable(false);
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.libsdl.app.SDLActivity.6
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface unused) {
                synchronized (SDLActivity.this.messageboxSelection) {
                    SDLActivity.this.messageboxSelection.notify();
                }
            }
        });
        TextView message = new TextView(this);
        message.setGravity(17);
        message.setText(args.getString("message"));
        if (textColor != 0) {
            message.setTextColor(textColor);
        }
        int[] buttonFlags = args.getIntArray("buttonFlags");
        int[] buttonIds = args.getIntArray("buttonIds");
        String[] buttonTexts = args.getStringArray("buttonTexts");
        final SparseArray<Button> mapping = new SparseArray<>();
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(17);
        for (int i5 = 0; i5 < buttonTexts.length; i5++) {
            Button button = new Button(this);
            final int id = buttonIds[i5];
            button.setOnClickListener(new View.OnClickListener() { // from class: org.libsdl.app.SDLActivity.7
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    SDLActivity.this.messageboxSelection[0] = id;
                    create.dismiss();
                }
            });
            if (buttonFlags[i5] != 0) {
                if ((buttonFlags[i5] & 1) != 0) {
                    mapping.put(66, button);
                }
                if ((buttonFlags[i5] & 2) != 0) {
                    mapping.put(111, button);
                }
            }
            button.setText(buttonTexts[i5]);
            if (textColor != 0) {
                button.setTextColor(textColor);
            }
            if (buttonBorderColor != 0) {
            }
            if (buttonBackgroundColor != 0) {
                Drawable drawable = button.getBackground();
                if (drawable == null) {
                    button.setBackgroundColor(buttonBackgroundColor);
                } else {
                    drawable.setColorFilter(buttonBackgroundColor, PorterDuff.Mode.MULTIPLY);
                }
            }
            if (buttonSelectedColor != 0) {
            }
            buttons.addView(button);
        }
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(message);
        content.addView(buttons);
        if (backgroundColor != 0) {
            content.setBackgroundColor(backgroundColor);
        }
        create.setView(content);
        create.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: org.libsdl.app.SDLActivity.8
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
                Button button2 = (Button) mapping.get(keyCode);
                if (button2 == null) {
                    return false;
                }
                if (event.getAction() != 1) {
                    return true;
                }
                button2.performClick();
                return true;
            }
        });
        create.show();
    }

    @Override // android.view.View.OnSystemUiVisibilityChangeListener
    public void onSystemUiVisibilityChange(int visibility) {
        Handler handler;
        if (mFullscreenModeActive) {
            if (((visibility & 4) == 0 || (visibility & 2) == 0) && (handler = getWindow().getDecorView().getHandler()) != null) {
                handler.removeCallbacks(this.rehideSystemUi);
                handler.postDelayed(this.rehideSystemUi, 2000L);
            }
        }
    }

    public static boolean clipboardHasText() {
        return mClipboardHandler.clipboardHasText();
    }

    public static String clipboardGetText() {
        return mClipboardHandler.clipboardGetText();
    }

    public static void clipboardSetText(String string) {
        mClipboardHandler.clipboardSetText(string);
    }

    public static int createCustomCursor(int[] colors, int width, int height, int hotSpotX, int hotSpotY) {
        Bitmap bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
        mLastCursorID++;
        if (Build.VERSION.SDK_INT < 24) {
            return 0;
        }
        try {
            mCursors.put(Integer.valueOf(mLastCursorID), PointerIcon.create(bitmap, hotSpotX, hotSpotY));
            return mLastCursorID;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean setCustomCursor(int cursorID) {
        if (Build.VERSION.SDK_INT < 24) {
            return false;
        }
        try {
            mSurface.setPointerIcon(mCursors.get(Integer.valueOf(cursorID)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean setSystemCursor(int cursorID) {
        int cursor_type = 0;
        switch (cursorID) {
            case 0:
                cursor_type = 1000;
                break;
            case 1:
                cursor_type = 1008;
                break;
            case 2:
                cursor_type = 1004;
                break;
            case 3:
                cursor_type = 1007;
                break;
            case 4:
                cursor_type = 1004;
                break;
            case 5:
                cursor_type = 1017;
                break;
            case SDL_SYSTEM_CURSOR_SIZENESW /* 6 */:
                cursor_type = 1016;
                break;
            case SDL_SYSTEM_CURSOR_SIZEWE /* 7 */:
                cursor_type = 1014;
                break;
            case SDL_SYSTEM_CURSOR_SIZENS /* 8 */:
                cursor_type = 1015;
                break;
            case SDL_SYSTEM_CURSOR_SIZEALL /* 9 */:
                cursor_type = 1020;
                break;
            case SDL_SYSTEM_CURSOR_NO /* 10 */:
                cursor_type = 1012;
                break;
            case SDL_SYSTEM_CURSOR_HAND /* 11 */:
                cursor_type = 1002;
                break;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                mSurface.setPointerIcon(PointerIcon.getSystemIcon(SDL.getContext(), cursor_type));
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public static void requestPermission(String permission, int requestCode) {
        if (Build.VERSION.SDK_INT < 23) {
            nativePermissionResult(requestCode, true);
            return;
        }
        Activity activity = (Activity) getContext();
        if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{permission}, requestCode);
        } else {
            nativePermissionResult(requestCode, true);
        }
    }

    @SuppressLint("WrongConstant")
    public static int openURL(String url) {
        int flags;
        try {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse(url));
            if (Build.VERSION.SDK_INT >= 21) {
                flags = 1207959552 | 524288;
            } else {
                flags = 1207959552 | 524288;
            }
            i.addFlags(flags);
            mSingleton.startActivity(i);
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public static int showToast(String message, int duration, int gravity, int xOffset, int yOffset) {
        if (mSingleton == null) {
            return -1;
        }
        try {
            mSingleton.runOnUiThread(new Runnable() { // from class: org.libsdl.app.SDLActivity.1OneShotTask
                int mDuration;
                int mGravity;
                String mMessage;
                int mXOffset;
                int mYOffset;

                {
                    this.mMessage = message;
                    this.mDuration = duration;
                    this.mGravity = gravity;
                    this.mXOffset = xOffset;
                    this.mYOffset = yOffset;
                }

                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Toast toast = Toast.makeText(SDLActivity.mSingleton, this.mMessage, this.mDuration);
                        if (this.mGravity >= 0) {
                            toast.setGravity(this.mGravity, this.mXOffset, this.mYOffset);
                        }
                        toast.show();
                    } catch (Exception ex) {
                        Log.e(SDLActivity.TAG, ex.getMessage());
                    }
                }
            });
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }
}
class SDLClipboardHandler implements ClipboardManager.OnPrimaryClipChangedListener {
    protected ClipboardManager mClipMgr = (ClipboardManager) SDL.getContext().getSystemService(Context.CLIPBOARD_SERVICE);

    SDLClipboardHandler() {
        this.mClipMgr.addPrimaryClipChangedListener(this);
    }

    public boolean clipboardHasText() {
        return this.mClipMgr.hasPrimaryClip();
    }

    public String clipboardGetText() {
        ClipData.Item item;
        CharSequence text;
        ClipData clip = this.mClipMgr.getPrimaryClip();
        if (clip == null || (item = clip.getItemAt(0)) == null || (text = item.getText()) == null) {
            return null;
        }
        return text.toString();
    }

    public void clipboardSetText(String string) {
        this.mClipMgr.removePrimaryClipChangedListener(this);
        ClipData clip = ClipData.newPlainText(null, string);
        this.mClipMgr.setPrimaryClip(clip);
        this.mClipMgr.addPrimaryClipChangedListener(this);
    }

    @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
    public void onPrimaryClipChanged() {
        SDLActivity.onNativeClipboardChanged();
    }
}


/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLGenericMotionListener_API12 implements View.OnGenericMotionListener {
    SDLGenericMotionListener_API12() {
    }

    @Override // android.view.View.OnGenericMotionListener
    public boolean onGenericMotion(View v, MotionEvent event) {
        switch (event.getSource()) {
            case 513:
            case 1025:
            case 16777232:
                return SDLControllerManager.handleJoystickMotionEvent(event);
            case 8194:
                int action = event.getActionMasked();
                switch (action) {
                    case 7:
                        float x = event.getX(0);
                        float y = event.getY(0);
                        SDLActivity.onNativeMouse(0, action, x, y, false);
                        return true;
                    case 8:
                        float x2 = event.getAxisValue(10, 0);
                        float y2 = event.getAxisValue(9, 0);
                        SDLActivity.onNativeMouse(0, action, x2, y2, false);
                        return true;
                }
        }
        return false;
    }

    public boolean supportsRelativeMouse() {
        return false;
    }

    public boolean inRelativeMode() {
        return false;
    }

    public boolean setRelativeMouseEnabled(boolean enabled) {
        return false;
    }

    public void reclaimRelativeMouseModeIfNeeded() {
    }

    public float getEventX(MotionEvent event) {
        return event.getX(0);
    }

    public float getEventY(MotionEvent event) {
        return event.getY(0);
    }
}


class SDLSurface extends SurfaceView implements SurfaceHolder.Callback, View.OnKeyListener, View.OnTouchListener, SensorEventListener {
    protected Display mDisplay;
    protected float mHeight;
    public boolean mIsSurfaceReady;
    protected SensorManager mSensorManager;
    protected float mWidth;

    public SDLSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        this.mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        setOnGenericMotionListener(SDLActivity.getMotionListener());
        this.mWidth = 1.0f;
        this.mHeight = 1.0f;
        this.mIsSurfaceReady = false;
    }

    public void handlePause() {
        enableSensor(1, false);
    }

    public void handleResume() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        enableSensor(1, true);
    }

    public Surface getNativeSurface() {
        return getHolder().getSurface();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("SDL", "surfaceCreated()");
        SDLActivity.onNativeSurfaceCreated();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("SDL", "surfaceDestroyed()");
        SDLActivity.mNextNativeState = SDLActivity.NativeState.PAUSED;
        SDLActivity.handleNativeState();
        this.mIsSurfaceReady = false;
        SDLActivity.onNativeSurfaceDestroyed();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v("SDL", "surfaceChanged()");
        if (SDLActivity.mSingleton != null) {
            this.mWidth = width;
            this.mHeight = height;
            int nDeviceWidth = width;
            int nDeviceHeight = height;
            try {
                if (Build.VERSION.SDK_INT >= 17) {
                    DisplayMetrics realMetrics = new DisplayMetrics();
                    this.mDisplay.getRealMetrics(realMetrics);
                    nDeviceWidth = realMetrics.widthPixels;
                    nDeviceHeight = realMetrics.heightPixels;
                }
            } catch (Exception e) {
            }
            synchronized (SDLActivity.getContext()) {
                SDLActivity.getContext().notifyAll();
            }
            Log.v("SDL", "Window size: " + width + "x" + height);
            Log.v("SDL", "Device size: " + nDeviceWidth + "x" + nDeviceHeight);
            SDLActivity.nativeSetScreenResolution(width, height, nDeviceWidth, nDeviceHeight, this.mDisplay.getRefreshRate());
            SDLActivity.onNativeResize();
            boolean skip = false;
            int requestedOrientation = SDLActivity.mSingleton.getRequestedOrientation();
            if (requestedOrientation == 1 || requestedOrientation == 7) {
                if (this.mWidth > this.mHeight) {
                    skip = true;
                }
            } else if ((requestedOrientation == 0 || requestedOrientation == 6) && this.mWidth < this.mHeight) {
                skip = true;
            }
            if (skip) {
                double min = Math.min(this.mWidth, this.mHeight);
                double max = Math.max(this.mWidth, this.mHeight);
                if (max / min < 1.2d) {
                    Log.v("SDL", "Don't skip on such aspect-ratio. Could be a square resolution.");
                    skip = false;
                }
            }
            if (skip && Build.VERSION.SDK_INT >= 24 && SDLActivity.mSingleton.isInMultiWindowMode()) {
                Log.v("SDL", "Don't skip in Multi-Window");
                skip = false;
            }
            if (skip) {
                Log.v("SDL", "Skip .. Surface is not ready.");
                this.mIsSurfaceReady = false;
            } else {
                SDLActivity.onNativeSurfaceChanged();
                this.mIsSurfaceReady = true;
                SDLActivity.mNextNativeState = SDLActivity.NativeState.RESUMED;
                SDLActivity.handleNativeState();
            }
        }
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        InputDevice device;
        int deviceId = event.getDeviceId();
        int source = event.getSource();
        if (source == 0 && (device = InputDevice.getDevice(deviceId)) != null) {
            source = device.getSources();
        }
        if (keyCode == 4) {
            keyCode = 111;
        }
        if (SDLControllerManager.isDeviceSDLJoystick(deviceId)) {
            if (event.getAction() == 0) {
                if (SDLControllerManager.onNativePadDown(deviceId, keyCode) == 0) {
                    return true;
                }
            } else if (event.getAction() == 1 && SDLControllerManager.onNativePadUp(deviceId, keyCode) == 0) {
                return true;
            }
        }
        if ((source & 257) != 0) {
            if (event.getAction() == 0) {
                if (SDLActivity.isTextInputEvent(event)) {
                    SDLInputConnection.nativeCommitText(String.valueOf((char) event.getUnicodeChar()), 1);
                }
                SDLActivity.onNativeKeyDown(keyCode);
                return true;
            }
            if (event.getAction() == 1) {
                SDLActivity.onNativeKeyUp(keyCode);
                return true;
            }
        }
        if ((source & 8194) != 0 && (keyCode == 4 || keyCode == 125)) {
            switch (event.getAction()) {
            }
            return true;
        }
        return false;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        int touchDevId = event.getDeviceId();
        int pointerCount = event.getPointerCount();
        int action = event.getActionMasked();
        int i = -1;
        if (touchDevId < 0) {
            touchDevId--;
        }
        if (event.getSource() == 8194 || event.getSource() == 12290) {
            int mouseButton = 1;
            try {
                Object object = event.getClass().getMethod("getButtonState", new Class[0]).invoke(event, new Object[0]);
                if (object != null) {
                    mouseButton = ((Integer) object).intValue();
                }
            } catch (Exception e) {
            }
            SDLGenericMotionListener_API12 motionListener = SDLActivity.getMotionListener();
            float x = motionListener.getEventX(event);
            float y = motionListener.getEventY(event);
            SDLActivity.onNativeMouse(mouseButton, action, x, y, motionListener.inRelativeMode());
            return true;
        }
        switch (action) {
            case 0:
            case 1:
                i = 0;
                break;
            case 2:
                for (int i2 = 0; i2 < pointerCount; i2++) {
                    int pointerFingerId = event.getPointerId(i2);
                    float x2 = event.getX(i2) / this.mWidth;
                    float y2 = event.getY(i2) / this.mHeight;
                    float p = event.getPressure(i2);
                    if (p > 1.0f) {
                        p = 1.0f;
                    }
                    SDLActivity.onNativeTouch(touchDevId, pointerFingerId, action, x2, y2, p);
                }
                return true;
            case 3:
                for (int i3 = 0; i3 < pointerCount; i3++) {
                    int pointerFingerId2 = event.getPointerId(i3);
                    float x3 = event.getX(i3) / this.mWidth;
                    float y3 = event.getY(i3) / this.mHeight;
                    float p2 = event.getPressure(i3);
                    if (p2 > 1.0f) {
                        p2 = 1.0f;
                    }
                    SDLActivity.onNativeTouch(touchDevId, pointerFingerId2, 1, x3, y3, p2);
                }
                return true;
            case 4:
            default:
                return true;
            case 5:
            case 6:
                break;
        }
        if (i == -1) {
            i = event.getActionIndex();
        }
        int pointerFingerId3 = event.getPointerId(i);
        float x4 = event.getX(i) / this.mWidth;
        float y4 = event.getY(i) / this.mHeight;
        float p3 = event.getPressure(i);
        if (p3 > 1.0f) {
            p3 = 1.0f;
        }
        SDLActivity.onNativeTouch(touchDevId, pointerFingerId3, action, x4, y4, p3);
        return true;
    }

    public void enableSensor(int sensortype, boolean enabled) {
        if (enabled) {
            this.mSensorManager.registerListener(this, this.mSensorManager.getDefaultSensor(sensortype), 1, (Handler) null);
        } else {
            this.mSensorManager.unregisterListener(this, this.mSensorManager.getDefaultSensor(sensortype));
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent event) {
        float x;
        float y;
        int newOrientation;
        if (event.sensor.getType() == 1) {
            switch (this.mDisplay.getRotation()) {
                case 1:
                    x = -event.values[1];
                    y = event.values[0];
                    newOrientation = 1;
                    break;
                case 2:
                    x = -event.values[0];
                    y = -event.values[1];
                    newOrientation = 4;
                    break;
                case 3:
                    x = event.values[1];
                    y = -event.values[0];
                    newOrientation = 2;
                    break;
                default:
                    x = event.values[0];
                    y = event.values[1];
                    newOrientation = 3;
                    break;
            }
            if (newOrientation != SDLActivity.mCurrentOrientation) {
                SDLActivity.mCurrentOrientation = newOrientation;
                SDLActivity.onNativeOrientationChanged(newOrientation);
            }
            SDLActivity.onNativeAccel((-x) / 9.80665f, y / 9.80665f, event.values[2] / 9.80665f);
        }
    }

    @Override // android.view.View
    public boolean onCapturedPointerEvent(MotionEvent event) {
        int action;
        int action2 = event.getActionMasked();
        switch (action2) {
            case 2:
            case 7:
                float x = event.getX(0);
                float y = event.getY(0);
                SDLActivity.onNativeMouse(0, action2, x, y, true);
                return true;
            case 3:
            case 4:
            case 5:
            case 6:
            case 9:
            case 10:
            default:
                return false;
            case 8:
                float x2 = event.getAxisValue(10, 0);
                float y2 = event.getAxisValue(9, 0);
                SDLActivity.onNativeMouse(0, action2, x2, y2, false);
                return true;
            case 11:
            case 12:
                if (action2 == 11) {
                    action = 0;
                } else {
                    action = 1;
                }
                float x3 = event.getX(0);
                float y3 = event.getY(0);
                int button = event.getButtonState();
                SDLActivity.onNativeMouse(button, action, x3, y3, true);
                return true;
        }
    }
}

class SDLGenericMotionListener_API26 extends SDLGenericMotionListener_API24 {
    private boolean mRelativeModeEnabled;

    SDLGenericMotionListener_API26() {
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API12, android.view.View.OnGenericMotionListener
    public boolean onGenericMotion(View v, MotionEvent event) {
        switch (event.getSource()) {
            case 513:
            case 1025:
            case 16777232:
                return SDLControllerManager.handleJoystickMotionEvent(event);
            case 8194:
            case 12290:
                int action = event.getActionMasked();
                switch (action) {
                    case 7:
                        float x = event.getX(0);
                        float y = event.getY(0);
                        SDLActivity.onNativeMouse(0, action, x, y, false);
                        return true;
                    case 8:
                        float x2 = event.getAxisValue(10, 0);
                        float y2 = event.getAxisValue(9, 0);
                        SDLActivity.onNativeMouse(0, action, x2, y2, false);
                        return true;
                }
            case 131076:
                int action2 = event.getActionMasked();
                switch (action2) {
                    case 7:
                        float x3 = event.getX(0);
                        float y3 = event.getY(0);
                        SDLActivity.onNativeMouse(0, action2, x3, y3, true);
                        return true;
                    case 8:
                        float x4 = event.getAxisValue(10, 0);
                        float y4 = event.getAxisValue(9, 0);
                        SDLActivity.onNativeMouse(0, action2, x4, y4, false);
                        return true;
                }
        }
        return false;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API12
    public boolean supportsRelativeMouse() {
        return !SDLActivity.isDeXMode() || Build.VERSION.SDK_INT >= 27;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API12
    public boolean inRelativeMode() {
        return this.mRelativeModeEnabled;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API12
    public boolean setRelativeMouseEnabled(boolean enabled) {
        if (!SDLActivity.isDeXMode() || Build.VERSION.SDK_INT >= 27) {
            if (enabled) {
                SDLActivity.getContentView().requestPointerCapture();
            } else {
                SDLActivity.getContentView().releasePointerCapture();
            }
            this.mRelativeModeEnabled = enabled;
            return true;
        }
        return false;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API12
    public void reclaimRelativeMouseModeIfNeeded() {
        if (this.mRelativeModeEnabled && !SDLActivity.isDeXMode()) {
            SDLActivity.getContentView().requestPointerCapture();
        }
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API12
    public float getEventX(MotionEvent event) {
        return event.getX(0);
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API12
    public float getEventY(MotionEvent event) {
        return event.getY(0);
    }
}

class SDLGenericMotionListener_API24 extends SDLGenericMotionListener_API12 {
    private boolean mRelativeModeEnabled;

    SDLGenericMotionListener_API24() {
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API12, android.view.View.OnGenericMotionListener
    public boolean onGenericMotion(View v, MotionEvent event) {
        int action;
        if (!this.mRelativeModeEnabled || event.getSource() != 8194 || (action = event.getActionMasked()) != 7) {
            return super.onGenericMotion(v, event);
        }
        float x = event.getAxisValue(27);
        float y = event.getAxisValue(28);
        SDLActivity.onNativeMouse(0, action, x, y, true);
        return true;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API12
    public boolean supportsRelativeMouse() {
        return true;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API12
    public boolean inRelativeMode() {
        return this.mRelativeModeEnabled;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API12
    public boolean setRelativeMouseEnabled(boolean enabled) {
        this.mRelativeModeEnabled = enabled;
        return true;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API12
    public float getEventX(MotionEvent event) {
        return this.mRelativeModeEnabled ? event.getAxisValue(27) : event.getX(0);
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API12
    public float getEventY(MotionEvent event) {
        return this.mRelativeModeEnabled ? event.getAxisValue(28) : event.getY(0);
    }
}

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

class DummyEdit extends View implements View.OnKeyListener {
    InputConnection ic;

    public DummyEdit(Context context) {
        super(context);
        setFocusableInTouchMode(true);
        setFocusable(true);
        setOnKeyListener(this);
    }

    @Override // android.view.View
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == 0) {
            if (SDLActivity.isTextInputEvent(event)) {
                this.ic.commitText(String.valueOf((char) event.getUnicodeChar()), 1);
                return true;
            }
            SDLActivity.onNativeKeyDown(keyCode);
            return true;
        }
        if (event.getAction() == 1) {
            SDLActivity.onNativeKeyUp(keyCode);
            return true;
        }
        return false;
    }

    @Override // android.view.View
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getAction() == 1 && keyCode == 4 && SDLActivity.mTextEdit != null && SDLActivity.mTextEdit.getVisibility() == 0) {
            SDLActivity.onNativeKeyboardFocusLost();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        this.ic = new SDLInputConnection(this, true);
        outAttrs.inputType = 145;
        outAttrs.imeOptions = 301989888;
        return this.ic;
    }
}

class SDLInputConnection extends BaseInputConnection {
    public static native void nativeCommitText(String str, int i);

    public native void nativeGenerateScancodeForUnichar(char c);

    public native void nativeSetComposingText(String str, int i);

    public SDLInputConnection(View targetView, boolean fullEditor) {
        super(targetView, fullEditor);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean sendKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 66 && SDLActivity.onNativeSoftReturnKey()) {
            return true;
        }
        return super.sendKeyEvent(event);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence text, int newCursorPosition) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n' && SDLActivity.onNativeSoftReturnKey()) {
                return true;
            }
            nativeGenerateScancodeForUnichar(c);
        }
        nativeCommitText(text.toString(), newCursorPosition);
        return super.commitText(text, newCursorPosition);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        nativeSetComposingText(text.toString(), newCursorPosition);
        return super.setComposingText(text, newCursorPosition);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        if (beforeLength > 0 && afterLength == 0) {
            boolean ret = true;
            while (true) {
                int beforeLength2 = beforeLength;
                beforeLength = beforeLength2 - 1;
                if (beforeLength2 <= 0) {
                    return ret;
                }
                boolean ret_key = sendKeyEvent(new KeyEvent(0, 67)) && sendKeyEvent(new KeyEvent(1, 67));
                ret = ret && ret_key;
            }
        } else {
            boolean ret2 = super.deleteSurroundingText(beforeLength, afterLength);
            return ret2;
        }
    }
}


class SDLJoystickHandler_API19 extends SDLJoystickHandler_API16 {
    SDLJoystickHandler_API19() {
    }

    @Override // org.libsdl.app.SDLJoystickHandler_API16
    public int getProductId(InputDevice joystickDevice) {
        return joystickDevice.getProductId();
    }

    @Override // org.libsdl.app.SDLJoystickHandler_API16
    public int getVendorId(InputDevice joystickDevice) {
        return joystickDevice.getVendorId();
    }

    @Override // org.libsdl.app.SDLJoystickHandler_API16
    public int getButtonMask(InputDevice joystickDevice) {
        int button_mask = 0;
        int[] keys = {96, 97, 99, 100, 4, 110, 108, 106, 107, 102, 103, 19, 20, 21, 22, 109, 23, 104, 105, 98, 101, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203};
        int[] masks = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 16, 1, 32768, 65536, 131072, 262144, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, Integer.MIN_VALUE, -1, -1, -1, -1};
        boolean[] has_keys = joystickDevice.hasKeys(keys);
        for (int i = 0; i < keys.length; i++) {
            if (has_keys[i]) {
                button_mask |= masks[i];
            }
        }
        return button_mask;
    }
}


class SDLJoystickHandler_API16 extends SDLJoystickHandler {
    private final ArrayList<SDLJoystick> mJoysticks = new ArrayList<>();

    /* compiled from: SDLControllerManager.java */
    static class SDLJoystick {
        public ArrayList<InputDevice.MotionRange> axes;
        public String desc;
        public int device_id;
        public ArrayList<InputDevice.MotionRange> hats;
        public String name;

        SDLJoystick() {
        }
    }

    /* compiled from: SDLControllerManager.java */
    static class RangeComparator implements Comparator<InputDevice.MotionRange> {
        RangeComparator() {
        }

        @Override // java.util.Comparator
        public int compare(InputDevice.MotionRange arg0, InputDevice.MotionRange arg1) {
            int arg0Axis = arg0.getAxis();
            int arg1Axis = arg1.getAxis();
            if (arg0Axis == 22) {
                arg0Axis = 23;
            } else if (arg0Axis == 23) {
                arg0Axis = 22;
            }
            if (arg1Axis == 22) {
                arg1Axis = 23;
            } else if (arg1Axis == 23) {
                arg1Axis = 22;
            }
            return arg0Axis - arg1Axis;
        }
    }

    @Override // org.libsdl.app.SDLJoystickHandler
    public void pollInputDevices() {
        int[] deviceIds = InputDevice.getDeviceIds();
        int length = deviceIds.length;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= length) {
                break;
            }
            int device_id = deviceIds[i2];
            if (SDLControllerManager.isDeviceSDLJoystick(device_id)) {
                SDLJoystick joystick = getJoystick(device_id);
                if (joystick == null) {
                    InputDevice joystickDevice = InputDevice.getDevice(device_id);
                    SDLJoystick joystick2 = new SDLJoystick();
                    joystick2.device_id = device_id;
                    joystick2.name = joystickDevice.getName();
                    joystick2.desc = getJoystickDescriptor(joystickDevice);
                    joystick2.axes = new ArrayList<>();
                    joystick2.hats = new ArrayList<>();
                    List<InputDevice.MotionRange> ranges = joystickDevice.getMotionRanges();
                    Collections.sort(ranges, new RangeComparator());
                    for (InputDevice.MotionRange range : ranges) {
                        if ((range.getSource() & 16) != 0) {
                            if (range.getAxis() == 15 || range.getAxis() == 16) {
                                joystick2.hats.add(range);
                            } else {
                                joystick2.axes.add(range);
                            }
                        }
                    }
                    this.mJoysticks.add(joystick2);
                    SDLControllerManager.nativeAddJoystick(joystick2.device_id, joystick2.name, joystick2.desc, getVendorId(joystickDevice), getProductId(joystickDevice), false, getButtonMask(joystickDevice), joystick2.axes.size(), joystick2.hats.size() / 2, 0);
                }
            }
            i = i2 + 1;
        }
        ArrayList<Integer> removedDevices = null;
        Iterator<SDLJoystick> it = this.mJoysticks.iterator();
        while (it.hasNext()) {
            SDLJoystick joystick3 = it.next();
            int device_id2 = joystick3.device_id;
            int i3 = 0;
            while (i3 < deviceIds.length && device_id2 != deviceIds[i3]) {
                i3++;
            }
            if (i3 == deviceIds.length) {
                if (removedDevices == null) {
                    removedDevices = new ArrayList<>();
                }
                removedDevices.add(Integer.valueOf(device_id2));
            }
        }
        if (removedDevices != null) {
            Iterator<Integer> it2 = removedDevices.iterator();
            while (it2.hasNext()) {
                int device_id3 = it2.next().intValue();
                SDLControllerManager.nativeRemoveJoystick(device_id3);
                int i4 = 0;
                while (true) {
                    if (i4 >= this.mJoysticks.size()) {
                        break;
                    }
                    if (this.mJoysticks.get(i4).device_id != device_id3) {
                        i4++;
                    } else {
                        this.mJoysticks.remove(i4);
                        break;
                    }
                }
            }
        }
    }

    protected SDLJoystick getJoystick(int device_id) {
        Iterator<SDLJoystick> it = this.mJoysticks.iterator();
        while (it.hasNext()) {
            SDLJoystick joystick = it.next();
            if (joystick.device_id == device_id) {
                return joystick;
            }
        }
        return null;
    }

    @Override // org.libsdl.app.SDLJoystickHandler
    public boolean handleMotionEvent(MotionEvent event) {
        SDLJoystick joystick;
        if ((event.getSource() & 16777232) != 0) {
            int actionPointerIndex = event.getActionIndex();
            int action = event.getActionMasked();
            if (action == 2 && (joystick = getJoystick(event.getDeviceId())) != null) {
                for (int i = 0; i < joystick.axes.size(); i++) {
                    InputDevice.MotionRange range = joystick.axes.get(i);
                    float value = (((event.getAxisValue(range.getAxis(), actionPointerIndex) - range.getMin()) / range.getRange()) * 2.0f) - 1.0f;
                    SDLControllerManager.onNativeJoy(joystick.device_id, i, value);
                }
                for (int i2 = 0; i2 < joystick.hats.size() / 2; i2++) {
                    int hatX = Math.round(event.getAxisValue(joystick.hats.get(i2 * 2).getAxis(), actionPointerIndex));
                    int hatY = Math.round(event.getAxisValue(joystick.hats.get((i2 * 2) + 1).getAxis(), actionPointerIndex));
                    SDLControllerManager.onNativeHat(joystick.device_id, i2, hatX, hatY);
                }
                return true;
            }
            return true;
        }
        return true;
    }

    public String getJoystickDescriptor(InputDevice joystickDevice) {
        String desc = joystickDevice.getDescriptor();
        return (desc == null || desc.isEmpty()) ? joystickDevice.getName() : desc;
    }

    public int getProductId(InputDevice joystickDevice) {
        return 0;
    }

    public int getVendorId(InputDevice joystickDevice) {
        return 0;
    }

    public int getButtonMask(InputDevice joystickDevice) {
        return -1;
    }
}
class SDLHapticHandler_API26 extends SDLHapticHandler {
    SDLHapticHandler_API26() {
    }

    @Override // org.libsdl.app.SDLHapticHandler
    public void run(int device_id, float intensity, int length) {
        SDLHaptic haptic = getHaptic(device_id);
        if (haptic != null) {
            Log.d("SDL", "Rtest: Vibe with intensity " + intensity + " for " + length);
            if (intensity == 0.0f) {
                stop(device_id);
                return;
            }
            int vibeValue = Math.round(255.0f * intensity);
            if (vibeValue > 255) {
                vibeValue = 255;
            }
            if (vibeValue < 1) {
                stop(device_id);
                return;
            }
            try {
                haptic.vib.vibrate(VibrationEffect.createOneShot(length, vibeValue));
            } catch (Exception e) {
                haptic.vib.vibrate(length);
            }
        }
    }
}
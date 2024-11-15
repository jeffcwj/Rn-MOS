package org.libsdl.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import org.libsdl.app.SDLActivity;

/* compiled from: SDLActivity.java */
/* loaded from: classes.dex */
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
        this.mDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
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

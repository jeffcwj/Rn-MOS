package org.libsdl.app;

import android.os.Build;
import android.os.Vibrator;
import android.view.InputDevice;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class SDLControllerManager {
    private static final String TAG = "SDLControllerManager";
    protected static SDLHapticHandler mHapticHandler;
    protected static SDLJoystickHandler mJoystickHandler;

    public static native int nativeAddHaptic(int i, String str);

    public static native int nativeAddJoystick(int i, String str, String str2, int i2, int i3, boolean z, int i4, int i5, int i6, int i7);

    public static native int nativeRemoveHaptic(int i);

    public static native int nativeRemoveJoystick(int i);

    public static native int nativeSetupJNI();

    public static native void onNativeHat(int i, int i2, int i3, int i4);

    public static native void onNativeJoy(int i, int i2, float f);

    public static native int onNativePadDown(int i, int i2);

    public static native int onNativePadUp(int i, int i2);

    public static void initialize() {
        if (mJoystickHandler == null) {
            if (Build.VERSION.SDK_INT >= 19) {
                mJoystickHandler = new SDLJoystickHandler_API19();
            } else {
                mJoystickHandler = new SDLJoystickHandler_API16();
            }
        }
        if (mHapticHandler == null) {
            if (Build.VERSION.SDK_INT >= 26) {
                mHapticHandler = new SDLHapticHandler_API26();
            } else {
                mHapticHandler = new SDLHapticHandler();
            }
        }
    }

    public static boolean handleJoystickMotionEvent(MotionEvent event) {
        return mJoystickHandler.handleMotionEvent(event);
    }

    public static void pollInputDevices() {
        mJoystickHandler.pollInputDevices();
    }

    public static void pollHapticDevices() {
        mHapticHandler.pollHapticDevices();
    }

    public static void hapticRun(int device_id, float intensity, int length) {
        mHapticHandler.run(device_id, intensity, length);
    }

    public static void hapticStop(int device_id) {
        mHapticHandler.stop(device_id);
    }

    public static boolean isDeviceSDLJoystick(int deviceId) {
        InputDevice device = InputDevice.getDevice(deviceId);
        if (device == null || deviceId < 0) {
            return false;
        }
        int sources = device.getSources();
        return (sources & 16) != 0 || (sources & 513) == 513 || (sources & 1025) == 1025;
    }
}


class SDLJoystickHandler {
    SDLJoystickHandler() {
    }

    public boolean handleMotionEvent(MotionEvent event) {
        return false;
    }

    public void pollInputDevices() {
    }
}

class SDLHapticHandler {
    private final ArrayList<SDLHaptic> mHaptics = new ArrayList<>();

    /* compiled from: SDLControllerManager.java */
    static class SDLHaptic {
        public int device_id;
        public String name;
        public Vibrator vib;

        SDLHaptic() {
        }
    }

    public void run(int device_id, float intensity, int length) {
        SDLHaptic haptic = getHaptic(device_id);
        if (haptic != null) {
            haptic.vib.vibrate(length);
        }
    }

    public void stop(int device_id) {
        SDLHaptic haptic = getHaptic(device_id);
        if (haptic != null) {
            haptic.vib.cancel();
        }
    }

    public void pollHapticDevices() {
        boolean hasVibratorService = false;
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int i = deviceIds.length - 1; i > -1; i--) {
            SDLHaptic haptic = getHaptic(deviceIds[i]);
            if (haptic == null) {
                InputDevice device = InputDevice.getDevice(deviceIds[i]);
                Vibrator vib = device.getVibrator();
                if (vib.hasVibrator()) {
                    SDLHaptic haptic2 = new SDLHaptic();
                    haptic2.device_id = deviceIds[i];
                    haptic2.name = device.getName();
                    haptic2.vib = vib;
                    this.mHaptics.add(haptic2);
                    SDLControllerManager.nativeAddHaptic(haptic2.device_id, haptic2.name);
                }
            }
        }
        Vibrator vib2 = (Vibrator) SDL.getContext().getSystemService("vibrator");
        if (vib2 != null && (hasVibratorService = vib2.hasVibrator())) {
            SDLHaptic haptic3 = getHaptic(999999);
            if (haptic3 == null) {
                SDLHaptic haptic4 = new SDLHaptic();
                haptic4.device_id = 999999;
                haptic4.name = "VIBRATOR_SERVICE";
                haptic4.vib = vib2;
                this.mHaptics.add(haptic4);
                SDLControllerManager.nativeAddHaptic(haptic4.device_id, haptic4.name);
            }
        }
        ArrayList<Integer> removedDevices = null;
        Iterator<SDLHaptic> it = this.mHaptics.iterator();
        while (it.hasNext()) {
            SDLHaptic haptic5 = it.next();
            int device_id = haptic5.device_id;
            int i2 = 0;
            while (i2 < deviceIds.length && device_id != deviceIds[i2]) {
                i2++;
            }
            if (device_id != 999999 || !hasVibratorService) {
                if (i2 == deviceIds.length) {
                    if (removedDevices == null) {
                        removedDevices = new ArrayList<>();
                    }
                    removedDevices.add(Integer.valueOf(device_id));
                }
            }
        }
        if (removedDevices != null) {
            Iterator<Integer> it2 = removedDevices.iterator();
            while (it2.hasNext()) {
                int device_id2 = it2.next().intValue();
                SDLControllerManager.nativeRemoveHaptic(device_id2);
                int i3 = 0;
                while (true) {
                    if (i3 >= this.mHaptics.size()) {
                        break;
                    }
                    if (this.mHaptics.get(i3).device_id != device_id2) {
                        i3++;
                    } else {
                        this.mHaptics.remove(i3);
                        break;
                    }
                }
            }
        }
    }

    protected SDLHaptic getHaptic(int device_id) {
        Iterator<SDLHaptic> it = this.mHaptics.iterator();
        while (it.hasNext()) {
            SDLHaptic haptic = it.next();
            if (haptic.device_id == device_id) {
                return haptic;
            }
        }
        return null;
    }
}

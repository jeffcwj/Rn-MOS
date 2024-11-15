package org.libsdl.app;

import android.os.Vibrator;
import android.view.InputDevice;
import java.util.ArrayList;
import java.util.Iterator;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
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

package org.libsdl.app;

import android.view.InputDevice;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
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

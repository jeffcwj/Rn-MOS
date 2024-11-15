package org.libsdl.app;

import android.view.InputDevice;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
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

package org.libsdl.app;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
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

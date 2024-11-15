package org.libsdl.app;

import android.view.MotionEvent;
import android.view.View;

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

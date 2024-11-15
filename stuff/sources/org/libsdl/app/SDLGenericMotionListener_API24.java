package org.libsdl.app;

import android.view.MotionEvent;
import android.view.View;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
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

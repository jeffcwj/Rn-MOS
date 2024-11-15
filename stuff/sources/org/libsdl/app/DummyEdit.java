package org.libsdl.app;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/* compiled from: SDLActivity.java */
/* loaded from: classes.dex */
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

package org.libsdl.app;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;

/* compiled from: SDLActivity.java */
/* loaded from: classes.dex */
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

package org.libsdl.app;

import android.content.ClipData;
import android.content.ClipboardManager;

/* compiled from: SDLActivity.java */
/* loaded from: classes.dex */
class SDLClipboardHandler implements ClipboardManager.OnPrimaryClipChangedListener {
    protected ClipboardManager mClipMgr = (ClipboardManager) SDL.getContext().getSystemService("clipboard");

    SDLClipboardHandler() {
        this.mClipMgr.addPrimaryClipChangedListener(this);
    }

    public boolean clipboardHasText() {
        return this.mClipMgr.hasPrimaryClip();
    }

    public String clipboardGetText() {
        ClipData.Item item;
        CharSequence text;
        ClipData clip = this.mClipMgr.getPrimaryClip();
        if (clip == null || (item = clip.getItemAt(0)) == null || (text = item.getText()) == null) {
            return null;
        }
        return text.toString();
    }

    public void clipboardSetText(String string) {
        this.mClipMgr.removePrimaryClipChangedListener(this);
        ClipData clip = ClipData.newPlainText(null, string);
        this.mClipMgr.setPrimaryClip(clip);
        this.mClipMgr.addPrimaryClipChangedListener(this);
    }

    @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
    public void onPrimaryClipChanged() {
        SDLActivity.onNativeClipboardChanged();
    }
}

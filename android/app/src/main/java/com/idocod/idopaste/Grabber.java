package com.idocod.idopaste;

import android.content.ClipboardManager;
import android.content.Context;

/** Reads the current primary clip (only works while the app has focus) and stores it. */
public final class Grabber {

    private Grabber() {
    }

    public static String read(Context context) {
        try {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null && cm.hasPrimaryClip() && cm.getPrimaryClip() != null) {
                CharSequence text = cm.getPrimaryClip().getItemAt(0).coerceToText(context);
                if (text != null && text.length() > 0 && text.length() < 10000) {
                    return text.toString();
                }
            }
        } catch (SecurityException | IllegalStateException ignored) {
        }
        return null;
    }

    public static boolean grab(Context context) {
        String text = read(context);
        return text != null && ClipStore.add(context, text);
    }
}
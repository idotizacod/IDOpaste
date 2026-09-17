package com.idocod.idopaste;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipboardManager;
import android.view.accessibility.AccessibilityEvent;

import java.util.HashSet;
import java.util.Set;

/** Reads the clipboard on accessibility events to capture copied text (Android 10+ safe). */
public class IdoPasteAccessibilityService extends AccessibilityService {

    private static final long MIN_INTERVAL_MS = 800;
    private long lastCapture = 0;
    private final Set<String> recent = new HashSet<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastCapture < MIN_INTERVAL_MS) {
            return;
        }
        String captured = tryClipboard();
        if (captured != null && !captured.trim().isEmpty()) {
            lastCapture = now;
            if (!recent.contains(captured)) {
                if (recent.size() > 12) {
                    recent.clear();
                }
                recent.add(captured);
                ClipStore.add(this, captured);
            }
        }
    }

    private String tryClipboard() {
        try {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (cm != null && cm.hasPrimaryClip() && cm.getPrimaryClip() != null) {
                CharSequence text = cm.getPrimaryClip().getItemAt(0).coerceToText(this);
                if (text != null && text.length() > 0 && text.length() < 10000) {
                    return text.toString();
                }
            }
        } catch (SecurityException | IllegalStateException ignored) {
        }
        return null;
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        ClipboardCaptureService.start(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

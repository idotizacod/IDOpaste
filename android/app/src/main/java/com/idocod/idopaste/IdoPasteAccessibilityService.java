package com.idocod.idopaste;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipboardManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Detects copy actions and captures the selected/copied text from the window tree. */
public class IdoPasteAccessibilityService extends AccessibilityService {

    private static final long MIN_INTERVAL_MS = 800;
    private long lastCapture = 0;
    private final Set<String> recent = new HashSet<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getSource() == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastCapture < MIN_INTERVAL_MS) {
            return;
        }
        String captured = tryClipboard();
        if (captured == null) {
            captured = tryNodeText(event);
        }
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

    private String tryNodeText(AccessibilityEvent event) {
        AccessibilityNodeInfo node = event.getSource();
        if (node == null) {
            return null;
        }
        int type = event.getEventType();
        if (type == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
                || type == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED
                || type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            List<CharSequence> texts = event.getText();
            if (texts != null && !texts.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (CharSequence t : texts) {
                    if (t != null && t.length() > 0) {
                        sb.append(t);
                    }
                }
                if (sb.length() > 0) {
                    return sb.toString();
                }
            }
        }
        node.recycle();
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
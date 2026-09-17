package com.idocod.idopaste;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipboardManager;
import android.view.accessibility.AccessibilityEvent;

/**
 * Captura lo copiado: lee el portapapeles en cada evento de accesibilidad y
 * guarda el texto SOLO cuando cambió respecto al último visto. Como escribir
 * no modifica el portapapeles, nunca guarda lo que se escribe.
 */
public class IdoPasteAccessibilityService extends AccessibilityService {

    private static final long MIN_INTERVAL_MS = 300;
    private long lastEventAt = 0;
    private String lastClipboard = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastEventAt < MIN_INTERVAL_MS) {
            return;
        }
        String current = readClipboard();
        if (current == null) {
            return;
        }
        if (current.equals(lastClipboard)) {
            return;
        }
        lastEventAt = now;
        lastClipboard = current;
        if (!current.trim().isEmpty()) {
            ClipStore.add(this, current);
        }
    }

    private String readClipboard() {
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
        lastClipboard = readClipboard();
        ClipboardCaptureService.start(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
package com.idocod.idopaste;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "idopaste/host";

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    switch (call.method) {
                        case "load":
                            result.success(loadClipsJson());
                            break;
                        case "status":
                            result.success(status());
                            break;
                        case "copy":
                            String text = call.argument("text");
                            copyToClipboard(text);
                            result.success(true);
                            break;
                        case "delete":
                            long ts = ((Number) call.argument("ts")).longValue();
                            ClipStore.delete(this, ts);
                            result.success(true);
                            break;
                        case "clear":
                            ClipStore.clear(this);
                            result.success(true);
                            break;
                        case "openAccessibilitySettings":
                            openAccessibilitySettings();
                            result.success(true);
                            break;
                        case "grabNow":
                            Grabber.grab(this);
                            result.success(true);
                            break;
                        default:
                            result.notImplemented();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Grabber.grab(this);
    }

    private List<Map<String, Object>> loadClipsJson() {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> clip : ClipStore.loadValidMaps(this)) {
            out.add(clip);
        }
        return out;
    }

    private Map<String, Object> status() {
        Map<String, Object> o = new HashMap<>();
        o.put("serviceEnabled", isAccessibilityEnabled());
        return o;
    }

    private boolean isAccessibilityEnabled() {
        String enabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabled == null) {
            return false;
        }
        ComponentName c = new ComponentName(this, IdoPasteAccessibilityService.class);
        String flattened = c.flattenToString();
        for (String s : enabled.split(":")) {
            s = s.trim();
            if (s.isEmpty()) {
                continue;
            }
            if (s.equalsIgnoreCase(flattened) || s.endsWith(flattened)) {
                return true;
            }
        }
        return false;
    }

    private void openAccessibilitySettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    private void copyToClipboard(String text) {
        if (text == null) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("idopaste", text));
        if (!TextUtils.isEmpty(text)) {
            Toast.makeText(this, "IDOPASTE · COPIADO", Toast.LENGTH_SHORT).show();
        }
    }
}
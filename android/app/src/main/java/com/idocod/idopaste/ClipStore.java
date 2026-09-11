package com.idocod.idopaste;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Shared JSON storage for captured clips, with 48h auto-purge and dedupe. */
public final class ClipStore {

    private static final String PREFS = "idopaste_prefs";
    private static final String KEY_CLIPS = "clips_json";
    public static final long TTL_MILLIS = 48L * 60L * 60L * 1000L;
    private static final int MAX_ITEMS = 200;

    private ClipStore() {
    }

    public static List<JSONObject> load(Context context) {
        List<JSONObject> out = new ArrayList<>();
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            String raw = prefs.getString(KEY_CLIPS, "[]");
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                out.add(arr.getJSONObject(i));
            }
        } catch (Exception ignored) {
        }
        return out;
    }

    public static List<JSONObject> loadValid(Context context) {
        List<JSONObject> all = load(context);
        List<JSONObject> out = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (JSONObject o : all) {
            if (now - o.optLong("ts", 0) <= TTL_MILLIS) {
                out.add(o);
            }
        }
        return out;
    }

    public static List<Map<String, Object>> loadValidMaps(Context context) {
        List<JSONObject> clips = loadValid(context);
        List<Map<String, Object>> out = new ArrayList<>();
        for (JSONObject o : clips) {
            Map<String, Object> m = new HashMap<>();
            m.put("text", o.optString("text", ""));
            m.put("ts", o.optLong("ts", 0));
            m.put("type", o.optString("type", "text"));
            out.add(m);
        }
        return out;
    }

    public static void save(Context context, List<JSONObject> clips) {
        long now = System.currentTimeMillis();
        List<JSONObject> valid = new ArrayList<>();
        for (JSONObject o : clips) {
            if (now - o.optLong("ts", 0) <= TTL_MILLIS) {
                valid.add(o);
            }
        }
        while (valid.size() > MAX_ITEMS) {
            valid.remove(valid.size() - 1);
        }
        JSONArray arr = new JSONArray();
        for (JSONObject o : valid) {
            arr.put(o);
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_CLIPS, arr.toString())
                .apply();
    }

    /** Adds a clip (newest first). True if stored. */
    public static boolean add(Context context, String text, long ts) {
        if (text == null) {
            return false;
        }
        String clean = text.trim();
        if (clean.isEmpty()) {
            return false;
        }
        List<JSONObject> clips = load(context);
        if (!clips.isEmpty()) {
            String top = clips.get(0).optString("text", "");
            if (top.equals(clean)) {
                return false;
            }
        }
        JSONObject item = new JSONObject();
        try {
            item.put("text", clean);
            item.put("ts", ts);
            item.put("type", clean.contains("://") ? "link" : "text");
        } catch (Exception ignored) {
            return false;
        }
        clips.add(0, item);
        save(context, clips);
        notifyWidgetDataChanged(context);
        return true;
    }

    public static boolean add(Context context, String text) {
        return add(context, text, System.currentTimeMillis());
    }

    public static void delete(Context context, long ts) {
        List<JSONObject> clips = load(context);
        Iterator<JSONObject> it = clips.iterator();
        while (it.hasNext()) {
            if (it.next().optLong("ts", 0) == ts) {
                it.remove();
            }
        }
        save(context, clips);
        notifyWidgetDataChanged(context);
    }

    public static void clear(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(KEY_CLIPS).apply();
        notifyWidgetDataChanged(context);
    }

    public static void notifyWidgetDataChanged(Context context) {
        AppWidgetManager am = AppWidgetManager.getInstance(context);
        int[] ids = am.getAppWidgetIds(new ComponentName(context, IdoPasteWidgetProvider.class));
        if (ids != null && ids.length > 0) {
            am.notifyAppWidgetViewDataChanged(ids, R.id.widget_list);
        }
    }
}
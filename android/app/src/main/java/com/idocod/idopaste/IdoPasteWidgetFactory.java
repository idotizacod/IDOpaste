package com.idocod.idopaste;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IdoPasteWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context context;
    private final List<JSONObject> clips = new ArrayList<>();
    private static final int MAX_WIDGET_ITEMS = 20;

    public IdoPasteWidgetFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        clips.clear();
        List<JSONObject> all = ClipStore.loadValid(context);
        int n = Math.min(all.size(), MAX_WIDGET_ITEMS);
        for (int i = 0; i < n; i++) {
            clips.add(all.get(i));
        }
    }

    @Override
    public void onDestroy() {
        clips.clear();
    }

    @Override
    public int getCount() {
        return clips.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        JSONObject item = clips.get(position);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.idopaste_widget_item);
        rv.setTextViewText(R.id.widget_item_text, item.optString("text", ""));

        Intent fillIn = new Intent();
        fillIn.putExtra(IdoPasteWidgetProvider.EXTRA_TEXT, item.optString("text", ""));
        rv.setOnClickFillInIntent(R.id.widget_item_root, fillIn);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
package com.idocod.idopaste;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/** Shows the captured clipboard history. Tap an item to copy it to the clipboard. */
public class IdoPasteWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_COPY_ITEM = "com.idocod.idopaste.COPY_ITEM";
    public static final String EXTRA_TEXT = "extra_text";
    public static final String ACTION_OPEN_APP = "com.idocod.idopaste.OPEN_APP";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        updateWidget(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (ACTION_OPEN_APP.equals(action)) {
            Intent open = new Intent(context, MainActivity.class);
            open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(open);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.idopaste_widget);

        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, IdoPasteWidgetService.class)
                        .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId));

        Intent copyIntent = new Intent(context, IdoPasteWidgetCopyReceiver.class);
        copyIntent.setAction(ACTION_COPY_ITEM);
        copyIntent.setPackage(context.getPackageName());
        android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(
                context, 0, copyIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        views.setPendingIntentTemplate(R.id.widget_list, pi);

        Intent openIntent = new Intent(context, IdoPasteWidgetProvider.class);
        openIntent.setAction(ACTION_OPEN_APP);
        android.app.PendingIntent openPi = android.app.PendingIntent.getBroadcast(
                context, 0, openIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_header, openPi);

        views.setEmptyView(R.id.widget_list, R.id.widget_empty);

        appWidgetManager.notifyAppWidgetViewDataChanged(new int[]{appWidgetId}, R.id.widget_list);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void refreshAll(Context context) {
        AppWidgetManager am = AppWidgetManager.getInstance(context);
        int[] ids = am.getAppWidgetIds(new ComponentName(context, IdoPasteWidgetProvider.class));
        if (ids != null && ids.length > 0) {
            am.notifyAppWidgetViewDataChanged(ids, R.id.widget_list);
        }
    }
}
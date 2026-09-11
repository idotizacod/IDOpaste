package com.idocod.idopaste;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

/** Foreground listener service: fires when the primary clip changes. */
public class ClipboardCaptureService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {

    public static final String CHANNEL_ID = "idopaste_capture";
    public static final int NOTIFICATION_ID = 1;

    private ClipboardManager clipboard;

    @Override
    public void onCreate() {
        super.onCreate();
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        startForeground(NOTIFICATION_ID, buildNotification());
        if (clipboard != null) {
            clipboard.addPrimaryClipChangedListener(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, buildNotification());
        return START_STICKY;
    }

    @Override
    public void onPrimaryClipChanged() {
        if (clipboard == null || !clipboard.hasPrimaryClip()) {
            return;
        }
        try {
            ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            CharSequence text = clipBoard.getPrimaryClip().getItemAt(0).getText();
            if (text != null && text.length() > 0 && text.length() < 10000) {
                ClipStore.add(this, text.toString());
            }
        } catch (SecurityException | IllegalStateException ignored) {
            // Background clipboard reads are blocked on Android 10+ for non-IME apps.
        }
    }

    @Override
    public void onDestroy() {
        if (clipboard != null) {
            try {
                clipboard.removePrimaryClipChangedListener(this);
            } catch (Exception ignored) {
            }
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "IDOpaste", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Escucha el portapapeles");
            nm.createNotificationChannel(channel);
        }
        Intent open = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
        b.setSmallIcon(R.drawable.ic_stat_paste)
                .setContentTitle("IDOPASTE")
                .setContentText("Escuchando portapapeles · REC")
                .setContentIntent(pi)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE);
        return b.build();
    }

    /** Starts the foreground capture service. */
    public static void start(Context context) {
        Intent intent = new Intent(context, ClipboardCaptureService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
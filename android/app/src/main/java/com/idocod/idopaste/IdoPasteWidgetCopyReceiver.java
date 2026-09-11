package com.idocod.idopaste;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class IdoPasteWidgetCopyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String text = intent.getStringExtra(IdoPasteWidgetProvider.EXTRA_TEXT);
        if (text == null) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("idopaste", text));
        Toast.makeText(context, "IDOPASTE · COPIADO", Toast.LENGTH_SHORT).show();
    }
}
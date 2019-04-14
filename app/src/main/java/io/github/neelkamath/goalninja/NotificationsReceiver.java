package io.github.neelkamath.goalninja;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationsReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        intent = intent.getExtras();
        Parcelable parcelable = intent != null ? intent.getParcelable("io.github.neelkamath.EXTRA_NOTIFICATION") : null;
        if (parcelable != null) {
            NotificationManagerCompat.from(context).notify(intent.getInt("io.github.neelkamath.EXTRA_ID"), (Notification) parcelable);
        }
    }
}

package io.github.neelkamath.goalninja;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build.VERSION;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class App extends Application {
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Goal.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize((Context) this);
        ParseACL.setDefaultACL(new ParseACL(), true);
        if (VERSION.SDK_INT > 26) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(C0406R.string.deadlines_channel_id), getString(C0406R.string.deadlines_notification_name), 2);
            notificationChannel.setDescription(getString(C0406R.string.deadlines_channel_desc));
            NotificationChannel notificationChannel2 = new NotificationChannel(getString(C0406R.string.review_channel_id), getString(C0406R.string.review_notification_name), 2);
            notificationChannel2.setDescription(getString(C0406R.string.review_channel_desc));
            NotificationChannel notificationChannel3 = new NotificationChannel(getString(C0406R.string.progress_channel_id), getString(C0406R.string.progress_notification_name), 2);
            notificationChannel3.setDescription(getString(C0406R.string.progress_channel_desc));
            NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
                notificationManager.createNotificationChannel(notificationChannel2);
                notificationManager.createNotificationChannel(notificationChannel3);
            }
        }
        NotificationsSetter notificationsSetter = new NotificationsSetter(this);
        SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
        if (sharedPreferences.getInt("notifyHour", -1) == -1) {
            sharedPreferences.edit().putInt("notifyHour", 17).putInt("notifyMinute", 30).apply();
        }
    }
}

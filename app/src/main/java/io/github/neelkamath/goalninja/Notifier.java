package io.github.neelkamath.goalninja;

import android.app.Notification;

class Notifier {
    private int id;
    private Notification notification;
    private long time;

    Notifier(int i, Notification notification, long j) {
        this.id = i;
        this.notification = notification;
        this.time = j;
    }

    int getId() {
        return this.id;
    }

    Notification getNotification() {
        return this.notification;
    }

    long getTime() {
        return this.time;
    }
}

package io.github.neelkamath.goalninja;

import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class NotificationsSetter {
    static final String ID = "io.github.neelkamath.EXTRA_ID";
    static final String NOTIFICATION = "io.github.neelkamath.EXTRA_NOTIFICATION";
    private Context context;
    private int id = 0;
    private int intentId = 0;
    private List<Notifier> notifiers;

    /* renamed from: io.github.neelkamath.goalninja.NotificationsSetter$1 */
    class C08861 implements FindCallback<Goal> {
        C08861() {
        }

        public void done(List<Goal> list, ParseException parseException) {
            if (parseException != null) {
                Log.e("query", parseException.getMessage());
            } else {
                NotificationsSetter.this.setNotifications(list);
            }
        }
    }

    NotificationsSetter(Context context) {
        this.context = context;
        this.notifiers = new ArrayList();
        ParseQuery.getQuery(Goal.class).fromLocalDatastore().whereExists("goal").whereEqualTo("isCompleted", Boolean.valueOf(false)).findInBackground(new C08861());
    }

    private void setNotifications(List<Goal> list) {
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(NotificationCompat.CATEGORY_ALARM);
        if (alarmManager != null) {
            while (true) {
                AlarmClockInfo nextAlarmClock = alarmManager.getNextAlarmClock();
                if (nextAlarmClock == null) {
                    break;
                }
                alarmManager.cancel(nextAlarmClock.getShowIntent());
            }
            NotificationManagerCompat.from(this.context).cancelAll();
            for (Goal goal : list) {
                Calendar calendar = getCalendar(goal.getCreatedDate());
                Calendar instance = Calendar.getInstance();
                while (calendar.before(instance)) {
                    calendar.add(2, 1);
                }
                instance = Calendar.getInstance();
                instance.setTime(goal.getDeadline());
                while (calendar.before(instance)) {
                    setReviewNotification(goal, calendar.getTimeInMillis());
                    calendar.add(2, 1);
                }
                setDeadlineNotification(goal);
                for (MiniDeadline miniDeadline : goal.getMiniDeadlines()) {
                    if (!miniDeadline.isCompleted()) {
                        setMiniDeadlineNotification(goal.getGoal(), miniDeadline);
                    }
                }
                List list2 = null;
                try {
                    list2 = goal.getProgress();
                } catch (CreatedDateNotSetException e) {
                    Log.e("retrieve", e.getMessage());
                }
                if (r3 != null) {
                    LocalDate minusDays = new LocalDate().minusDays(1);
                    for (ProgressEntry progressEntry : r3) {
                        boolean z = !progressEntry.isCompleted() && progressEntry.getReason().isEmpty();
                        if (z) {
                            setProgressEntryNotification(goal.getGoal(), progressEntry.getDate().getTime(), true);
                        }
                        if (minusDays.isEqual(new LocalDate(progressEntry.getDate())) && z) {
                            setProgressEntryNotification(goal.getGoal(), getCalendar(new Date()).getTimeInMillis(), false);
                        }
                    }
                }
            }
            list = this.notifiers.iterator();
            while (list.hasNext()) {
                Notifier notifier = (Notifier) list.next();
                alarmManager.set(0, notifier.getTime(), PendingIntent.getBroadcast(this.context, notifier.getId(), new Intent(this.context, NotificationsReceiver.class).putExtra(NOTIFICATION, notifier.getNotification()).putExtra(ID, notifier.getId()), 0));
            }
        }
    }

    private void setReviewNotification(Goal goal, long j) {
        String string = this.context.getString(C0406R.string.review);
        goal = getNotification(String.format(Locale.US, "%s %s", string, goal.getGoal()), C0406R.string.why_review, true, new Intent(this.context, CreatorActivity.class).putExtra("io.github.neelkamath.GOAL", goal.getGoal()));
        List list = this.notifiers;
        int i = this.id + 1;
        this.id = i;
        list.add(new Notifier(i, goal, j));
    }

    private void setDeadlineNotification(Goal goal) {
        Notification notification = getNotification(String.format(Locale.US, "%s%s", goal.getGoal(), getDeadlineText(goal.getDeadline().getTime())), C0406R.string.goal_deadline_reached, true, new Intent(this.context, MainActivity.class));
        List list = this.notifiers;
        int i = this.id + 1;
        this.id = i;
        list.add(new Notifier(i, notification, getCalendar(goal.getDeadline()).getTimeInMillis()));
    }

    private void setMiniDeadlineNotification(String str, MiniDeadline miniDeadline) {
        String deadlineText = getDeadlineText(miniDeadline.getDate().getTime());
        str = getNotification(String.format(Locale.US, "%s%s", miniDeadline.getText(), deadlineText), C0406R.string.mini_deadline_deadline_reached, true, new Intent(this.context, CreatorActivity.class).putExtra("io.github.neelkamath.GOAL", str));
        List list = this.notifiers;
        int i = this.id + 1;
        this.id = i;
        list.add(new Notifier(i, str, getCalendar(miniDeadline.getDate()).getTimeInMillis()));
    }

    private void setProgressEntryNotification(String str, long j, boolean z) {
        String string = this.context.getString(z ? C0406R.string.update_today_progress : C0406R.string.update_yesterday_progress);
        str = getNotification(String.format(Locale.US, "%s %s", string, str), C0406R.string.no_progress_entry, z ^ true, new Intent(this.context, CreatorActivity.class).putExtra("io.github.neelkamath.GOAL", str));
        z = this.notifiers;
        int i = this.id + 1;
        this.id = i;
        z.add(new Notifier(i, str, j));
    }

    private Notification getNotification(String str, int i, boolean z, Intent intent) {
        intent.setFlags(268468224);
        str = new Builder(this.context, this.context.getString(C0406R.string.progress_channel_id)).setSmallIcon(C0406R.mipmap.ic_launcher_foreground).setContentTitle(str).setPriority(-1);
        Context context = this.context;
        int i2 = this.intentId + 1;
        this.intentId = i2;
        str = str.setContentIntent(PendingIntent.getActivity(context, i2, intent, 134217728)).setCategory(NotificationCompat.CATEGORY_REMINDER).setAutoCancel(true);
        i = this.context.getString(i);
        if (z) {
            str.setStyle(new BigTextStyle().bigText(i));
        } else {
            str.setContentText(i);
        }
        return str.build();
    }

    private Calendar getCalendar(Date date) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("settings", 0);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(11, sharedPreferences.getInt("notifyHour", instance.get(11)));
        instance.set(12, sharedPreferences.getInt("notifyMinute", instance.get(12)));
        instance.set(13, 0);
        instance.set(14, 0);
        return instance;
    }

    private String getDeadlineText(long j) {
        return this.context.getString(new LocalDate().isEqual(new LocalDate(j)) ? C0406R.string.today_deadline : C0406R.string.before_deadline);
    }
}

package io.github.nfdz.tomatito.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import io.github.nfdz.tomatito.R;
import io.github.nfdz.tomatito.data.PreferencesUtils;
import io.github.nfdz.tomatito.services.PomodoroService;
import io.github.nfdz.tomatito.ui.MainActivity;

public class NotificationUtils {

    private static final int NOTIFICATION_ID = 875;

    private static final long[] VIBRATION_PATTERN = { 0, 200, 500, 200, 500, 200, 500, 200, 500 };

    private static Builder buildPomodoroNotification(@NonNull Context context,
                                                     @StringRes int title,
                                                     @StringRes int text) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent operation =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String textString = context.getString(text);
        Builder noteBld = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(title))
                .setContentText(textString)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textString))
                .setSmallIcon(R.drawable.ic_tomato)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_HIGH);

        // vibrate
        boolean vibrationEnabled = PreferencesUtils.getVibrationEnabled(context);
        if (vibrationEnabled) {
            noteBld.setVibrate(VIBRATION_PATTERN);
        } else {
            // this is a quick hack to force heads-up notification with no vibration
            noteBld.setVibrate(new long[0]);
        }

        return noteBld;
    }

    private static void addStopAction(@NonNull Context context, Builder noteBld) {
        Intent intent = new Intent(context, PomodoroService.class);
        intent.setAction(PomodoroService.STOP_POMODORO);
        PendingIntent stopOperation =
                PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        noteBld.addAction(R.drawable.ic_stop,
                context.getString(R.string.notification_action_stop),
                stopOperation);
    }

    public static void notifyWork(@NonNull Context context) {
        Builder noteBld = buildPomodoroNotification(context,
                R.string.notification_title_work,
                R.string.notification_text_work);
        addStopAction(context, noteBld);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, noteBld.build());
    }

    public static void notifyShortBreak(@NonNull Context context) {
        Builder noteBld = buildPomodoroNotification(context,
                R.string.notification_title_short_break,
                R.string.notification_text_short_break);
        addStopAction(context, noteBld);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, noteBld.build());

    }

    public static void notifyLongBreak(@NonNull Context context) {
        Builder noteBld = buildPomodoroNotification(context,
                R.string.notification_title_long_break,
                R.string.notification_text_long_break);
        addStopAction(context, noteBld);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, noteBld.build());

    }

    public static void notifyEnd(@NonNull Context context) {
        Builder noteBld = buildPomodoroNotification(context,
                R.string.notification_title_end,
                R.string.notification_text_end);

        // restart action
        Intent intent = new Intent(context, PomodoroService.class);
        intent.setAction(PomodoroService.RESTART_POMODORO);
        PendingIntent restartOperation =
                PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        noteBld.addAction(R.drawable.ic_replay,
                context.getString(R.string.notification_action_restart),
                restartOperation);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, noteBld.build());
    }

    public static void cancel(@NonNull Context context) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }
}

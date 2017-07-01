package io.github.nfdz.tomatito.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.NotificationCompat;

import io.github.nfdz.tomatito.R;
import io.github.nfdz.tomatito.ui.MainActivity;

public class NotificationUtils {

    private static final int NOTIFICATION_ID = 875;

    private static void notifyPomodoro(@NonNull Context context,
                                       @StringRes int title,
                                       @StringRes int text) {

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent operation =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification note = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(title))
                .setContentText(context.getString(text))
                //.setSmallIcon(R.drawable.ic)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .build();

        manager.notify(NOTIFICATION_ID, note);
    }

    public static void notifyWork(@NonNull Context context) {
        notifyPomodoro(context, R.string.notification_title_work, R.string.notification_text_work);
    }

    public static void notifyShortBreak(@NonNull Context context) {
        notifyPomodoro(context,
                R.string.notification_title_short_break,
                R.string.notification_text_short_break);

    }

    public static void notifyLongBreak(@NonNull Context context) {
        notifyPomodoro(context,
                R.string.notification_title_long_break,
                R.string.notification_text_long_break);

    }

    public static void notifyEnd(@NonNull Context context) {
        notifyPomodoro(context, R.string.notification_title_end, R.string.notification_text_end);

    }
}

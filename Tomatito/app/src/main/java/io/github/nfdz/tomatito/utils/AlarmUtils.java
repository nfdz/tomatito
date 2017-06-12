/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import io.github.nfdz.tomatito.data.PreferencesUtils;
import io.github.nfdz.tomatito.ui.MainActivity;

public class AlarmUtils {

    private static boolean sInitialized = false;

    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized) return;
        sInitialized = true;

        long currentPomodoro = PreferencesUtils.getCurrentPomodoro(context);

        if (checkValidPomodoro(currentPomodoro)) {
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // TODO
            long currentTime = System.currentTimeMillis();
            long oneMinute = 60 * 1000;
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    currentTime + oneMinute,
                    oneMinute,
                    pendingIntent);
        } else {
            PreferencesUtils.deleteCurrentPomodoro(context);
        }
    }

    private static boolean checkValidPomodoro(long currentPomodoro) {
        // TODO
        return false;
    }
}
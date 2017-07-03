/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Date;

import io.github.nfdz.tomatito.data.Pomodoro;
import io.github.nfdz.tomatito.data.PreferencesUtils;
import io.github.nfdz.tomatito.services.AlarmService;
import timber.log.Timber;

public class AlarmUtils {

    private static boolean sInitialized = false;

    synchronized public static void initialize(@NonNull Context context) {
        if (sInitialized) return;
        sInitialized = true;

        Pomodoro pomodoro = PreferencesUtils.getPomodoro(context);

        if (PomodoroUtils.isValid(pomodoro)) {
            scheduleAlarm(context, pomodoro);
        }
    }

    public static void scheduleAlarm(@NonNull Context context) {
        Pomodoro pomodoro = PreferencesUtils.getPomodoro(context);
        scheduleAlarm(context, pomodoro);
    }

    public static void scheduleAlarm(@NonNull Context context, @NonNull Pomodoro pomodoro) {

        boolean alarmEnabled = PreferencesUtils.getAlarmEnabled(context);
        if (!alarmEnabled) {
            disableAlarm(context);
            return;
        }

        // intent to trigger
        Intent intent = new Intent(context, AlarmService.class);
        PomodoroUtils.PomodoroState state = PomodoroUtils.getPomodoroState(pomodoro);
        switch (state.flag) {
            case PomodoroUtils.WORKING_STATE:
                if (state.pomodoroCounter >= pomodoro.pomodorosToLongBreak) {
                    intent.setAction(AlarmService.LONG_BREAK_ALARM);
                } else {
                    intent.setAction(AlarmService.SHORT_BREAK_ALARM);
                }
                break;
            case PomodoroUtils.SHORT_BREAK_STATE:
                intent.setAction(AlarmService.WORK_ALARM);
                break;
            case PomodoroUtils.LONG_BREAK_STATE:
                intent.setAction(AlarmService.END_ALARM);
                break;
            default:
                Timber.w("Invalid pomodoro state, cannot schedule alarm for it.");
                return;
        }

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent operation = PendingIntent
                .getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Timber.d("Scheduling pomodoro alarm at " + new Date(state.stateUtcEndTime));
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            manager.setExact(AlarmManager.RTC_WAKEUP, state.stateUtcEndTime, operation);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, state.stateUtcEndTime, operation);
        }
    }

    public static void disableAlarm(@NonNull Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intentEnd = new Intent(context, AlarmService.class);
        intentEnd.setAction(AlarmService.END_ALARM);
        PendingIntent operationEnd = PendingIntent.getService(context, 0, intentEnd, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentWork = new Intent(context, AlarmService.class);
        intentWork.setAction(AlarmService.WORK_ALARM);
        PendingIntent operationWork = PendingIntent.getService(context, 0, intentWork, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentShortBreak = new Intent(context, AlarmService.class);
        intentShortBreak.setAction(AlarmService.SHORT_BREAK_ALARM);
        PendingIntent operationShortBreak = PendingIntent.getService(context, 0, intentShortBreak, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentLongBreak = new Intent(context, AlarmService.class);
        intentLongBreak.setAction(AlarmService.LONG_BREAK_ALARM);
        PendingIntent operationLongBreak = PendingIntent.getService(context, 0, intentLongBreak, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.cancel(operationEnd);
        manager.cancel(operationWork);
        manager.cancel(operationShortBreak);
        manager.cancel(operationLongBreak);
    }
}
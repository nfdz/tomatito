/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

import io.github.nfdz.tomatito.R;
import io.github.nfdz.tomatito.utils.AlarmUtils;

/**
 * This class has static methods to ease work with shared preferences
 */
public class PreferencesUtils {

    public static final String POMODORO_START_TIME_KEY = "current-pomodoro";
    public static final long POMODORO_START_TIME_DEFAULT = -1;

    public static long getPomodoroStartTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(POMODORO_START_TIME_KEY, POMODORO_START_TIME_DEFAULT);
    }

    public static void setPomodoroStartTime(Context context, long currentPomodoro) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(POMODORO_START_TIME_KEY, currentPomodoro);
        editor.apply();

        AlarmUtils.scheduleAlarm(context);
    }

    public static void deletePomodoro(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(POMODORO_START_TIME_KEY, POMODORO_START_TIME_DEFAULT);
        editor.apply();

        AlarmUtils.disableAlarm(context);
    }

    public static long getPomodoroTime(Context context) {
        // TODO
        //return TimeUnit.MINUTES.toMillis(25);
        return TimeUnit.MINUTES.toMillis(1);
    }

    public static long getShortBreakTime(Context context) {
        // TODO
        //return TimeUnit.MINUTES.toMillis(5);
        return TimeUnit.MINUTES.toMillis(1);
    }


    public static long getLongBreakTime(Context context) {
        // TODO
        //return TimeUnit.MINUTES.toMillis(25);
        return TimeUnit.MINUTES.toMillis(2);
    }

    public static int getPomodorosToLongBreak(Context context) {
        // TODO
        return 4;
    }

    public static Pomodoro getPomodoro(Context context) {
        long pomodoroStartTime = getPomodoroStartTime(context);
        int pomodorosToLongBreak = PreferencesUtils.getPomodorosToLongBreak(context);
        long pomodoroTime = PreferencesUtils.getPomodoroTime(context);
        long shortBreakTime = PreferencesUtils.getShortBreakTime(context);
        long longBreakTime = PreferencesUtils.getLongBreakTime(context);
        return new Pomodoro(pomodoroStartTime,
                            pomodoroTime,
                            shortBreakTime,
                            longBreakTime,
                            pomodorosToLongBreak);
    }

    public static boolean getAlarmEnabled(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_alarm_key);
        boolean defaultValue = Boolean.parseBoolean(context.getString(R.string.pref_alarm_default));
        return sp.getBoolean(key, defaultValue);
    }

    public static boolean getVibrationEnabled(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_vibration_key);
        boolean defaultValue = Boolean.parseBoolean(context.getString(R.string.pref_vibration_default));
        return sp.getBoolean(key, defaultValue);
    }
}

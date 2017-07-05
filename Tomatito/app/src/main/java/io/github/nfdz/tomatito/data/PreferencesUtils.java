/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

import io.github.nfdz.tomatito.R;

/**
 * This class has static methods to ease work with shared preferences.
 */
public class PreferencesUtils {

    /**
     * This preference is not a user preference, it is used to store the start time
     * of the ongoing pomodoro.
     */
    public static final String POMODORO_START_TIME_KEY = "current-pomodoro";
    /**
     * Default value of ongoing pomodoro start time. It means that there is no ongoing pomodoro.
     */
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
    }

    /**
     * Removes stored ongoing pomodoro start time and sets its value to the default one.
     * @param context
     */
    public static void deletePomodoro(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(POMODORO_START_TIME_KEY, POMODORO_START_TIME_DEFAULT);
        editor.apply();
    }

    public static long getPomodoroTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_pomodoro_time_key);
        int defaultValue = Integer.parseInt(context.getString(R.string.pref_pomodoro_time_default));
        int pomodoroTimeMinutes = sp.getInt(key, defaultValue);
        return TimeUnit.MINUTES.toMillis(pomodoroTimeMinutes);
    }

    public static long getShortBreakTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_short_break_time_key);
        int defaultValue = Integer.parseInt(context.getString(R.string.pref_short_break_time_default));
        int shortBreakTimeMinutes = sp.getInt(key, defaultValue);
        return TimeUnit.MINUTES.toMillis(shortBreakTimeMinutes);
    }


    public static long getLongBreakTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_long_break_time_key);
        int defaultValue = Integer.parseInt(context.getString(R.string.pref_long_break_time_default));
        int longBreakTimeMinutes = sp.getInt(key, defaultValue);
        return TimeUnit.MINUTES.toMillis(longBreakTimeMinutes);
    }

    public static int getPomodorosToLongBreak(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_pomodoros_to_long_break_key);
        int defaultValue = Integer.parseInt(context.getString(R.string.pref_pomodoros_to_long_break_default));
        return sp.getInt(key, defaultValue);
    }

    /**
     * Gets stored ongoing pomodoro. If there is no ongoing pomodoro, this method will not
     * fail but it will return a pomodoro with a wrong start time value.
     * @param context
     * @return
     */
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

    /**
     * This method restores default preferences of a pomodoro. It is
     * working time, short break time, long break time and pomodoros to long break.
     * @param context
     */
    public static void restoreDefaultPomodoroPrefs(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        int pomodoroTimeDefault = Integer.parseInt(context.getString(R.string.pref_pomodoro_time_default));
        String pomodoroTimeKey = context.getString(R.string.pref_pomodoro_time_key);
        editor.putInt(pomodoroTimeKey, pomodoroTimeDefault);

        int shortBreakTimeDefault = Integer.parseInt(context.getString(R.string.pref_short_break_time_default));
        String shortBreakTimeKey = context.getString(R.string.pref_short_break_time_key);
        editor.putInt(shortBreakTimeKey, shortBreakTimeDefault);

        int longBreakTimeDefault = Integer.parseInt(context.getString(R.string.pref_long_break_time_default));
        String longBreakTimeKey = context.getString(R.string.pref_long_break_time_key);
        editor.putInt(longBreakTimeKey, longBreakTimeDefault);

        int pomodorosToLongBreakDefault = Integer.parseInt(context.getString(R.string.pref_pomodoros_to_long_break_default));
        String pomodorosToLongBreakKey = context.getString(R.string.pref_pomodoros_to_long_break_key);
        editor.putInt(pomodorosToLongBreakKey, pomodorosToLongBreakDefault);

        editor.apply();
    }

    public static int getStorageLimit(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_storage_limit_key);
        int defaultValue = Integer.parseInt(context.getString(R.string.pref_storage_limit_default));
        return sp.getInt(key, defaultValue);
    }
}

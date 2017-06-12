/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

/**
 * This class has static methods to ease work with shared preferences
 */
public class PreferencesUtils {

    public static final String CURRENT_POMODORO_KEY = "current-pomodoro";
    public static final long CURRENT_POMODORO_DEFAULT = -1;

    public static long getCurrentPomodoro(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(CURRENT_POMODORO_KEY, CURRENT_POMODORO_DEFAULT);
    }

    public static void setCurrentPomodoro(Context context, long currentPomodoro) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(CURRENT_POMODORO_KEY, currentPomodoro);
        editor.apply();
    }

    public static void deleteCurrentPomodoro(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(CURRENT_POMODORO_KEY, CURRENT_POMODORO_DEFAULT);
        editor.apply();
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

}

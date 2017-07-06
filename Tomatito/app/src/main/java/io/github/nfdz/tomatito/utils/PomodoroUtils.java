/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.util.concurrent.TimeUnit;

import io.github.nfdz.tomatito.data.DatabaseManager;
import io.github.nfdz.tomatito.data.FinishedPomodoro;
import io.github.nfdz.tomatito.data.Pomodoro;
import io.github.nfdz.tomatito.data.PreferencesUtils;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/** This class has methods to ease work with pomodoro operations. */
public class PomodoroUtils {

    private PomodoroUtils() { }

    @Retention(SOURCE)
    @IntDef({INVALID_STATE, WORKING_STATE, SHORT_BREAK_STATE, LONG_BREAK_STATE})
    public @interface PomodoroStateFlag {}

    public static final int INVALID_STATE = 0;
    public static final int WORKING_STATE = 1;
    public static final int SHORT_BREAK_STATE = 2;
    public static final int LONG_BREAK_STATE = 3;

    public static class PomodoroState {

        public PomodoroState(@PomodoroStateFlag int flag) {
            this.flag = flag;
        }

        @PomodoroStateFlag
        public final int flag;

        public long stateProgressTime;
        public long stateUtcEndTime;
        public int pomodoroCounter;
        public int breakCounter;
    }

    /**
     * This method computes pomodoro state for the given ongoing pomodoro.
     * @param pomodoro
     * @return
     */
    public static PomodoroState getPomodoroState(Pomodoro pomodoro) {
        long currentTime = System.currentTimeMillis();

        if (pomodoro.pomodoroStartTime > currentTime) {
            return new PomodoroState(INVALID_STATE);
        }

        long elapsedTime = currentTime - pomodoro.pomodoroStartTime;
        int pomodoroCounter;

        for (pomodoroCounter = 1; pomodoroCounter <= pomodoro.pomodorosToLongBreak; pomodoroCounter++) {
            int breakCounter = pomodoroCounter - 1;
            long pomodoroEnd = pomodoro.pomodoroTime * pomodoroCounter + pomodoro.shortBreakTime * breakCounter;
            if (elapsedTime < pomodoroEnd) {
                long pomodoroStart = pomodoroEnd - pomodoro.pomodoroTime;
                long progressTime = elapsedTime - pomodoroStart;

                PomodoroState state = new PomodoroState(WORKING_STATE);
                state.pomodoroCounter = pomodoroCounter;
                state.breakCounter = breakCounter;
                state.stateProgressTime = progressTime;
                state.stateUtcEndTime = pomodoro.pomodoroStartTime + pomodoroEnd;
                return state;
            } else if (elapsedTime < pomodoroEnd + pomodoro.shortBreakTime) {
                // it is on a break
                int currentBreak = breakCounter + 1;
                if (currentBreak < pomodoro.pomodorosToLongBreak ) {
                    // short break
                    long breakStart = pomodoroEnd;
                    long progressTime = elapsedTime - breakStart;

                    PomodoroState state = new PomodoroState(SHORT_BREAK_STATE);
                    state.pomodoroCounter = pomodoroCounter;
                    state.breakCounter = breakCounter;
                    state.stateProgressTime = progressTime;
                    state.stateUtcEndTime = pomodoro.pomodoroStartTime + pomodoroEnd + pomodoro.shortBreakTime;
                    return state;
                } else {
                    // long break, do nothing because long break is handle outside for
                }
            }
        }
        long  longBreakEnd = pomodoro.pomodoroTime * pomodoro.pomodorosToLongBreak +
                pomodoro.shortBreakTime * (pomodoro.pomodorosToLongBreak - 1) + pomodoro.longBreakTime;
        if (elapsedTime <= longBreakEnd) {
            long longBreakStart = longBreakEnd - pomodoro.longBreakTime;
            long progressTime = elapsedTime - longBreakStart;

            PomodoroState state = new PomodoroState(LONG_BREAK_STATE);
            state.pomodoroCounter = pomodoro.pomodorosToLongBreak;
            state.breakCounter = pomodoro.pomodorosToLongBreak - 1;
            state.stateProgressTime = progressTime;
            state.stateUtcEndTime = pomodoro.pomodoroStartTime + longBreakEnd;
            return state;
        } else {
            return new PomodoroState(INVALID_STATE);
        }
    }

    /**
     * Return the time text with the format "mm:ss" for the given UTC time.
     * @param time
     * @return
     */
    public static String getTimerTextFor(long time) {
        String minutes = Long.toString(TimeUnit.MILLISECONDS.toMinutes(time));
        if (minutes.length() == 1) minutes = "0" + minutes;
        String seconds = Long.toString(TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        if (seconds.length() == 1) seconds = "0" + seconds;
        return minutes + ":" + seconds;
    }

    /**
     * This method checks if the given ongoing pomodoro is valid. It uses system current time
     * millis to get current time and check if pomodoro UTC start time is still valid.
     * @param pomodoro
     * @return true if it is valid, false if not.
     */
    public static boolean isValid(Pomodoro pomodoro) {
        boolean isDefault = pomodoro.pomodoroStartTime == PreferencesUtils.POMODORO_START_TIME_DEFAULT;
        if (!isDefault) {
            long endTime = getExpectedEndTime(pomodoro);
            long now = System.currentTimeMillis() + 5000; // 5s safe margin
            return now < endTime;
        }
        return false;
    }

    /** This method computes expected end time for the given ongoing pomodoro. */
    public static long getExpectedEndTime(Pomodoro pomodoro) {
        long endTime = pomodoro.pomodoroStartTime +
                pomodoro.pomodoroTime * pomodoro.pomodorosToLongBreak +
                pomodoro.shortBreakTime * (pomodoro.pomodorosToLongBreak - 1) +
                pomodoro.longBreakTime;
        return endTime;
    }

    /**
     * This method manages the initialization of a new pomodoro now. It performs serveral operations
     * like store, schedule alarm and notification.
     *
     * @param context
     */
    public static void startPomodoro(Context context) {
        AlarmUtils.disableAlarm(context);
        long now = System.currentTimeMillis();
        PreferencesUtils.setPomodoroStartTime(context, now);
        AlarmUtils.scheduleAlarm(context);
        NotificationUtils.notifyWork(context);
    }

    /**
     * This method manages the stop of current ongoing pomodoro.
     * @param context
     */
    public static void stopPomodoro(Context context) {
        Pomodoro currentPomodoro = PreferencesUtils.getPomodoro(context);
        if (currentPomodoro.pomodoroStartTime != PreferencesUtils.POMODORO_START_TIME_DEFAULT) {
            storePomodoroAsync(context, currentPomodoro);
        }
        PreferencesUtils.deletePomodoro(context);
        AlarmUtils.disableAlarm(context);
    }

    public static void storePomodoroAsync(final Context context, final Pomodoro pomodoro) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                long now = System.currentTimeMillis() + 5000; // 5s safe margin
                long expectedEndTime = getExpectedEndTime(pomodoro);
                long endTime = now > expectedEndTime ? expectedEndTime : now;
                FinishedPomodoro finishedPomodoro =
                        FinishedPomodoro.buildFinishedPomodoro(pomodoro, endTime);
                DatabaseManager.getInstance(context).insertPomodoro(finishedPomodoro);
                return null;
            }
        }.execute();
    }
}

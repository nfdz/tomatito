/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.util.concurrent.TimeUnit;

import io.github.nfdz.tomatito.data.Pomodoro;
import io.github.nfdz.tomatito.data.PreferencesUtils;

import static java.lang.annotation.RetentionPolicy.SOURCE;

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

    public static boolean isValid(Pomodoro pomodoro) {
        boolean isDefault = pomodoro.pomodoroStartTime != PreferencesUtils.POMODORO_START_TIME_DEFAULT;
        if (isDefault) {
            long endTime = pomodoro.pomodoroStartTime +
                    pomodoro.pomodoroTime * pomodoro.pomodorosToLongBreak +
                    pomodoro.shortBreakTime * (pomodoro.pomodorosToLongBreak - 1) +
                    pomodoro.longBreakTime;
            long now = System.currentTimeMillis() + 5000; // 5s safe margin
            return now < endTime;
        }
        return false;
    }
}

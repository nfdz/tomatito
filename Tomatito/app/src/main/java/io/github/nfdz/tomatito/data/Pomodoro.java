/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.data;

public class Pomodoro {

    public final long pomodoroStartTime;
    public final long pomodoroTime;
    public final long shortBreakTime;
    public final long longBreakTime;
    public final int pomodorosToLongBreak;

    public Pomodoro(long pomodoroStartTime,
                    long pomodoroTime,
                    long shortBreakTime,
                    long longBreakTime,
                    int pomodorosToLongBreak) {
        this.pomodoroStartTime = pomodoroStartTime;
        this.pomodoroTime = pomodoroTime;
        this.shortBreakTime = shortBreakTime;
        this.longBreakTime = longBreakTime;
        this.pomodorosToLongBreak = pomodorosToLongBreak;
    }
}

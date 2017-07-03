package io.github.nfdz.tomatito.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FinishedPomodoro {

    public final String name;
    public final long pomodoroTotalTime;
    public final long pomodoroEndTime;
    public final long pomodoroTime;
    public final long shortBreakTime;
    public final long longBreakTime;
    public final int pomodorosToLongBreak;

    public FinishedPomodoro(String name,
                            long pomodoroTotalTime,
                            long pomodoroEndTime,
                            long pomodoroTime,
                            long shortBreakTime,
                            long longBreakTime,
                            int pomodorosToLongBreak) {
        this.name = name;
        this.pomodoroTotalTime = pomodoroTotalTime;
        this.pomodoroEndTime = pomodoroEndTime;
        this.pomodoroTime = pomodoroTime;
        this.shortBreakTime = shortBreakTime;
        this.longBreakTime = longBreakTime;
        this.pomodorosToLongBreak = pomodorosToLongBreak;
    }

    public FinishedPomodoro(Cursor cursor) {
        // assume that it has default projection
        this.name = cursor.getString(Contract.PomodoroEntry.POSITION_NAME);
        this.pomodoroTotalTime = cursor.getLong(Contract.PomodoroEntry.POSITION_TOTAL_TIME);
        this.pomodoroEndTime = cursor.getLong(Contract.PomodoroEntry.POSITION_END_TIME);
        this.pomodoroTime = cursor.getLong(Contract.PomodoroEntry.POSITION_WORK_TIME);
        this.shortBreakTime = cursor.getLong(Contract.PomodoroEntry.POSITION_SHORT_BREAK_TIME);
        this.longBreakTime = cursor.getLong(Contract.PomodoroEntry.POSITION_LONG_BREAK_TIME);
        this.pomodorosToLongBreak = cursor.getInt(Contract.PomodoroEntry.POSITION_POMODOROS_TO_LONG_BREAK);
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Contract.PomodoroEntry.COLUMN_NAME, name);
        values.put(Contract.PomodoroEntry.COLUMN_TOTAL_TIME, pomodoroTotalTime);
        values.put(Contract.PomodoroEntry.COLUMN_END_TIME, pomodoroEndTime);
        values.put(Contract.PomodoroEntry.COLUMN_WORK_TIME, pomodoroTime);
        values.put(Contract.PomodoroEntry.COLUMN_SHORT_BREAK_TIME, shortBreakTime);
        values.put(Contract.PomodoroEntry.COLUMN_LONG_BREAK_TIME, longBreakTime);
        values.put(Contract.PomodoroEntry.COLUMN_POMODOROS_TO_LONG_BREAK, pomodorosToLongBreak);
        return values;
    }

    public static FinishedPomodoro buildFinishedPomodoro(Pomodoro pomodoro, long endTime) {
        if (pomodoro.pomodoroStartTime >= endTime) {
            throw new IllegalArgumentException("Pomodoro start time is in the future");
        }
        long elapsedTime = endTime - pomodoro.pomodoroStartTime;
        String name = getDefaultName(pomodoro.pomodoroStartTime);
        return new FinishedPomodoro(name,
                elapsedTime,
                endTime,
                pomodoro.pomodoroTime,
                pomodoro.shortBreakTime,
                pomodoro.longBreakTime,
                pomodoro.pomodorosToLongBreak);
    }

    public static String getDefaultName(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return "Pomodoro - " + format.format(new Date(time));
    }

}

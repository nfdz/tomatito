package io.github.nfdz.tomatito.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Finished pomodoro immutable data pojo class.
 */
public class FinishedPomodoro {

    public final String name;
    public final long pomodoroTotalTime;
    public final long pomodoroEndTime;
    public final long pomodoroTime;
    public final long shortBreakTime;
    public final long longBreakTime;
    public final int pomodorosToLongBreak;

    /**
     * Default constructor.
     * @param name
     * @param pomodoroTotalTime
     * @param pomodoroEndTime
     * @param pomodoroTime
     * @param shortBreakTime
     * @param longBreakTime
     * @param pomodorosToLongBreak
     */
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

    /**
     * Database cursor constructor. It assumes that cursor is pointing valid data and that
     * has the default projection.
     * @param cursor
     */
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

    /**
     * This method processes all attributes in to a content values object.
     * @return
     */
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

    /**
     * This static method builds a finished pomodoro with the given ongoing pomodoro and end time.
     * @param pomodoro
     * @param endTime UTC
     * @return
     */
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

    /**
     * Gets the default name with the given time. It is like "Pomodoro 10:05".
     * @param time
     * @return
     */
    public static String getDefaultName(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return "Pomodoro " + format.format(new Date(time));
    }

    /**
     * Gets the end time date formatted with the default format. It is like "1999/12/31".
     * @return
     */
    public String getEndTimeDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return format.format(new Date(pomodoroEndTime));
    }

    /**
     * Gets the overall percentage progress of the finished pomodoro. It is like "55%".
     * @return
     */
    public String getOverallProgress() {
        long totalTime = pomodoroTime * pomodorosToLongBreak +
                shortBreakTime * (pomodorosToLongBreak - 1) +
                longBreakTime;
        int progress = (int) (((pomodoroTotalTime + 0.0)/totalTime) * 100);
        return Integer.toString(progress) + "%";
    }

}

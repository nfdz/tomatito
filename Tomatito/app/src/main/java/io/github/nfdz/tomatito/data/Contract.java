package io.github.nfdz.tomatito.data;

import android.provider.BaseColumns;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Contract {

    public static final class PomodoroEntry implements BaseColumns {

        public static final String TABLE_NAME = "pomodoros";

        /** Stored as string. */
        public static final String COLUMN_NAME = "name";

        /** Stored as integer. */
        public static final String COLUMN_TOTAL_TIME = "total_time";

        /** Stored as integer. */
        public static final String COLUMN_END_TIME = "end_time";

        /** Stored as integer. */
        public static final String COLUMN_WORK_TIME = "work_time";

        /** Stored as integer. */
        public static final String COLUMN_SHORT_BREAK_TIME = "short_break_time";

        /** Stored as integer. */
        public static final String COLUMN_LONG_BREAK_TIME = "long_break_time";

        /** Stored as integer. */
        public static final String COLUMN_POMODOROS_TO_LONG_BREAK = "pomodoros_to_long_break";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE "  + TABLE_NAME + " (" +
                _ID                               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME                       + " TEXT NOT NULL, " +
                COLUMN_TOTAL_TIME                 + " INTEGER NOT NULL, " +
                COLUMN_END_TIME                   + " INTEGER NOT NULL, " +
                COLUMN_WORK_TIME                  + " INTEGER NOT NULL, " +
                COLUMN_SHORT_BREAK_TIME           + " INTEGER NOT NULL, " +
                COLUMN_LONG_BREAK_TIME            + " INTEGER NOT NULL, " +
                COLUMN_POMODOROS_TO_LONG_BREAK    + " INTEGER NOT NULL);";

        // Indexes for COLUMNS projection
        public static final int POSITION_ID = 0;
        public static final int POSITION_NAME = 1;
        public static final int POSITION_TOTAL_TIME = 2;
        public static final int POSITION_END_TIME = 3;
        public static final int POSITION_WORK_TIME = 4;
        public static final int POSITION_SHORT_BREAK_TIME = 5;
        public static final int POSITION_LONG_BREAK_TIME = 6;
        public static final int POSITION_POMODOROS_TO_LONG_BREAK = 7;

        public static final List<String> COLUMNS = Collections.unmodifiableList(Arrays.asList(
                _ID,
                COLUMN_NAME,
                COLUMN_TOTAL_TIME,
                COLUMN_END_TIME,
                COLUMN_WORK_TIME,
                COLUMN_SHORT_BREAK_TIME,
                COLUMN_LONG_BREAK_TIME,
                COLUMN_POMODOROS_TO_LONG_BREAK
        ));
    }
}

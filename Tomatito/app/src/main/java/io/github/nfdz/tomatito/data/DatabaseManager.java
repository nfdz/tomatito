package io.github.nfdz.tomatito.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DatabaseManager {

    private static DatabaseManager sInstance;

    public static synchronized DatabaseManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private final DbHelper mDbHelper;

    private DatabaseManager(Context context) {
        mDbHelper = new DbHelper(context);
    }

    public Cursor queryAllPomodoros() {
        String table = Contract.PomodoroEntry.TABLE_NAME;
        String[] columns = Contract.PomodoroEntry.COLUMNS.toArray(new String[]{});
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = Contract.PomodoroEntry.COLUMN_END_TIME + " DESC";

        return mDbHelper.getReadableDatabase().query(table,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy);
    }

    public void insertPomodoro(FinishedPomodoro pomodoro) {
        mDbHelper.getWritableDatabase().insert(Contract.PomodoroEntry.TABLE_NAME,
                null,
                pomodoro.getContentValues());
    }

    public void editPomodoroName(FinishedPomodoro pomodoro, long id, String name) {
        ContentValues values = pomodoro.getContentValues();
        values.put(Contract.PomodoroEntry.COLUMN_NAME, name);

        String whereClause = Contract.PomodoroEntry._ID + " = ? ";
        String[] whereArgs = new String[]{ Long.toString(id) };

        mDbHelper.getWritableDatabase().update(Contract.PomodoroEntry.TABLE_NAME,
                values,
                whereClause,
                whereArgs);
    }
}


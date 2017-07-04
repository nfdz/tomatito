package io.github.nfdz.tomatito.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.AnyThread;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseManager {

    public interface DatabaseListener {
        @AnyThread
        void notifyChanges();
    }

    private static DatabaseManager sInstance;

    public static synchronized DatabaseManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private final DbHelper mDbHelper;
    private final List<DatabaseListener> mListeners;

    private DatabaseManager(Context context) {
        mDbHelper = new DbHelper(context);
        mListeners = new CopyOnWriteArrayList<>();
    }

    public void addListener(DatabaseListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(DatabaseListener listener) {
        mListeners.remove(listener);
    }

    private void notifyListeners() {
        for (DatabaseListener listener : mListeners) {
            listener.notifyChanges();
        }
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
        notifyListeners();
    }

    public void editPomodoroName(long id, FinishedPomodoro pomodoro, String name) {
        ContentValues values = pomodoro.getContentValues();
        values.put(Contract.PomodoroEntry.COLUMN_NAME, name);

        String whereClause = Contract.PomodoroEntry._ID + " = ? ";
        String[] whereArgs = new String[]{ Long.toString(id) };

        mDbHelper.getWritableDatabase().update(Contract.PomodoroEntry.TABLE_NAME,
                values,
                whereClause,
                whereArgs);
        notifyListeners();
    }
}


package io.github.nfdz.tomatito.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.AnyThread;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class manages all operations with the database.
 * As this is a small application and the database is not expected to escalate or become more
 * important in the future, this manager will be used to manage reading and writing in the database
 * (instead of a content provider that is usually the best choice)
 */
public class DatabaseManager {

    /**
     * This interface must be implemented in order to be notified when database changes.
     */
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
    private final Context mContext;

    private DatabaseManager(Context context) {
        mContext = context;
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

    /**
     * This method retrieves all pomodoro records stored in database.
     * @return Cursor
     */
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

    /**
     * This method inserts a pomodoro record in database.
     * @param pomodoro
     */
    public void insertPomodoro(FinishedPomodoro pomodoro) {
        mDbHelper.getWritableDatabase().insert(Contract.PomodoroEntry.TABLE_NAME,
                null,
                pomodoro.getContentValues());
        purgePomodoros(false);
        notifyListeners();
    }

    /**
     * This method edits a pomodoro record name with the given one.
     * @param id
     * @param pomodoro
     * @param name
     */
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

    public void deletePomodoro(long id) {
        String whereClause = Contract.PomodoroEntry._ID + " = ? ";
        String[] whereArgs = new String[]{ Long.toString(id) };

        mDbHelper.getWritableDatabase().delete(Contract.PomodoroEntry.TABLE_NAME,
                whereClause,
                whereArgs);
        notifyListeners();
    }

    /**
     * This method removes all stored pomodoros.
     */
    public void removeAllPomodoros() {
        mDbHelper.getWritableDatabase().delete(Contract.PomodoroEntry.TABLE_NAME, null, null);
        notifyListeners();
    }

    /**
     * This method removes all old pomodoros in order to grant that database do not surpasses
     * preferred storage limit.
     */
    public void purgePomodoros() {
        purgePomodoros(true);
    }

    private void purgePomodoros(boolean notify) {
        int limit = PreferencesUtils.getStorageLimit(mContext);
        String whereClause = Contract.PomodoroEntry._ID +
                " IN (SELECT " + Contract.PomodoroEntry._ID +
                " FROM " + Contract.PomodoroEntry.TABLE_NAME +
                " ORDER BY " + Contract.PomodoroEntry._ID +
                " DESC LIMIT -1 OFFSET " + limit + ")";
        String[] whereArgs = null;
        int rowsAffected = mDbHelper.getWritableDatabase().delete(Contract.PomodoroEntry.TABLE_NAME,
                whereClause,
                whereArgs);

        if (notify && rowsAffected > 0) {
            notifyListeners();
        }
    }
}


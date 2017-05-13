package com.example.hoanbk.todomvploader.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.example.hoanbk.todomvploader.data.Task;
import com.example.hoanbk.todomvploader.data.source.TasksDataSource;
import com.example.hoanbk.todomvploader.data.source.local.TasksPersistenceContract.TaskEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by hoanbk on 4/17/2017.
 */

public class TasksLocalDataSource implements TasksDataSource {

    private static TasksLocalDataSource sInstance;

    private TasksDbHelper mDbHelper;

    private SQLiteDatabase mDb;

    private TasksLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new TasksDbHelper(context);
        mDb = mDbHelper.getWritableDatabase();
    }

    public static TasksLocalDataSource getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TasksLocalDataSource(context);
        }
        return sInstance;
    }

    /**
     *
     * Note: {@Link LoadTasksCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty
     */
    @Override
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        try {

            String[] projection = {
                    TaskEntry.COLUMN_NAME_ENTRY_ID,
                    TaskEntry.COLUMN_NAME_TITLE,
                    TaskEntry.COLUMN_NAME_DESCRIPTION,
                    TaskEntry.COLUMN_NAME_COMPLETED
            };

            Cursor c = mDb.query(
                    TaskEntry.TABLE_NAME, projection, null, null, null, null, null);

            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
                    String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
                    String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
                    boolean completed = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
                    Task task = new Task(title, description, itemId, completed);
                    tasks.add(task);
                }
            }
            if (c != null) {
                c.close();
            }
        } catch (IllegalStateException e) {
            //
        }
        return tasks;
    }

    /**
     * Note: {@Link GetTaskCallback#onDataNotAvailable()} is fired if the {@Link Task} isn't
     * found
     * @param taskId
     * @param callback
     */
    @Override
    public Task getTask(@NonNull String taskId) {
        Task task = null;

        try {
            String[] projection = {
                    TaskEntry.COLUMN_NAME_ENTRY_ID,
                    TaskEntry.COLUMN_NAME_TITLE,
                    TaskEntry.COLUMN_NAME_DESCRIPTION,
                    TaskEntry.COLUMN_NAME_COMPLETED
            };

            String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
            String[] selectionArgs = {taskId};

            Cursor c = mDb.query(
                    TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null
            );

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
                String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
                String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
                int completed = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED));

                task = new Task(title, description, itemId, completed == 1);
            }
            if (c != null) {
                c.close();
            }
        } catch (IllegalStateException e) {

        }

        return task;
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.getId());
        values.put(TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());

        db.insert(TaskEntry.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void completeTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, true);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {task.getId()};

        db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        // TODO: 4/18/2017
    }

    @Override
    public void activateTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, false);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {task.getId()};

        db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        // TODO: 4/18/2017
    }

    @Override
    public void clearCompletedTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = { "1" };

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void refreshTasks() {
        // TODO: 4/18/2017
    }

    @Override
    public void deleteAllTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(TaskEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}

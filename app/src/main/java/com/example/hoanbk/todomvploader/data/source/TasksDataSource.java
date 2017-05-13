package com.example.hoanbk.todomvploader.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.hoanbk.todomvploader.data.Task;

import java.util.List;

/**
 * Created by hoanbk on 5/6/2017.
 */

public interface TasksDataSource {

    interface GetTaskCallback {

        void onTaskLoaded(Task task);

        void onDataAvailable();
    }

    @Nullable
    List<Task> getTasks();

    @Nullable
    Task getTask(@NonNull String taskId);

    void saveTask(@NonNull Task task);

    void completeTask(@NonNull Task task);

    void completeTask(@NonNull String taskId);

    void activateTask(@NonNull Task task);

    void activateTask(@NonNull String taskId);

    void clearCompletedTasks();

    void refreshTasks();

    void deleteAllTasks();

    void deleteTask(@NonNull String taskId);
}

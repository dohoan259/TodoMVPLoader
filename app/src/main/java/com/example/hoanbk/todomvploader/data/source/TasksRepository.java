package com.example.hoanbk.todomvploader.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.hoanbk.todomvploader.data.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by hoanbk on 4/16/2017.
 */

public class TasksRepository implements TasksDataSource {

    private static final String TAG = "TasksRepository";

    private static TasksRepository sInstance = null;

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;

    private List<TasksRepositoryObserver> mObservers = new ArrayList<>();

    /**
     * This variable has package local visibility so it can be accessed from tests
     */
    Map<String, Task> mCachedTasks;

    /**
     * marks the cache as invalid, to force an update the next time data is request. This variable
     * has package local visibility so it can be accessed from tests
     */
    boolean mCacheIsDirty = false;

    private TasksRepository(@NonNull TasksDataSource tasksRemoteDataSource,
                            @NonNull TasksDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * Returns the singleton
     */
    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource,
                                              TasksDataSource tasksLocalDataSource) {
        if (sInstance == null) {
            sInstance = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return sInstance;
    }

    /**
     * Used to force {@Link #getInsatnce(TasksDataSource, TasksDataSource)} to create a new instance
     * next time it's called
     */
    public static void destroyInstance() {sInstance = null;}

    public void addContentObserver(TasksRepositoryObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }


    public void removeContentObserver(TasksRepositoryObserver observer) {
        if (mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    private void notifyContentObserver() {
        for (TasksRepositoryObserver observer : mObservers) {
            observer.onTasksChanged();
        }
    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source whichever is
     * available first. this is done synchronously because it's used by {@Link TasksLoader},
     * which implements the async mechanism.
     */
    @Nullable
    @Override
    public List<Task> getTasks() {

        List<Task> tasks = null;

        if (!mCacheIsDirty) {
            // Respond immediately with cache if available and not dirty
            if (mCachedTasks != null) {
                tasks = getCachedTasks();
                return tasks;
            } else {
                // query local storage if available
                tasks = mTasksLocalDataSource.getTasks();
            }
        }
        // to simplify, we'll consider the local data source fresh when it has data
        if (tasks == null || tasks.isEmpty()) {
            // Grab remote data if cache is dirty or local data not available.
            tasks = mTasksRemoteDataSource.getTasks();
            // We copy the data to the device so we don't need query the network next time
            saveTasksInLocalDataSource(tasks);
        }

        processLoadedTasks(tasks);
        return getCachedTasks();
    }

    public boolean cachedTasksAvailable() {
        return mCachedTasks != null && !mCacheIsDirty;
    }

    public List<Task> getCachedTasks() {
        return mCachedTasks == null ? null : new ArrayList<>(mCachedTasks.values());
    }

    private void saveTasksInLocalDataSource(List<Task> tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
                mTasksLocalDataSource.saveTask(task);
            }
        }
    }

    private void processLoadedTasks(List<Task> tasks) {
        if (tasks == null) {
            mCachedTasks = null;
            mCacheIsDirty = false;
            return;
        }
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    @Override
    public Task getTask(@NonNull final String taskId) {
        checkNotNull(taskId);

        Task cachedTask = getTaskWithId(taskId);

        // Respond immediately with cache if available
        if (cachedTask != null) {
            return cachedTask;
        }

        // Is the task in the local data source? If not, query the network
        Task task = mTasksLocalDataSource.getTask(taskId);
        if (task == null) {
            task = mTasksRemoteDataSource.getTask(taskId);
        }

        return task;
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);

        mTasksRemoteDataSource.saveTask(task);
        mTasksLocalDataSource.saveTask(task);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);

        notifyContentObserver();
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);

        mTasksRemoteDataSource.completeTask(task);
        mTasksLocalDataSource.completeTask(task);

        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), completedTask);

        // update UI
        notifyContentObserver();
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.activateTask(task);
        mTasksLocalDataSource.activateTask(task);

        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), activeTask);

        // Update the UI
        notifyContentObserver();
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRemoteDataSource.clearCompletedTasks();
        mTasksLocalDataSource.clearCompletedTasks();

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }

        // Update the UI
        notifyContentObserver();
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
        notifyContentObserver();
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();

        // Update the UI
        notifyContentObserver();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        mTasksRemoteDataSource.deleteTask(checkNotNull(taskId));
        mTasksLocalDataSource.deleteTask(checkNotNull(taskId));

        mCachedTasks.remove(taskId);

        // Update the UI
        notifyContentObserver();
    }

    @Nullable
    private Task getTaskWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }

    public interface TasksRepositoryObserver {

        void onTasksChanged();
    }
}

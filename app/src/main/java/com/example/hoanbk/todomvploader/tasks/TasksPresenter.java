package com.example.hoanbk.todomvploader.tasks;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.hoanbk.todomvploader.addedittask.AddEditTaskActivity;
import com.example.hoanbk.todomvploader.data.Task;
import com.example.hoanbk.todomvploader.data.source.TasksLoader;
import com.example.hoanbk.todomvploader.data.source.TasksRepository;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by hoanbk on 4/16/2017.
 * Listens to user actions from the UI ({@Link TasksFragment}), retrieves the data and updates the
 * UI as required
 */

public class TasksPresenter implements TasksContract.Presenter,
        LoaderManager.LoaderCallbacks<List<Task>> {

    private static final String TAG = "TasksPresenter";

    private final static int TASKS_QUERY = 1;

    private final TasksRepository mTaskRepository;

    private final TasksContract.View mTasksView;

    private List<Task> mCurrentTasks;

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    private TasksLoader mLoader;

    private final LoaderManager mLoaderManager;

    public TasksPresenter(@NonNull TasksLoader loader, @NonNull LoaderManager loaderManager,
                          @NonNull TasksRepository taskRepository, @NonNull TasksContract.View tasksView) {
        mTaskRepository = checkNotNull(taskRepository, "tasksRepository cannot be null!");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mLoader = checkNotNull(loader, "loader cannot be null!");
        mLoaderManager = checkNotNull(loaderManager, "loader manager cannot be null!");

        mTasksView.setPresenter(this);
    }

    @Override
    public void start() {
//        Log.d(TAG, "start()");
        mLoaderManager.initLoader(TASKS_QUERY, null, this);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a task was successfully added, show snackbar
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && resultCode == Activity.RESULT_OK) {
            mTasksView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public Loader<List<Task>> onCreateLoader(int id, Bundle args) {
        mTasksView.setLoadIndicator(true);
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Task>> loader, List<Task> data) {
        mTasksView.setLoadIndicator(false);

        mCurrentTasks = data;
        if (mCurrentTasks == null) {
            mTasksView.showLoadingTaskError();
        } else {
            showFilteredTasks();
        }
    }

    private void showFilteredTasks() {
        List<Task> tasksToDisplay = new ArrayList<>();
        if (mCurrentTasks != null) {
            for (Task task : mCurrentTasks) {
                switch (mCurrentFiltering) {
                    case ALL_TASKS:
                        tasksToDisplay.add(task);
                        break;
                    case ACTIVE_TASKS:
                        if (task.isActive()) {
                            tasksToDisplay.add(task);
                        }
                        break;
                    case COMPLETED_TASKS:
                        if (task.isCompleted()) {
                            tasksToDisplay.add(task);
                        }
                        break;
                    default:
                        tasksToDisplay.add(task);
                        break;
                }
            }
        }

        processTasks(tasksToDisplay);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    /**
     * @param forceUpdate Pass in true to refresh the data in the {@Link TasksDataSource}
     */
    @Override
    public void loadTasks(boolean forceUpdate) {
        if (forceUpdate || mFirstLoad) {
            mFirstLoad = false;
            mTaskRepository.refreshTasks();
        } else {
            showFilteredTasks();
        }
    }

    private void processTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type
            processEmptyTasks();
        } else {
            // Show the list of tasks
            mTasksView.showTasks(tasks);
            // Set the filter label's text
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                mTasksView.showCompletedFilterLabel();
                break;
            default:
                mTasksView.showAllFilterLabel();
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                mTasksView.showNoCompletedTasks();
                break;
            default:
                mTasksView.showNoTasks();
        }
    }

    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "requestedTask cannot be null!");
        mTasksView.showTaskDetailsUi(requestedTask.getId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        checkNotNull(completedTask, "completedTask cannot be null!");
        mTaskRepository.completeTask(completedTask);
        mTasksView.showTaskMarkedComplete();
        loadTasks(false);
    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        checkNotNull(activeTask, "activeTask cannot be null!");
        mTaskRepository.activateTask(activeTask);
        mTasksView.showTaskMarkedActive();
        loadTasks(false);
    }

    @Override
    public void clearCompletedTask() {
        mTaskRepository.clearCompletedTasks();
        mTasksView.showCompletedTasksCleared();
        loadTasks(false);
    }

    /**
     * Sets the current task filtering type
     *
     * @param requestType Can be {@link TasksFilterType#ALL_TASKS},
     *                    {@link TasksFilterType#COMPLETED_TASKS}, or
     *                    {@link TasksFilterType#ACTIVE_TASKS}
     */
    @Override
    public void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }
}

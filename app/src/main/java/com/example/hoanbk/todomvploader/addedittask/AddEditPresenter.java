package com.example.hoanbk.todomvploader.addedittask;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.hoanbk.todomvploader.data.Task;
import com.example.hoanbk.todomvploader.data.source.TaskLoader;
import com.example.hoanbk.todomvploader.data.source.TasksRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by hoanbk on 4/26/2017.
 */

public class AddEditPresenter implements AddEditContract.Presenter,
        LoaderManager.LoaderCallbacks<Task>{

    private static final int TASK_QUERY = 2;

    private String mTaskId;

    private TasksRepository mTaskRepository;

    private AddEditContract.View mAddEditView;

    private TaskLoader mTaskLoader;

    private final LoaderManager mLoaderManager;

    public AddEditPresenter(@Nullable String taskId,
                            @NonNull TasksRepository repository,
                            @NonNull AddEditContract.View addEditView,
                            @NonNull TaskLoader taskLoader,
                            @NonNull LoaderManager loaderManager) {
        mTaskId = taskId;
        mTaskRepository = checkNotNull(repository, "TasksRepository can not be null!");
        mAddEditView = checkNotNull(addEditView, "AddEditView can not be null!");
        mTaskLoader = checkNotNull(taskLoader);
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");

        mAddEditView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewTask()) {
            mLoaderManager.initLoader(TASK_QUERY, null, this);
        }
    }

    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        } else {
            updateTask(title, description);
        }
    }

    @Override
    public Loader<Task> onCreateLoader(int id, Bundle args) {
        if (mTaskId == null) {
            return null;
        }
        return mTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<Task> loader, Task data) {
        if (data != null) {
            if (mAddEditView.isActive()) {
                mAddEditView.setDescription(data.getDescription());
                mAddEditView.setTitle(data.getTitle());
            }
        } else {
            // the view may not be able handle UI updates anymore
            if (mAddEditView.isActive()) {
                mAddEditView.showEmptyTaskError();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void createTask(String title, String description) {
        Task newTask = new Task(title, description);
        if (newTask.isEmpty()) {
            mAddEditView.showEmptyTaskError();
        } else {
            mTaskRepository.saveTask(newTask);
            mAddEditView.showListTasks();
        }
    }

    private void updateTask(String title, String description) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() is called but task is new.");
        }
        Task updateTask = new Task(title, description, mTaskId);
        if (updateTask.isEmpty()) {
            mAddEditView.showEmptyTaskError();
            return;
        }
        mTaskRepository.saveTask(updateTask);
        mAddEditView.showListTasks();
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }
}

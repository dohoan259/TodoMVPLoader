package com.example.hoanbk.todomvploader.taskdetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.hoanbk.todomvploader.data.Task;
import com.example.hoanbk.todomvploader.data.source.TaskLoader;
import com.example.hoanbk.todomvploader.data.source.TasksRepository;
import com.google.common.base.Strings;

/**
 * Created by hoanbk on 4/29/2017.
 */

public class TaskDetailPresenter implements TaskDetailContract.Presenter,
        LoaderManager.LoaderCallbacks<Task>{

    private static final int TASK_QUERY = 4;

    private String mTaskId;

    private TaskDetailContract.View mTaskDetailView;

    private TasksRepository mTaskRepository;

    private TaskLoader mTaskLoader;

    private LoaderManager mLoaderManager;

    public TaskDetailPresenter(@Nullable String taskId,
                               TasksRepository taskRepository,
                               TaskDetailContract.View taskDetailView,
                               TaskLoader taskLoader,
                               LoaderManager loaderManager) {
        mTaskId = taskId;
        mTaskRepository = taskRepository;
        mTaskDetailView = taskDetailView;
        mTaskLoader = taskLoader;
        mLoaderManager = loaderManager;

        mTaskDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(TASK_QUERY, null, this);
    }

    @Override
    public void editTask() {
        if(Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTaskDetailView.showEditTask(mTaskId);
    }

    @Override
    public void deleteTask() {
        if(Strings.isNullOrEmpty(mTaskId)){
            mTaskDetailView.showMissingTask();
            return;
        }
        mTaskRepository.deleteTask(mTaskId);
        mTaskDetailView.showTaskDeleted();
    }

    @Override
    public void completeTask() {
        if(Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTaskRepository.completeTask(mTaskId);
        mTaskDetailView.showTaskMarkedComplete();
    }

    @Override
    public void activateTask() {
        if(Strings.isNullOrEmpty(mTaskId)){
            mTaskDetailView.showMissingTask();
            return;
        }
        mTaskRepository.activateTask(mTaskId);
        mTaskDetailView.showTaskMarkedActive();
    }

    private void showTask(@NonNull Task task) {
        String title = task.getTitle();
        String description = task.getDescription();

        if(Strings.isNullOrEmpty(title)) {
            mTaskDetailView.hideTitle();
        } else {
            mTaskDetailView.showTitle(title);
        }

        if(Strings.isNullOrEmpty(description)) {
            mTaskDetailView.hideDescription();
        } else {
            mTaskDetailView.showDescription(description);
        }
        mTaskDetailView.showCompletionStatus(task.isCompleted());
    }

    @Override
    public Loader<Task> onCreateLoader(int id, Bundle args) {
        if (mTaskId == null) {
            return null;
        }
        mTaskDetailView.setLoadingIndicator(true);
        return mTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<Task> loader, Task data) {
        if (data != null) {
            showTask(data);
        } else {
            mTaskDetailView.showMissingTask();
        }
    }

    @Override
    public void onLoaderReset(Loader<Task> loader) {

    }
}

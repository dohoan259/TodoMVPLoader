package com.example.hoanbk.todomvploader.data.source;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.hoanbk.todomvploader.data.Task;
import com.example.hoanbk.todomvploader.prod.Injection;

/**
 * Created by hoanbk on 5/6/2017.
 */

public class TaskLoader extends AsyncTaskLoader<Task>
        implements TasksRepository.TasksRepositoryObserver {

    private final String mTaskId;
    private TasksRepository mRepository;

    public TaskLoader(String taskId, Context context) {
        super(context);
        mTaskId = taskId;
        mRepository = Injection.provideTasksRepository(context);
    }

    @Override
    public Task loadInBackground() {
        return mRepository.getTask(mTaskId);
    }

    @Override
    public void deliverResult(Task data) {
        if (isReset()) {
            return;
        }

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mRepository.cachedTasksAvailable()) {
            deliverResult(mRepository.getTask(mTaskId));
        }

        mRepository.addContentObserver(this);

        if (takeContentChanged() || !mRepository.cachedTasksAvailable()) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        onStopLoading();
        mRepository.removeContentObserver(this);
    }

    @Override
    public void onTasksChanged() {
        if (isStarted()) {
            forceLoad();
        }
    }
}

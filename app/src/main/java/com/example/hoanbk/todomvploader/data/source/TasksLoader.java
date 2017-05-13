package com.example.hoanbk.todomvploader.data.source;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.example.hoanbk.todomvploader.data.Task;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by hoanbk on 5/6/2017.
 * Custom {@Link android.content.Loader} for a list of {@Link Task}, using the
 * {@Link TasksRepository} as its resource. This Loader is a {@Link AsyncTaskLoader} so it queries
 * the data asynchronous.
 */

public class TasksLoader extends AsyncTaskLoader<List<Task>> implements TasksRepository.TasksRepositoryObserver {

    private TasksRepository mRepository;

    public TasksLoader(Context context, @NonNull TasksRepository repository) {
        super(context);
        checkNotNull(repository);
        mRepository = repository;
    }

    @Override
    public List<Task> loadInBackground() {
        return mRepository.getTasks();
    }

    @Override
    public void deliverResult(List<Task> data) {
        if (isReset()) {
            return;
        }

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        // Deliver any previous loaded data immediately if available
        if (mRepository.cachedTasksAvailable()) {
            deliverResult(mRepository.getCachedTasks());
        }

        // Begin monitoring the underlying data source
        mRepository.addContentObserver(this);

        if (takeContentChanged() || !mRepository.cachedTasksAvailable()) {
            // When a change has been delivered or the repository cache isn't available, we force
            // a load
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
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

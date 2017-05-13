package com.example.hoanbk.todomvploader.statistics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.hoanbk.todomvploader.data.Task;
import com.example.hoanbk.todomvploader.data.source.TasksLoader;
import com.example.hoanbk.todomvploader.data.source.TasksRepository;
import com.example.hoanbk.todomvploader.data.source.TasksDataSource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by hoanbk on 5/1/2017.
 */

public class StatisticsPresenter implements StatisticsContract.Presenter,
        LoaderManager.LoaderCallbacks<List<Task>>{

    private static final int TASK_QUERY = 3;

    final private StatisticsContract.View mStatisticsView;

    private TasksLoader mTasksLoader;

    private LoaderManager mLoaderManager;

    public StatisticsPresenter(@NonNull StatisticsContract.View statisticsView,
                               @NonNull TasksLoader tasksLoader,
                               @NonNull LoaderManager loaderManager) {
        mStatisticsView = checkNotNull(statisticsView, "statisticsView cannot be null.");
        mTasksLoader = checkNotNull(tasksLoader, "tasksLoader cannot be null!");
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");

        mStatisticsView.setPresenter(this);
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(TASK_QUERY, null, this);
    }

    @Override
    public Loader<List<Task>> onCreateLoader(int id, Bundle args) {
        mStatisticsView.showIndicator(true);
        return mTasksLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Task>> loader, List<Task> data) {
        loadStatistics(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Task>> loader) {

    }

    private void loadStatistics(List<Task> tasks) {
        if (tasks == null) {
            mStatisticsView.showLoadingStatisticsError();
        } else {
            int activeTasks = 0;
            int completdTasks = 0;

            // Calculate
            for (Task task : tasks) {
                if (task.isActive()) {
                    activeTasks++;
                } else {
                    completdTasks++;
                }
            }

            mStatisticsView.showIndicator(false);

            mStatisticsView.showStatistics(activeTasks, completdTasks);
        }
    }
}

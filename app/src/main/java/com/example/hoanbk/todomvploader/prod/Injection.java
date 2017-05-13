package com.example.hoanbk.todomvploader.prod;

import android.content.Context;

import com.example.hoanbk.todomvploader.data.source.TasksRepository;
import com.example.hoanbk.todomvploader.data.source.local.TasksLocalDataSource;
import com.example.hoanbk.todomvploader.data.source.remote.TasksRemoteDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by hoanbk on 4/17/2017.
 */

public class Injection {

    public static TasksRepository provideTasksRepository(Context context) {
        checkNotNull(context);
        return TasksRepository.getInstance(TasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(context));
    }
}

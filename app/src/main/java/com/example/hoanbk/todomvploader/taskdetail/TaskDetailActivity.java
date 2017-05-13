package com.example.hoanbk.todomvploader.taskdetail;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.hoanbk.todomvploader.R;
import com.example.hoanbk.todomvploader.data.source.TaskLoader;
import com.example.hoanbk.todomvploader.prod.Injection;
import com.example.hoanbk.todomvploader.utils.ActivityUtils;

public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskdetail_act);

        // set up actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        FragmentManager fm = getSupportFragmentManager();
        TaskDetailFragment taskDetailFragment =
                (TaskDetailFragment) fm.findFragmentById(R.id.contentFrame);
        if (taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance(taskId);

            ActivityUtils.addFragmentToActivity(fm, taskDetailFragment, R.id.contentFrame);
        }

        // Create the presenter
        new TaskDetailPresenter(
                taskId,
                Injection.provideTasksRepository(getApplicationContext()),
                taskDetailFragment,
                new TaskLoader(taskId, getApplicationContext()),
                getSupportLoaderManager());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

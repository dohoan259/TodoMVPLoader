package com.example.hoanbk.todomvploader.addedittask;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.hoanbk.todomvploader.R;
import com.example.hoanbk.todomvploader.data.source.TaskLoader;
import com.example.hoanbk.todomvploader.prod.Injection;
import com.example.hoanbk.todomvploader.utils.ActivityUtils;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 1;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        String taskId = getIntent().getStringExtra(AddEditFragment.ARGUMENT_EDIT_TASK_ID);

        setToolbarTitle(taskId);

        // add fragment
        FragmentManager fm = getSupportFragmentManager();
        AddEditFragment fragment = (AddEditFragment) fm.findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = AddEditFragment.newInstance();

            if (getIntent().hasExtra(AddEditFragment.ARGUMENT_EDIT_TASK_ID)) {
                Bundle args = new Bundle();
                args.putString(AddEditFragment.ARGUMENT_EDIT_TASK_ID, taskId);
                fragment.setArguments(args);
            }
            ActivityUtils.addFragmentToActivity(fm, fragment, R.id.contentFrame);
        }

        // TODO: 4/26/2017

        // Create the loader and presenter
        TaskLoader taskLoader = new TaskLoader(taskId, getApplicationContext());
        new AddEditPresenter(
                taskId,
                Injection.provideTasksRepository(getApplicationContext()),
                fragment,
                taskLoader,
                getSupportLoaderManager());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setToolbarTitle(String taskId) {
        if (taskId == null) {
            mActionBar.setTitle(R.string.add_task);
        } else {
            mActionBar.setTitle(R.string.edit_task);
        }
    }
}

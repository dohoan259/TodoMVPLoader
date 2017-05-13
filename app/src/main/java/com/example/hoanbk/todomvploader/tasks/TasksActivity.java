package com.example.hoanbk.todomvploader.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.hoanbk.todomvploader.R;
import com.example.hoanbk.todomvploader.data.source.TasksLoader;
import com.example.hoanbk.todomvploader.data.source.TasksRepository;
import com.example.hoanbk.todomvploader.prod.Injection;
import com.example.hoanbk.todomvploader.statistics.StatisticsActivity;
import com.example.hoanbk.todomvploader.utils.ActivityUtils;

public class TasksActivity extends AppCompatActivity {

    private static final String CURRENT_FILTERING_KEY = "FILTERING_KEY";

    private DrawerLayout mDrawerLayout;

    private TasksPresenter mTasksPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Set up the navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        TasksFragment tasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // create the fragment
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }


        // Create the presenter
//        TasksRepository repository = Injection.provideTasksRepository(getApplicationContext());
//        TasksLoader tasksLoader = new TasksLoader(getApplicationContext(), repository);
//        mTasksPresenter = new TasksPresenter(
//                tasksLoader,
//                getSupportLoaderManager(),
//                repository,
//                tasksFragment
//        );

        TasksLoader tasksLoader = new TasksLoader(getApplicationContext(),
                Injection.provideTasksRepository(getApplicationContext()));
        mTasksPresenter = new TasksPresenter(
                tasksLoader,
                getSupportLoaderManager(),
                Injection.provideTasksRepository(getApplicationContext()),
                tasksFragment
        );

        if(savedInstanceState != null) {
            TasksFilterType filterType =
                    (TasksFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mTasksPresenter.setFiltering(filterType);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mTasksPresenter.getFiltering());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list_navigation_menu_item:
                        // Do thing, we're already on that screen
                        break;
                    case R.id.statistics_navigation_menu_item:
                        Intent intent =
                                new Intent(TasksActivity.this, StatisticsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                // Close the navigation drawer when an item is selected.
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
}

package com.example.hoanbk.todomvploader.statistics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.hoanbk.todomvploader.R;
import com.example.hoanbk.todomvploader.data.source.TasksLoader;
import com.example.hoanbk.todomvploader.prod.Injection;
import com.example.hoanbk.todomvploader.utils.ActivityUtils;

public class StatisticsActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_act);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // set up fragment
        FragmentManager fm = getSupportFragmentManager();
        StatisticsFragment statisticsFragment =
                (StatisticsFragment) fm.findFragmentById(R.id.contentFrame);
        if (statisticsFragment == null) {
            statisticsFragment = StatisticsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(fm, statisticsFragment, R.id.contentFrame);
        }

        TasksLoader tasksLoader = new TasksLoader(getApplicationContext(),
                Injection.provideTasksRepository(getApplication()));
        new StatisticsPresenter(statisticsFragment, tasksLoader, getSupportLoaderManager());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
                        NavUtils.navigateUpFromSameTask(StatisticsActivity.this);
                        break;
                    case R.id.statistics_navigation_menu_item:

                        break;
                }
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
}

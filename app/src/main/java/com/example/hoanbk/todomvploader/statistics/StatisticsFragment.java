package com.example.hoanbk.todomvploader.statistics;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hoanbk.todomvploader.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment implements StatisticsContract.View{

    private StatisticsContract.Presenter mPresenter;

    private TextView mStatisticsTV;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void setPresenter(StatisticsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.statistics_frag, container, false);
        mStatisticsTV = (TextView) root.findViewById(R.id.statistics);
        return root;
    }

    @Override
    public void showIndicator(boolean active) {
        if(active) {
            mStatisticsTV.setText(R.string.loading);
        } else {
            mStatisticsTV.setText("");
        }
    }

    @Override
    public void showStatistics(int numberActiveTasks, int numberCompletedTasks) {
        if(numberActiveTasks == 0 && numberCompletedTasks == 0) {
            mStatisticsTV.setText(R.string.no_tasks_all);
        } else {
            mStatisticsTV.setText("Active tasks: " + numberActiveTasks + '\n'
            + "Completed tasks: " + numberCompletedTasks);
        }
    }

    @Override
    public void showLoadingStatisticsError() {
        mStatisticsTV.setText(R.string.loading_tasks_error);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}

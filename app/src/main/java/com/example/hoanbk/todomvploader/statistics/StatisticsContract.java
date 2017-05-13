package com.example.hoanbk.todomvploader.statistics;

import com.example.hoanbk.todomvploader.base.BasePresenter;
import com.example.hoanbk.todomvploader.base.BaseView;

/**
 * Created by hoanbk on 5/1/2017.
 */

public interface StatisticsContract {

    interface View extends BaseView<Presenter> {

        void showIndicator(boolean active);

        void showStatistics(int numberActiveTasks, int numberCompletedTasks);

        void showLoadingStatisticsError();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

    }
}

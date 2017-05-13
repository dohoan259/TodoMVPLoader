package com.example.hoanbk.todomvploader.addedittask;

import com.example.hoanbk.todomvploader.base.BasePresenter;
import com.example.hoanbk.todomvploader.base.BaseView;

/**
 * Created by hoanbk on 4/26/2017.
 */

public interface AddEditContract {

    interface View extends BaseView<Presenter> {

        void setTitle(String title);

        void setDescription(String description);

        void showListTasks();

        void showEmptyTaskError();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void saveTask(String title, String description);
    }
}

package com.example.hoanbk.todomvploader.taskdetail;

import com.example.hoanbk.todomvploader.base.BasePresenter;
import com.example.hoanbk.todomvploader.base.BaseView;

/**
 * Created by hoanbk on 4/29/2017.
 */

public interface TaskDetailContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showMissingTask();

        void hideTitle();

        void showTitle(String title);

        void hideDescription();

        void showDescription(String description);

        void showCompletionStatus(boolean complete);

        void showEditTask(String taskId);

        void showTaskDeleted();

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void editTask();

        void deleteTask();

        void completeTask();

        void activateTask();
    }
}

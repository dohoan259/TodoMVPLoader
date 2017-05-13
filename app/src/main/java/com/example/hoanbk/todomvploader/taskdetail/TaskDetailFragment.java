package com.example.hoanbk.todomvploader.taskdetail;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.hoanbk.todomvploader.R;
import com.example.hoanbk.todomvploader.addedittask.AddEditFragment;
import com.example.hoanbk.todomvploader.addedittask.AddEditTaskActivity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskDetailFragment extends Fragment implements TaskDetailContract.View{

    public static final String ARGUMENT_TASK_ID = "TASK_ID";

    public static final int EXTRA_EDIT_TASK = 1;

    private TaskDetailContract.Presenter mPresenter;

    private CheckBox mCompletedStatus;

    private TextView mDetailTitle;

    private TextView mDetailDescription;

    public static TaskDetailFragment newInstance(@NonNull String taskId) {
        Bundle args = new Bundle();
        args.putString(ARGUMENT_TASK_ID, taskId);
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TaskDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void setPresenter(TaskDetailContract.Presenter presenter) {
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
        View root = inflater.inflate(R.layout.taskdetail_frag, container, false);
        mCompletedStatus = (CheckBox) root.findViewById(R.id.task_detail_complete);
        mDetailTitle = (TextView) root.findViewById(R.id.task_detail_title);
        mDetailDescription = (TextView) root.findViewById(R.id.task_detail_description);

        FloatingActionButton fab = (FloatingActionButton)
                getActivity().findViewById(R.id.fab_edit_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.editTask();
            }
        });
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mPresenter.deleteTask();
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EXTRA_EDIT_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().finish();
            }
        }
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if(active) {
            mDetailTitle.setText("");
            mDetailDescription.setText(R.string.loading);
        }
    }

    @Override
    public void showMissingTask() {
        mDetailTitle.setText("");
        mDetailDescription.setText(R.string.no_data);
    }

    @Override
    public void hideTitle() {
        mDetailTitle.setVisibility(View.GONE);
    }

    @Override
    public void showTitle(String title) {
        mDetailTitle.setVisibility(View.VISIBLE);
        mDetailTitle.setText(title);
    }

    @Override
    public void hideDescription() {
        mDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void showDescription(String description) {
        mDetailDescription.setVisibility(View.VISIBLE);
        mDetailDescription.setText(description);
    }

    @Override
    public void showCompletionStatus(boolean complete) {
        mCompletedStatus.setChecked(complete);
        mCompletedStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mPresenter.completeTask();
                } else {
                    mPresenter.activateTask();
                }
            }
        });
    }

    @Override
    public void showEditTask(String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(AddEditFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        startActivityForResult(intent, EXTRA_EDIT_TASK);
    }

    @Override
    public void showTaskDeleted() {
        getActivity().finish();
    }

    @Override
    public void showTaskMarkedComplete() {
        Snackbar.make(getView(), getString(R.string.task_marked_complete), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTaskMarkedActive() {
        Snackbar.make(getView(), getString(R.string.task_marked_active), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}

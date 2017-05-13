package com.example.hoanbk.todomvploader.addedittask;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.hoanbk.todomvploader.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditFragment extends Fragment implements AddEditContract.View{

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private EditText mTitle;
    private EditText mDescription;

    private AddEditContract.Presenter mPresenter;

    public static AddEditFragment newInstance() {
        return new AddEditFragment();
    }

    public AddEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void setPresenter(@NonNull AddEditContract.Presenter presenter) {
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
        View root = inflater.inflate(R.layout.addtask_frag, container, false);
        mTitle = (EditText) root.findViewById(R.id.add_task_title);
        mDescription = (EditText) root.findViewById(R.id.add_task_description);

        FloatingActionButton fab_add_task =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);
        fab_add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.saveTask(mTitle.getText().toString(), mDescription.getText().toString());
            }
        });
        return root;
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public void showListTasks() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(getView(), R.string.empty_task_message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}

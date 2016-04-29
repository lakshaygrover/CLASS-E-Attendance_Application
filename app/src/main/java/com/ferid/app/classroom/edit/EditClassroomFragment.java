/*
 * Copyright (C) 2016 Ferid Cafer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferid.app.classroom.edit;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.EditClassroomsAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.enums.ClassroomPopup;
import com.ferid.app.classroom.interfaces.AdapterClickListener;
import com.ferid.app.classroom.interfaces.OnAlertClick;
import com.ferid.app.classroom.interfaces.PopupClickListener;
import com.ferid.app.classroom.interfaces.PromptListener;
import com.ferid.app.classroom.material_dialog.CustomAlertDialog;
import com.ferid.app.classroom.material_dialog.PromptDialog;
import com.ferid.app.classroom.model.Classroom;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/15/2015.<br />
 * Adds and removes classes.
 */
public class EditClassroomFragment extends Fragment {

    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView list;
    private ArrayList<Classroom> arrayList = new ArrayList<>();
    private EditClassroomsAdapter adapter;

    private TextView emptyText; //empty list view text


    public EditClassroomFragment() {}

    public static EditClassroomFragment newInstance() {
        EditClassroomFragment editClassroomFragment = new EditClassroomFragment();
        return editClassroomFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.refreshable_list, container, false);

        context = rootView.getContext();

        list = (RecyclerView) rootView.findViewById(R.id.list);
        adapter = new EditClassroomsAdapter(context, arrayList);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setHasFixedSize(true);

        emptyText = (TextView) rootView.findViewById(R.id.emptyText);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SelectClassrooms().execute();
            }
        });

        addAdapterClickListener();
        addPopupClickListener();

        new SelectClassrooms().execute();


        return rootView;
    }

    /**
     * Set empty list text
     */
    private void setEmptyText() {
        if (emptyText != null) {
            if (arrayList.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
            } else {
                emptyText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Check if the given classroom name already exists
     * @param classroomName Selected classroom
     * @return
     */
    private boolean isAlreadyExist(String classroomName) {
        boolean isAlreadyExist = false;

        for (Classroom classroom : arrayList) {
            if (classroom.getName().equals(classroomName)) {
                isAlreadyExist = true;
                break;
            }
        }

        return isAlreadyExist;
    }

    /**
     * Add new class item
     */
    public void addClassroom() {
        final PromptDialog promptDialog = new PromptDialog(context);
        promptDialog.setPositiveButton(getString(R.string.ok));
        promptDialog.setAllCaps();
        promptDialog.setAlphanumeric();
        promptDialog.setOnPositiveClickListener(new PromptListener() {
            @Override
            public void OnPrompt(String promptText) {
                closeKeyboard();

                promptDialog.dismiss();

                if (!TextUtils.isEmpty(promptText)) {
                    if (!isAlreadyExist(promptText)) {
                        new InsertClassroom().execute(promptText);
                    } else {
                        //alert
                        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
                        customAlertDialog.setMessage(getString(R.string.couldNotInsertClassroom));
                        customAlertDialog.setPositiveButtonText(getString(R.string.ok));
                        customAlertDialog.showDialog();
                    }
                }
            }
        });
        promptDialog.show();
    }

    /**
     * Change the selected class name
     * @param classroomId current classroom to be changed
     * @param content current name of the classroom
     */
    public void editClassroom(final int classroomId, String content) {
        final PromptDialog promptDialog = new PromptDialog(context);
        promptDialog.setContent(content);
        promptDialog.setPositiveButton(getString(R.string.ok));
        promptDialog.setAllCaps();
        promptDialog.setAlphanumeric();
        promptDialog.setOnPositiveClickListener(new PromptListener() {
            @Override
            public void OnPrompt(String promptText) {
                closeKeyboard();

                promptDialog.dismiss();

                if (!TextUtils.isEmpty(promptText)) {
                    new UpdateClassroom().execute(String.valueOf(classroomId), promptText);
                }
            }
        });
        promptDialog.show();
    }

    /**
     * Delete classroom
     * @param classroom Selected classroom
     */
    private void deleteClassroom(final Classroom classroom) {
        //show alert before deleting
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setMessage(classroom.getName()
                + getString(R.string.sureToDelete));
        customAlertDialog.setPositiveButtonText(getString(R.string.delete));
        customAlertDialog.setNegativeButtonText(getString(R.string.cancel));
        customAlertDialog.setOnClickListener(new OnAlertClick() {
            @Override
            public void OnPositive() {
                new DeleteClassroom().execute(classroom.getId());
            }

            @Override
            public void OnNegative() {
                //do nothing
            }
        });
        customAlertDialog.showDialog();
    }

    /**
     * Go inside classroom to add, change or delete students
     * @param classroom
     */
    private void showStudents(Classroom classroom) {
        Intent intent = new Intent(context, EditStudentActivity.class);
        intent.putExtra("classroom", classroom);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.move_in_from_bottom,
                R.anim.stand_still);
    }

    /**
     * List item click event
     */
    private void addAdapterClickListener() {
        adapter.setAdapterClickListener(new AdapterClickListener() {
            @Override
            public void OnItemClick(int position) {
                if (arrayList != null && arrayList.size() > position) {
                    showStudents(arrayList.get(position));
                }
            }
        });
    }

    /**
     * Pop-up menu item click event
     */
    public void addPopupClickListener() {
        adapter.setPopupClickListener(new PopupClickListener() {
            @Override
            public void OnPopupClick(int itemPosition, int menuPosition) {
                if (arrayList != null && arrayList.size() > itemPosition) {
                    Classroom classroom = arrayList.get(itemPosition);

                    if (menuPosition == ClassroomPopup.CHANGE_NAME.getValue()) {
                        editClassroom(classroom.getId(), classroom.getName());
                    } else if (menuPosition == ClassroomPopup.DELETE_CLASSROOM.getValue()) {
                        deleteClassroom(classroom);
                    }
                }
            }
        });
    }

    /**
     * Select classrooms from DB
     */
    private class SelectClassrooms extends AsyncTask<Void, Void, ArrayList<Classroom>> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected ArrayList<Classroom> doInBackground(Void... params) {
            DatabaseManager databaseManager = new DatabaseManager(context);
            ArrayList<Classroom> tmpList = databaseManager.selectClassrooms();

            return tmpList;
        }

        @Override
        protected void onPostExecute(ArrayList<Classroom> tmpList) {
            swipeRefreshLayout.setRefreshing(false);

            arrayList.clear();

            if (tmpList != null) {
                arrayList.addAll(tmpList);
                adapter.notifyDataSetChanged();

                setEmptyText();
            }
        }
    }

    /**
     * Insert classroom name into DB
     */
    private class InsertClassroom extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String classroom = params[0];
            DatabaseManager databaseManager = new DatabaseManager(context);
            boolean isSuccessful = databaseManager.insertClassroom(classroom);

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) {
                new SelectClassrooms().execute();
            }
        }
    }

    /**
     * Update classroom name in the DB
     */
    private class UpdateClassroom extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String classroomId = params[0];
            String newName = params[1];
            DatabaseManager databaseManager = new DatabaseManager(context);
            boolean isSuccessful = databaseManager.updateClassroomName(classroomId, newName);

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) {
                new SelectClassrooms().execute();
            }
        }
    }

    /**
     * Delete a classroom item from DB
     */
    private class DeleteClassroom extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            int classroomId = params[0];
            DatabaseManager databaseManager = new DatabaseManager(context);
            boolean isSuccessful = databaseManager.deleteClassroom(classroomId);

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) {
                new SelectClassrooms().execute();
            }
        }
    }

    /**
     * Closes keyboard for disabling interruption
     */
    private void closeKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {}
    }
}
/*
 * Copyright (C) 2015 Ferid Cafer
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.ClassroomAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.interfaces.OnClick;
import com.ferid.app.classroom.interfaces.OnPrompt;
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
    private ListView list;
    private ArrayList<Classroom> arrayList;
    private ClassroomAdapter adapter;


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

        View rootView = inflater.inflate(R.layout.simple_listview, container, false);

        context = rootView.getContext();

        list = (ListView) rootView.findViewById(R.id.list);
        arrayList = new ArrayList<Classroom>();
        adapter = new ClassroomAdapter(context, R.layout.simple_text_item_big, arrayList);
        list.setAdapter(adapter);

        //empty list view text
        TextView emptyText = (TextView) rootView.findViewById(R.id.emptyText);
        list.setEmptyView(emptyText);

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

        setListItemClickListener();

        new SelectClassrooms().execute();


        return rootView;
    }

    /**
     * setOnItemClickListener & setOnItemLongClickListener
     */
    private void setListItemClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    Intent intent = new Intent(context, EditStudentActivity.class);
                    intent.putExtra("classroom", arrayList.get(position));
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.move_in_from_bottom,
                            R.anim.stand_still);
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    final Classroom classroom = arrayList.get(position);

                    //alert
                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
                    customAlertDialog.setMessage(classroom.getName()
                            + getString(R.string.sureToDelete));
                    customAlertDialog.setPositiveButtonText(getString(R.string.delete));
                    customAlertDialog.setNegativeButtonText(getString(R.string.cancel));
                    customAlertDialog.setOnClickListener(new OnClick() {
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
                return true;
            }
        });
    }

    /**
     * Add new class item
     */
    public void addNewItem() {
        final PromptDialog promptDialog = new PromptDialog(context);
        promptDialog.setTitle(getString(R.string.classroomName));
        promptDialog.setPositiveButton(getString(R.string.ok));
        promptDialog.setAllCaps();
        promptDialog.setValidateAlphanumeric();
        promptDialog.setOnPositiveClickListener(new OnPrompt() {
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
     * Check if the given classroom name already exists
     * @param classroomName
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
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {}
    }
}
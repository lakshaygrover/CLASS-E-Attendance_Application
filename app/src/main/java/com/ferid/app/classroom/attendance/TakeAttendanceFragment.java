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

package com.ferid.app.classroom.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.ClassroomAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.model.Classroom;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/15/2015.<br />
 * Shows classes to choose to take an attendance.
 */
public class TakeAttendanceFragment extends Fragment {
    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView list;
    private ArrayList<Classroom> arrayList;
    private ClassroomAdapter adapter;


    public TakeAttendanceFragment() {}

    public static TakeAttendanceFragment newInstance() {
        TakeAttendanceFragment takeAttendanceFragment = new TakeAttendanceFragment();
        return takeAttendanceFragment;
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
        arrayList = new ArrayList<>();
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
     * setOnItemClickListener
     */
    private void setListItemClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    Intent intent = new Intent(context, AttendanceActivity.class);
                    intent.putExtra("classroom", arrayList.get(position));
                    startActivityForResult(intent, 0);
                    getActivity().overridePendingTransition(R.anim.move_in_from_bottom,
                            R.anim.stand_still);
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
            ArrayList<Classroom> tmpList = databaseManager.selectClassroomsWithStudentNumber();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Snackbar.make(list, getString(R.string.saved), Snackbar.LENGTH_LONG).show();
        }
    }
}
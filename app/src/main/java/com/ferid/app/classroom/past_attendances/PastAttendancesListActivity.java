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

package com.ferid.app.classroom.past_attendances;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.PastAttendancesAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.enums.PastAttendancePopup;
import com.ferid.app.classroom.interfaces.AdapterClickListener;
import com.ferid.app.classroom.interfaces.OnAlertClick;
import com.ferid.app.classroom.interfaces.PopupClickListener;
import com.ferid.app.classroom.material_dialog.CustomAlertDialog;
import com.ferid.app.classroom.model.Attendance;
import com.ferid.app.classroom.model.Classroom;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/16/2015.<br />
 * Shows the list of past attendances.
 */
public class PastAttendancesListActivity extends AppCompatActivity {
    private Context context;

    private RecyclerView list;
    private ArrayList<Attendance> arrayList = new ArrayList<>();
    private PastAttendancesAdapter adapter;

    private TextView emptyText; //empty list view text

    private Classroom classroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_with_toolbar);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            classroom = (Classroom) args.getSerializable("classroom");
        }

        context = this;

        //toolbar
        setToolbar();

        list = (RecyclerView) findViewById(R.id.list);
        adapter = new PastAttendancesAdapter(context, arrayList);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setHasFixedSize(true);

        emptyText = (TextView) findViewById(R.id.emptyText);
        emptyText.setText(getString(R.string.emptyMessagePastAttendance));

        addAdapterClickListener();
        addPopupClickListener();

        new SelectAttendances().execute();
    }

    /**
     * Create toolbar and set its attributes
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(getString(R.string.pastAttendances));
        toolbar.setSubtitle(classroom.getName());
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
     * Show student attendance list of the selected item
     * @param dateTime Taken attendance date
     */
    private void showAttendance(String dateTime) {
        Intent intent = new Intent(context, PastAttendanceActivity.class);
        intent.putExtra("classroom", classroom);
        intent.putExtra("dateTime", dateTime);
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.move_in_from_bottom, R.anim.stand_still);
    }

    /**
     * Delete attendance
     * @param attendance Taken attendance
     */
    private void deleteAttendance(final Attendance attendance) {
        //show alert before deleting
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setMessage(attendance.getDateTime()
                + getString(R.string.sureToDelete));
        customAlertDialog.setPositiveButtonText(getString(R.string.delete));
        customAlertDialog.setNegativeButtonText(getString(R.string.cancel));
        customAlertDialog.setOnClickListener(new OnAlertClick() {
            @Override
            public void OnPositive() {
                new DeleteAttendance().execute(attendance.getDateTime());
            }

            @Override
            public void OnNegative() {
                //do nothing
            }
        });
        customAlertDialog.showDialog();
    }

    /**
     * List item click event
     */
    private void addAdapterClickListener() {
        adapter.setAdapterClickListener(new AdapterClickListener() {
            @Override
            public void OnItemClick(int position) {
                if (arrayList != null && arrayList.size() > position) {
                    showAttendance(arrayList.get(position).getDateTime());
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
                    Attendance attendance = arrayList.get(itemPosition);

                    if (menuPosition == PastAttendancePopup.SHOW_ATTENDANCE.getValue()) {
                        showAttendance(attendance.getDateTime());
                    } else if (menuPosition == PastAttendancePopup.DELETE_ATTENDANCE.getValue()) {
                        deleteAttendance(attendance);
                    }
                }
            }
        });
    }

    /**
     * Select from the list of taken attendances
     */
    private class SelectAttendances extends AsyncTask<Void, Void, ArrayList<Attendance>> {

        @Override
        protected ArrayList<Attendance> doInBackground(Void... params) {
            DatabaseManager databaseManager = new DatabaseManager(context);
            ArrayList<Attendance> tmpList = databaseManager.selectAttendanceDates(classroom.getId());

            return tmpList;
        }

        @Override
        protected void onPostExecute(ArrayList<Attendance> tmpList) {
            arrayList.clear();

            if (tmpList != null) {
                arrayList.addAll(tmpList);
                adapter.notifyDataSetChanged();

                setEmptyText();
            }
        }
    }

    /**
     * Delete selected date's attendance item from DB
     */
    private class DeleteAttendance extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String dateTime = params[0];
            DatabaseManager databaseManager = new DatabaseManager(context);
            boolean isSuccessful = databaseManager.deleteAttendance(dateTime, classroom.getId());

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) {
                new SelectAttendances().execute();
            }
        }
    }

    /**
     * Delete all attendances of given class
     */
    private class DeleteAllAttendances extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            DatabaseManager databaseManager = new DatabaseManager(context);
            boolean isSuccessful = databaseManager.deleteAllAttendancesOfClass(classroom.getId());

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) {
                new SelectAttendances().execute();
            }
        }
    }

    /**
     * Ask to delete all
     */
    private void deleteAllAttendances() {
        //try to delete if there are items in the list
        if (!arrayList.isEmpty()) {
            //show alert before deleting
            CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
            customAlertDialog.setMessage(getString(R.string.allToDelete));
            customAlertDialog.setPositiveButtonText(getString(R.string.delete));
            customAlertDialog.setNegativeButtonText(getString(R.string.cancel));
            customAlertDialog.setOnClickListener(new OnAlertClick() {
                @Override
                public void OnPositive() {
                    new DeleteAllAttendances().execute();
                }

                @Override
                public void OnNegative() {
                    //do nothing
                }
            });
            customAlertDialog.showDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Snackbar.make(list, getString(R.string.saved), Snackbar.LENGTH_LONG).show();

            new SelectAttendances().execute();
        }
    }

    private void closeWindow() {
        finish();
        overridePendingTransition(R.anim.stand_still, R.anim.move_out_to_bottom);
    }

    @Override
    public void onBackPressed() {
        closeWindow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                closeWindow();
                return true;
            case R.id.delete:
                deleteAllAttendances();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
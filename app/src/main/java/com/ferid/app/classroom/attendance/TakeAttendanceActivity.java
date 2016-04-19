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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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
import com.ferid.app.classroom.adapters.TakeAttendanceAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.date_time_pickers.CustomDatePickerDialog;
import com.ferid.app.classroom.date_time_pickers.CustomTimePickerDialog;
import com.ferid.app.classroom.date_time_pickers.DatePickerFragment;
import com.ferid.app.classroom.date_time_pickers.TimePickerFragment;
import com.ferid.app.classroom.interfaces.AdapterClickListener;
import com.ferid.app.classroom.interfaces.DateBackListener;
import com.ferid.app.classroom.model.Classroom;
import com.ferid.app.classroom.model.Student;
import com.ferid.app.classroom.past_attendances.PastAttendancesListActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ferid.cafer on 4/16/2015.<br />
 * Takes attendance.
 */
public class TakeAttendanceActivity extends AppCompatActivity implements DateBackListener {
    private Context context;
    private Toolbar toolbar;

    private RecyclerView list;
    private ArrayList<Student> arrayList = new ArrayList<>();
    private TakeAttendanceAdapter adapter;

    private TextView emptyText; //empty list view text

    private Classroom classroom;
    private String classDate = "";

    //date and time pickers
    private DatePickerFragment datePickerFragment;
    private TimePickerFragment timePickerFragment;
    private CustomDatePickerDialog datePickerDialog;
    private CustomTimePickerDialog timePickerDialog;
    private Date changedDate;

    private FloatingActionButton floatingActionButton;


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
        adapter = new TakeAttendanceAdapter(context, arrayList);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setHasFixedSize(true);

        emptyText = (TextView) findViewById(R.id.emptyText);
        emptyText.setText(getString(R.string.emptyMessageSave));

        addAdapterClickListener();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        startButtonAnimation();

        new SelectStudents().execute();
    }

    /**
     * Create toolbar and set its attributes
     */
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Date dateTime = new Date();
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        classDate = targetFormat.format(dateTime);

        if (toolbar != null && classroom != null && classDate != null) {
            setTitle(classroom.getName());
            toolbar.setSubtitle(classDate);
        }
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
     * Set floating action button with its animation
     */
    private void startButtonAnimation() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                floatingActionButton.setImageResource(R.drawable.ic_action_save);
                floatingActionButton.show();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNewAttendance();
            }
        });
    }

    /**
     * List item click event
     */
    private void addAdapterClickListener() {
        adapter.setAdapterClickListener(new AdapterClickListener() {
            @Override
            public void OnItemClick(int position) {
                if (arrayList.size() > position) {
                    Student student = arrayList.get(position);
                    boolean isPresent = !student.isPresent();
                    arrayList.get(position).setPresent(isPresent);

                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Shows date picker
     */
    private void changeDate() {
        if (Build.VERSION.SDK_INT < 21) {
            datePickerDialog = new CustomDatePickerDialog(context);
            datePickerDialog.show();
        } else {
            datePickerFragment = new DatePickerFragment();
            datePickerFragment.show(getSupportFragmentManager(), "DatePickerFragment");
        }
    }

    /**
     * Shows time picker
     */
    private void changeTime() {
        if (Build.VERSION.SDK_INT < 21) {
            timePickerDialog = new CustomTimePickerDialog(context);
            timePickerDialog.show();
        } else {
            timePickerFragment = new TimePickerFragment();
            timePickerFragment.show(getSupportFragmentManager(), "TimePickerFragment");
        }
    }

    /**
     * Makes the change both on variable that will be send to DB and on the toolbar subtitle
     */
    private void changeDateTime() {
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        classDate = targetFormat.format(changedDate);
        toolbar.setSubtitle(classDate);
    }

    @Override
    public void OnPress(int dayOfMonth, int month, int year) {
        changedDate.setYear(year - 1900);
        changedDate.setMonth(month);
        changedDate.setDate(dayOfMonth);

        changeTime();
    }

    @Override
    public void OnPress(int minute, int hour) {
        changedDate.setHours(hour);
        changedDate.setMinutes(minute);

        changeDateTime();
    }

    /**
     * Go to past attendaces of the given classroom
     */
    private void goToPastAttendances() {
        Intent intent = new Intent(context, PastAttendancesListActivity.class);
        intent.putExtra("classroom", classroom);
        startActivity(intent);
        overridePendingTransition(R.anim.move_in_from_bottom, R.anim.stand_still);
    }

    /**
     * Select students from DB
     */
    private class SelectStudents extends AsyncTask<Void, Void, ArrayList<Student>> {

        @Override
        protected ArrayList<Student> doInBackground(Void... params) {
            ArrayList<Student> tmpList = null;
            if (classroom != null) {
                DatabaseManager databaseManager = new DatabaseManager(context);
                tmpList = databaseManager.selectStudents(classroom.getId());
            }

            return tmpList;
        }

        @Override
        protected void onPostExecute(ArrayList<Student> tmpList) {
            arrayList.clear();

            if (tmpList != null) {
                arrayList.addAll(tmpList);
                adapter.notifyDataSetChanged();

                setEmptyText();
            }
        }
    }

    /**
     * Inserts a new attendance after check its existence
     */
    private void insertNewAttendance() {
        new IsAlreadyExist().execute();
    }

    private class IsAlreadyExist extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isExist = false;
            if (classroom != null) {
                DatabaseManager databaseManager = new DatabaseManager(context);
                isExist = databaseManager.selectAttendanceToCheckExistance(classroom.getId(),
                        classDate);
            }

            return isExist;
        }

        @Override
        protected void onPostExecute(Boolean isExist) {
            if (isExist) {
                Snackbar.make(list, getString(R.string.couldNotInsertAttendance),
                        Snackbar.LENGTH_LONG).show();
            } else {
                new InsertAttendance().execute();
            }
        }
    }

    /**
     * Insert attendance name into DB
     */
    private class InsertAttendance extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isSuccessful = false;

            if (arrayList != null) {
                DatabaseManager databaseManager = new DatabaseManager(context);
                isSuccessful = databaseManager.insertAttendance(arrayList, classDate);
            }

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
            }
            closeWindow();
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
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                closeWindow();
                return true;
            case R.id.changeDateTime:
                changedDate = new Date();
                changeDate();
                return true;
            case R.id.pastAttendances:
                goToPastAttendances();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
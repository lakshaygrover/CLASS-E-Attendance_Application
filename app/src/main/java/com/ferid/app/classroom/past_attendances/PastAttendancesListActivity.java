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

package com.ferid.app.classroom.past_attendances;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.PastAttendancesListAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.interfaces.OnClick;
import com.ferid.app.classroom.material_dialog.MaterialDialog;
import com.ferid.app.classroom.model.Attendance;
import com.ferid.app.classroom.model.Classroom;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/16/2015.
 */
public class PastAttendancesListActivity extends AppCompatActivity {
    private Context context;

    private ListView list;
    private ArrayList<Attendance> arrayList;
    private PastAttendancesListAdapter adapter;

    private Classroom classroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_listview_with_toolbar);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            classroom = (Classroom) args.getSerializable("classroom");
        }

        context = this;

        //toolbar
        setToolbar();

        list = (ListView) findViewById(R.id.list);
        arrayList = new ArrayList<Attendance>();
        adapter = new PastAttendancesListAdapter(context, R.layout.simple_text_item_small, arrayList);
        list.setAdapter(adapter);

        //empty list view text
        TextView emptyText = (TextView) findViewById(R.id.emptyText);
        emptyText.setText(getString(R.string.emptyMessageDelete));
        list.setEmptyView(emptyText);

        setListItemClickListener();

        new SelectAttendances().execute();
    }

    /**
     * setOnItemClickListener & setOnItemLongClickListener
     */
    private void setListItemClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    Intent intent = new Intent(context, PastAttendanceActivity.class);
                    intent.putExtra("classroom", classroom);
                    intent.putExtra("dateTime", arrayList.get(position).getDateTime());
                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.move_in_from_bottom, R.anim.stand_still);
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    final Attendance attendance = arrayList.get(position);

                    final MaterialDialog materialDialog = new MaterialDialog(context);
                    materialDialog.setContent(attendance.getDateTime()
                            + getString(R.string.sureToDelete));
                    materialDialog.setPositiveButton(getString(R.string.ok));
                    materialDialog.setNegativeButton(getString(R.string.cancel));
                    materialDialog.setOnClickListener(new OnClick() {
                        @Override
                        public void OnPositive() {
                            materialDialog.dismiss();

                            new DeleteAttendance().execute(attendance.getDateTime());
                        }

                        @Override
                        public void OnNegative() {
                            materialDialog.dismiss();
                        }
                    });
                    materialDialog.show();
                }
                return true;
            }
        });
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
            if (isSuccessful)
                new SelectAttendances().execute();
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
            if (isSuccessful)
                new SelectAttendances().execute();
        }
    }

    /**
     * Ask to delete all
     */
    private void deleteAllAttendances() {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setContent(getString(R.string.allToDelete));
        materialDialog.setPositiveButton(getString(R.string.ok));
        materialDialog.setNegativeButton(getString(R.string.cancel));
        materialDialog.setOnClickListener(new OnClick() {
            @Override
            public void OnPositive() {
                materialDialog.dismiss();

                new DeleteAllAttendances().execute();
            }

            @Override
            public void OnNegative() {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
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

package com.ferid.app.classroom.statistics;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.StatisticsAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.model.Attendance;
import com.ferid.app.classroom.model.Classroom;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/20/2015.
 */
public class StudentsListActivity extends AppCompatActivity {
    private Context context;

    private ListView list;
    private ArrayList<Attendance> arrayList;
    private StatisticsAdapter adapter;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(classroom.getName());
        //---

        list = (ListView) findViewById(R.id.list);
        arrayList = new ArrayList<Attendance>();
        adapter = new StatisticsAdapter(context, R.layout.hash_text_item, arrayList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    Attendance attendance = arrayList.get(position);
                    Intent intent = new Intent(context, GraphActivity.class);
                    intent.putExtra("attendance", attendance);
                    startActivity(intent);
                }
            }
        });

        new SelectAllAttendancesOfClass().execute();
    }

    /**
     * Select students with percentages from DB
     */
    private class SelectAllAttendancesOfClass extends AsyncTask<Void, Void, ArrayList<Attendance>> {

        @Override
        protected ArrayList<Attendance> doInBackground(Void... params) {
            ArrayList<Attendance> tmpList = null;
            if (classroom != null) {
                DatabaseManager databaseManager = new DatabaseManager(context);
                tmpList = databaseManager.selectAllAttendancesOfClass(classroom.getId());
            }

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

    private void closeWindow() {
        finish();
        overridePendingTransition(R.anim.stand_still, R.anim.move_out_to_bottom);
    }

    @Override
    public void onBackPressed() {
        closeWindow();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                closeWindow();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

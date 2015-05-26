package com.ferid.app.classroom.statistics;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.model.Attendance;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/27/2015.
 */
public class GraphActivity extends AppCompatActivity {
    private Context context;

    private ArrayList<Attendance> arrayList;
    private Attendance attendance;

    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_layout);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            attendance = (Attendance) args.getSerializable("attendance");
        }

        context = this;

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(attendance.getClassroomName());
        //---

        arrayList = new ArrayList<Attendance>();

        graph = (GraphView) findViewById(R.id.graph);

        new SelectAllAttendancesOfStudent().execute();
    }

    /**
     * Select students with percentages from DB
     */
    private class SelectAllAttendancesOfStudent extends AsyncTask<Void, Void, ArrayList<Attendance>> {

        @Override
        protected ArrayList<Attendance> doInBackground(Void... params) {
            ArrayList<Attendance> tmpList = null;
            if (attendance != null) {
                DatabaseManager databaseManager = new DatabaseManager(context);
                tmpList = databaseManager.selectAllAttendancesOfStudent(attendance.getClassroomId(),
                        attendance.getStudentId());
            }

            return tmpList;
        }

        @Override
        protected void onPostExecute(ArrayList<Attendance> tmpList) {
            arrayList.clear();

            if (tmpList != null) {
                arrayList.addAll(tmpList);

                calculateAttendanceByWeek();
            }
        }
    }

    /**
     * Calculate presence percentage by week
     */
    private void calculateAttendanceByWeek() {
        ArrayList<Integer> presenceList = new ArrayList<Integer>();
        int numberOfWeeks = arrayList.size();
        int numberOfPresence = 0;

        for (int i = 0; i < numberOfWeeks; i++) {
            Attendance tmpAttendance = arrayList.get(i);

            if (tmpAttendance.getPresent() == 1) {
                numberOfPresence++;
            }

            int percentage = (int) ((double)numberOfPresence * 100 / (i+1));
            presenceList.add(percentage);
        }

        prepareGraphics(presenceList);
    }

    /**
     * Draw graph of weekly attendance
     * @param presenceList
     */
    private void prepareGraphics(ArrayList<Integer> presenceList) {
        DataPoint[] dataPoints = new DataPoint[presenceList.size()+1];
        dataPoints[0] = new DataPoint(0, 0);
        for (int i = 0; i < presenceList.size(); i++) {
            dataPoints[i+1] = new DataPoint((i+1), presenceList.get(i));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        series.setColor(getResources().getColor(R.color.colourAccent));
        series.setThickness(5);
        graph.addSeries(series);

        graph.setTitle(attendance.getStudentName());
        graph.getViewport().setMaxY(100);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxX(presenceList.size());
        graph.getViewport().setXAxisBoundsManual(true);
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

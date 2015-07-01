package com.ferid.app.classroom.statistics;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.StatisticsAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.model.Attendance;
import com.ferid.app.classroom.model.Classroom;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/20/2015.
 */
public class StudentsListActivity extends AppCompatActivity {
    private Context context;

    private ListView list;
    private ArrayList<Attendance> attendanceList;
    private StatisticsAdapter adapter;

    private Classroom classroom;

    //graphics
    private GraphView graph;
    private LinearLayout graphLayout;
    private Attendance attendance;
    private ArrayList<Attendance> graphList;
    private ImageView closeGraphIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            classroom = (Classroom) args.getSerializable("classroom");
        }

        context = this;

        //toolbar
        setToolbar();

        //graph
        graphLayout = (LinearLayout) findViewById(R.id.graphLayout);
        graph = (GraphView) findViewById(R.id.graph);
        closeGraphIcon = (ImageView) findViewById(R.id.closeGraphIcon);
        graphList = new ArrayList<Attendance>();

        //list
        list = (ListView) findViewById(R.id.list);
        attendanceList = new ArrayList<Attendance>();
        adapter = new StatisticsAdapter(context, R.layout.hash_text_item, attendanceList);
        list.setAdapter(adapter);

        //empty list view text
        TextView emptyText = (TextView) findViewById(R.id.emptyText);
        list.setEmptyView(emptyText);

        setCloseGraphIconListener();
        setListItemClickListener();

        new SelectAllAttendancesOfClass().execute();
    }

    /**
     * Create toolbar and set its attributes
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(classroom.getName());
    }

    /**
     * setOnItemClickListener
     */
    private void setListItemClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (attendanceList != null && attendanceList.size() > position) {
                    attendance = attendanceList.get(position);
                    graphList.clear();

                    new SelectAllAttendancesOfStudent().execute();
                }
            }
        });
    }

    /**
     * setOnClickListener
     */
    private void setCloseGraphIconListener() {
        closeGraphIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideGraph();
            }
        });
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
            attendanceList.clear();

            if (tmpList != null) {
                attendanceList.addAll(tmpList);
                adapter.notifyDataSetChanged();
            }
        }
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
            graphList.clear();

            if (tmpList != null) {
                graphList.addAll(tmpList);

                calculateAttendanceByWeek();
            }
        }
    }

    /**
     * Calculate presence percentage by week
     */
    private void calculateAttendanceByWeek() {
        ArrayList<Integer> presenceList = new ArrayList<Integer>();
        int numberOfWeeks = graphList.size();
        int numberOfPresence = 0;

        for (int i = 0; i < numberOfWeeks; i++) {
            Attendance tmpAttendance = graphList.get(i);

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

        graph.removeAllSeries();
        graph.addSeries(series);

        graph.setTitle(attendance.getStudentName());
        graph.getViewport().setMaxY(100);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxX(presenceList.size());
        graph.getViewport().setXAxisBoundsManual(true);

        showGraph();
    }

    /**
     * Show graph layout with its full content
     */
    private void showGraph() {
        if (graphLayout.getVisibility() != View.VISIBLE) {
            Animation animShow = AnimationUtils.loadAnimation(context, R.anim.push_from_bottom);
            graphLayout.startAnimation(animShow);
            graphLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide graph layout
     */
    private void hideGraph() {
        if (graphLayout.getVisibility() == View.VISIBLE) {
            Animation animHide = AnimationUtils.loadAnimation(context,
                    R.anim.push_to_bottom);
            graphLayout.setAnimation(animHide);
            graphLayout.setVisibility(View.GONE);
        }
    }

    private void closeWindow() {
        finish();
        overridePendingTransition(R.anim.stand_still, R.anim.move_out_to_bottom);
    }

    @Override
    public void onBackPressed() {
        //if the graph is open, close it
        if (graphLayout.getVisibility() == View.VISIBLE) {
            hideGraph();
        } else { //otherwise leave the screen
            closeWindow();
        }
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

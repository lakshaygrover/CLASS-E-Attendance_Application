package com.ferid.app.classroom.past_attendances;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.AttendanceAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.date_time_pickers.CustomDatePickerDialog;
import com.ferid.app.classroom.date_time_pickers.CustomTimePickerDialog;
import com.ferid.app.classroom.date_time_pickers.DatePickerFragment;
import com.ferid.app.classroom.date_time_pickers.TimePickerFragment;
import com.ferid.app.classroom.interfaces.BackNavigationListener;
import com.ferid.app.classroom.model.Classroom;
import com.ferid.app.classroom.model.Student;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ferid.cafer on 4/16/2015.
 */
public class PastAttendanceActivity extends AppCompatActivity implements BackNavigationListener {
    private Context context;
    private Toolbar toolbar;

    private ListView list;
    private ArrayList<Student> arrayList;
    private AttendanceAdapter adapter;

    private Classroom classroom;
    private String dateTime = "";

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
        setContentView(R.layout.simple_listview_with_toolbar);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            classroom = (Classroom) args.getSerializable("classroom");
            dateTime = args.getString("dateTime");
        }

        context = this;

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(classroom.getName());
        toolbar.setSubtitle(dateTime);
        //---

        list = (ListView) findViewById(R.id.list);
        arrayList = new ArrayList<Student>();
        adapter = new AttendanceAdapter(context, R.layout.checkable_text_item, arrayList);
        list.setAdapter(adapter);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        startButtonAnimation();

        new SelectAttendingStudents().execute();
    }

    /**
     * Set floating action button with its animation
     */
    private void startButtonAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.SlideInUp).playOn(floatingActionButton);
                floatingActionButton.setIcon(R.drawable.ic_action_save);
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        }, 400);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateAttendance().execute();
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
        dateTime = targetFormat.format(changedDate);
        toolbar.setSubtitle(dateTime);
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
     * Select attending and non-attending students from DB
     */
    private class SelectAttendingStudents extends AsyncTask<Void, Void, ArrayList<Student>> {

        @Override
        protected ArrayList<Student> doInBackground(Void... params) {
            ArrayList<Student> tmpList = null;
            if (classroom != null) {
                DatabaseManager databaseManager = new DatabaseManager(context);
                tmpList = databaseManager.selectAttendingStudents(dateTime, classroom.getId());
            }

            return tmpList;
        }

        @Override
        protected void onPostExecute(ArrayList<Student> tmpList) {
            arrayList.clear();

            if (tmpList != null) {
                arrayList.addAll(tmpList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Update attendance
     */
    private class UpdateAttendance extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            DatabaseManager databaseManager = new DatabaseManager(context);
            int rowsAffected = databaseManager.updateAttendance(arrayList, dateTime);

            return rowsAffected;
        }

        @Override
        protected void onPostExecute(Integer rowsAffected) {
            if (rowsAffected > 0) {
                Toast.makeText(context, getString(R.string.saved), Toast.LENGTH_LONG).show();
                closeWindow();
            }
        }
    }

    private void closeWindow() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        finish();
        overridePendingTransition(R.anim.stand_still, R.anim.move_out_to_bottom);
    }

    @Override
    public void onBackPressed() {
        closeWindow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_past_attendance, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
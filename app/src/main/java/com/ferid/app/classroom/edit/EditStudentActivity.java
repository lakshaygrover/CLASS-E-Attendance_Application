package com.ferid.app.classroom.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.StudentAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.interfaces.OnClick;
import com.ferid.app.classroom.interfaces.OnPrompt;
import com.ferid.app.classroom.material_dialog.MaterialDialog;
import com.ferid.app.classroom.material_dialog.PromptDialog;
import com.ferid.app.classroom.model.Classroom;
import com.ferid.app.classroom.model.Student;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/15/2015.
 */
public class EditStudentActivity extends AppCompatActivity {
    private Context context;

    private ListView list;
    private ArrayList<Student> arrayList;
    private StudentAdapter adapter;

    private Classroom classroom;

    private FloatingActionButton floatingActionButton;


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
        arrayList = new ArrayList<Student>();
        adapter = new StudentAdapter(context, R.layout.simple_text_item_small, arrayList);
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    final Student student = arrayList.get(position);

                    final MaterialDialog materialDialog = new MaterialDialog(context);
                    materialDialog.setContent(student.getName() + getString(R.string.sureToDelete));
                    materialDialog.setPositiveButton(getString(R.string.ok));
                    materialDialog.setNegativeButton(getString(R.string.cancel));
                    materialDialog.setOnClickListener(new OnClick() {
                        @Override
                        public void OnPositive() {
                            materialDialog.dismiss();

                            new DeleteStudent().execute(student.getId());
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

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        startButtonAnimation();

        new SelectStudents().execute();
    }

    /**
     * Set floating action button with its animation
     */
    private void startButtonAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.SlideInUp).playOn(floatingActionButton);
                floatingActionButton.setIcon(R.drawable.ic_action_add);
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        }, 400);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });
    }

    /**
     * Add new student item
     */
    private void addNewItem() {
        final PromptDialog promptDialog = new PromptDialog(context);
        promptDialog.setTitle(getString(R.string.studentName));
        promptDialog.setPositiveButton(getString(R.string.ok));
        promptDialog.setOnPositiveClickListener(new OnPrompt() {
            @Override
            public void OnPrompt(String promptText) {
                promptDialog.dismiss();

                closeKeyboard();

                if (!promptText.toString().equals(""))
                    new InsertStudent().execute(promptText);
            }
        });
        promptDialog.show();
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
            }
        }
    }

    /**
     * Insert student name into DB
     */
    private class InsertStudent extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean isSuccessful = false;

            String student = params[0];
            if (classroom != null) {
                DatabaseManager databaseManager = new DatabaseManager(context);
                isSuccessful = databaseManager.insertStudent(classroom.getId(), student);
            }

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful)
                new SelectStudents().execute();
        }
    }

    /**
     * Delete a student item from DB
     */
    private class DeleteStudent extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            int studentId = params[0];
            DatabaseManager databaseManager = new DatabaseManager(context);
            boolean isSuccessful = databaseManager.deleteStudent(studentId, classroom.getId());

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful)
                new SelectStudents().execute();
        }
    }

    /**
     * Closes keyboard for disabling interruption
     */
    private void closeKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {}
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
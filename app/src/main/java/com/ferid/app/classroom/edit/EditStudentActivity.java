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

package com.ferid.app.classroom.edit;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.EditStudentsAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.enums.StudentPopup;
import com.ferid.app.classroom.listeners.OnAlertClick;
import com.ferid.app.classroom.listeners.PermissionGrantListener;
import com.ferid.app.classroom.listeners.PopupClickListener;
import com.ferid.app.classroom.listeners.PromptListener;
import com.ferid.app.classroom.material_dialog.CustomAlertDialog;
import com.ferid.app.classroom.material_dialog.PromptDialog;
import com.ferid.app.classroom.model.Classroom;
import com.ferid.app.classroom.model.Student;
import com.ferid.app.classroom.utility.DirectoryUtility;
import com.ferid.app.classroom.utility.PermissionProcessor;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ferid.cafer on 4/15/2015.<br />
 * Adds and removes students.
 */
public class EditStudentActivity extends AppCompatActivity {
    private Context context;

    private RecyclerView list;
    private ArrayList<Student> arrayList = new ArrayList<>();
    private EditStudentsAdapter adapter;

    private TextView emptyText; //empty list view text

    private Classroom classroom;

    private FloatingActionButton floatingActionButton;

    private ProgressDialog progressDialog;

    private static final int REQUEST_IMPORT_EXCEL = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editable_list);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            classroom = args.getParcelable("classroom");
        }

        context = this;

        //toolbar
        setToolbar();

        list = (RecyclerView) findViewById(R.id.list);
        adapter = new EditStudentsAdapter(context, arrayList);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setHasFixedSize(true);

        emptyText = (TextView) findViewById(R.id.emptyText);
        emptyText.setText(getString(R.string.emptyMessageStudent));

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        setFloatingButton();

        addPopupClickListener();

        new SelectStudents().execute();
    }

    /**
     * Create toolbar and set its attributes
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        if (classroom != null) {
            setTitle(classroom.getName());
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
     * Add new student item
     */
    private void addNewItem() {
        final PromptDialog promptDialog = new PromptDialog(context);
        promptDialog.setPositiveButton(getString(R.string.ok));
        promptDialog.setOnPositiveClickListener(new PromptListener() {
            @Override
            public void OnPrompt(String promptText) {
                closeKeyboard();

                promptDialog.dismiss();

                if (!TextUtils.isEmpty(promptText)) {
                    new InsertStudent().execute(promptText);
                }
            }
        });
        promptDialog.show();
    }

    /**
     * Change the name of the selected student
     * @param studentId current student to be changed
     * @param content current name of the student
     */
    private void editStudent(final int studentId, final String content) {
        final PromptDialog promptDialog = new PromptDialog(context);
        promptDialog.setContent(content);
        promptDialog.setPositiveButton(getString(R.string.ok));
        promptDialog.setOnPositiveClickListener(new PromptListener() {
            @Override
            public void OnPrompt(String promptText) {
                closeKeyboard();

                promptDialog.dismiss();

                if (!TextUtils.isEmpty(promptText)) {
                    new UpdateStudent().execute(String.valueOf(studentId), promptText);
                }
            }
        });
        promptDialog.show();
    }

    /**
     * Delete student
     * @param student Student
     */
    private void deleteStudent(final Student student) {
        //show alert before deleting
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setMessage(student.getName()
                + getString(R.string.sureToDelete));
        customAlertDialog.setPositiveButtonText(getString(R.string.delete));
        customAlertDialog.setNegativeButtonText(getString(R.string.cancel));
        customAlertDialog.setOnClickListener(new OnAlertClick() {
            @Override
            public void OnPositive() {
                new DeleteStudent().execute(student.getId());
            }

            @Override
            public void OnNegative() {
                //do nothing
            }
        });
        customAlertDialog.showDialog();
    }

    /**
     * Set floating action button with its animation
     */
    private void setFloatingButton() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                floatingActionButton.setImageResource(R.drawable.ic_action_add);
                floatingActionButton.show();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
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
                    Student student = arrayList.get(itemPosition);

                    if (menuPosition == StudentPopup.CHANGE_NAME.getValue()) {
                        editStudent(student.getId(), student.getName());
                    } else if (menuPosition == StudentPopup.DELETE_STUDENT.getValue()) {
                        deleteStudent(student);
                    }
                }
            }
        });
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
            if (isSuccessful) {
                new SelectStudents().execute();
            }
        }
    }

    /**
     * Update student name in the DB
     */
    private class UpdateStudent extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean isSuccessful = false;

            String studentId = params[0];
            String newName = params[1];

            if (classroom != null) {
                DatabaseManager databaseManager = new DatabaseManager(context);
                isSuccessful = databaseManager.updateStudentName(studentId, newName);
            }

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) {
                new SelectStudents().execute();
            }
        }
    }

    /**
     * Insert multiple student names into DB
     */
    private class InsertMultipleStudents extends AsyncTask<ArrayList<String>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<String>... params) {
            boolean isSuccessful = false;

            ArrayList<String> studentsList = params[0];
            DatabaseManager databaseManager = new DatabaseManager(context);

            if (classroom != null) {
                for (String student : studentsList) {
                    isSuccessful = databaseManager.insertStudent(classroom.getId(), student);

                    //if any of them fails stop going further
                    if (!isSuccessful) break;
                }
            }

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (isSuccessful) {
                new SelectStudents().execute();
            } else {
                excelFileError();
            }
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
            if (isSuccessful) {
                new SelectStudents().execute();
            }
        }
    }

    /**
     * Check permission before going to excel sheet.<br />
     * If permission is granted file browser starts to import from excel.
     */
    private void checkPermissionForExcel() {
        PermissionProcessor permissionProcessor = new PermissionProcessor(this, list);
        permissionProcessor.setPermissionGrantListener(new PermissionGrantListener() {
            @Override
            public void OnGranted() {
                browseFiles();
            }
        });
        permissionProcessor.askForPermissionExternalStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == PermissionProcessor.REQUEST_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                browseFiles();
            }
        }
    }

    /**
     * Import students form excel
     * @param fileName Excel file name
     */
    private void readXlsFile(String fileName) {
        ArrayList<String> studentsList = new ArrayList<>();
        progressDialog = ProgressDialog.show(this, getString(R.string.wait),
                getString(R.string.ongoing), true, false);

        try {
            // Creating Input Stream
            File file = new File(fileName);
            FileInputStream fileInputStream = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(fileInputStream);

            // Create a workbook using the File System
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);

            // Get the first sheet from workbook
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);

            // Iterate through the cells
            Iterator rowIter = hssfSheet.rowIterator();

            StringBuilder studentName; //full name

            while (rowIter.hasNext()) {
                studentName = new StringBuilder("");

                HSSFRow hssfRow = (HSSFRow) rowIter.next();
                Iterator cellIter = hssfRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell currentCell = (HSSFCell) cellIter.next();
                    if (!currentCell.toString().trim().equals("")) {
                        //put space in between name, surname, etc.
                        if (studentName.toString().length() > 0) {
                            studentName.append(" ");
                        }
                        studentName.append(currentCell.toString());
                    }
                }

                //add to list
                if (!studentName.toString().equals("")) {
                    studentsList.add(studentName.toString());
                }
            }
        } catch (Exception e) {
            progressDialog.dismiss();

            excelFileError();
        }

        if (!studentsList.isEmpty()) {
            new InsertMultipleStudents().execute(studentsList);
        } else {
            progressDialog.dismiss();
        }
    }

    /**
     * Get the path of the excel file to import students list
     */
    private void browseFiles() {
        //if directory is not mounted do not start the operation
        if (DirectoryUtility.isExternalStorageMounted()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.selectFile)), REQUEST_IMPORT_EXCEL);
            } catch (ActivityNotFoundException e) {
                excelFileError();
            }

        } else {
            Snackbar.make(list, getString(R.string.mountExternalStorage),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMPORT_EXCEL) {
            if (resultCode == RESULT_OK) {
                String filePath = data.getData().getPath();

                //only xls and xlsx extensions are allowed
                if (!filePath.endsWith("xls")) {
                    Snackbar.make(list, getString(R.string.extensionWarningXls),
                            Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (filePath.contains(":")) {
                    String[] partFilePath = filePath.split(":");
                    if (partFilePath.length == 2) {
                        readXlsFile(Environment.getExternalStorageDirectory()
                                + "/" + partFilePath[1]);
                    } else {
                        excelFileError();
                    }
                } else {
                    readXlsFile(filePath);
                }
            }
        }
    }

    /**
     * Excel related error
     */
    private void excelFileError() {
        Snackbar.make(list, getString(R.string.excelError),
                Snackbar.LENGTH_LONG).show();
    }

    /**
     * Closes keyboard for disabling interruption
     */
    private void closeKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {}
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
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                closeWindow();
                return true;
            case R.id.upload:
                checkPermissionForExcel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
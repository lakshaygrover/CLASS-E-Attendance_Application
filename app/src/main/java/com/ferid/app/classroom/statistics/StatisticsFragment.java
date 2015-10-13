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

package com.ferid.app.classroom.statistics;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.ClassroomAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.model.Attendance;
import com.ferid.app.classroom.model.Classroom;
import com.ferid.app.classroom.utility.DirectoryUtility;
import com.ferid.app.classroom.utility.ExcelStyleManager;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ferid.cafer on 4/20/2015.<br />
 * Prepares statistical data and excel file.
 */
public class StatisticsFragment extends Fragment {
    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView list;
    private ArrayList<Classroom> classroomArrayList;
    private ClassroomAdapter classroomAdapter;

    //excel
    private final String FILE_NAME = "attendances.xls";
    private ArrayList<Attendance> attendanceArrayList = new ArrayList<Attendance>();


    public StatisticsFragment() {}

    public static StatisticsFragment newInstance() {
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        return statisticsFragment;
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
        classroomArrayList = new ArrayList<Classroom>();
        classroomAdapter = new ClassroomAdapter(context, R.layout.simple_text_item_big, classroomArrayList);
        list.setAdapter(classroomAdapter);

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
                if (classroomArrayList != null && classroomArrayList.size() > position) {
                    Intent intent = new Intent(context, StudentsListActivity.class);
                    intent.putExtra("classroom", classroomArrayList.get(position));
                    startActivity(intent);
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

            classroomArrayList.clear();

            if (tmpList != null) {
                classroomArrayList.addAll(tmpList);
                classroomAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Select all for excel
     */
    private class SelectForExcel extends AsyncTask<Void, Void, ArrayList<Attendance>> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected ArrayList<Attendance> doInBackground(Void... params) {
            DatabaseManager databaseManager = new DatabaseManager(context);
            ArrayList<Attendance> tmpList = databaseManager.selectAllAttendances();

            return tmpList;
        }

        @Override
        protected void onPostExecute(ArrayList<Attendance> tmpList) {
            attendanceArrayList.clear();

            if (tmpList != null && !tmpList.isEmpty()) {
                attendanceArrayList.addAll(tmpList);

                convertToExcel();
            } else {
                swipeRefreshLayout.setRefreshing(false);

                excelFileError(getString(R.string.takeAttendanceBeforeExcel));
            }
        }
    }

    public void getDataForExcel() {
        new SelectForExcel().execute();
    }

    /**
     * Converts all attendances into excel format
     */
    private void convertToExcel() {
        int length = classroomArrayList.size();

        HSSFWorkbook wb = new HSSFWorkbook();
        ExcelStyleManager excelStyleManager = new ExcelStyleManager();

        for (int i = 0; i < length; i++) {
            Classroom classroom = classroomArrayList.get(i);

            HSSFSheet sheet = wb.createSheet(classroom.getName());

            //header
            HashMap<String, Integer> date_column_map = new HashMap<String, Integer>();
            ArrayList<String> dates = new ArrayList<String>();
            int rowNumber = 0;
            int colNumber = 1;
            HSSFRow row = sheet.createRow(rowNumber);

            for (int j = 0; j < attendanceArrayList.size(); j++) {
                Attendance attendance = attendanceArrayList.get(j);

                if (classroom.getId() == attendance.getClassroomId()
                        && !dates.contains(attendance.getDateTime())) {

                    HSSFCell cellDate = row.createCell(colNumber);
                    cellDate.setCellStyle(excelStyleManager.getHeaderCellStyle(wb));

                    cellDate.setCellValue(attendance.getDateTime());

                    dates.add(attendance.getDateTime());
                    date_column_map.put(attendance.getDateTime(), colNumber);

                    colNumber++;
                }
            }

            //students list at the left column
            HashMap<Integer, Integer> student_row_map = new HashMap<Integer, Integer>();
            ArrayList<Integer> studentIds = new ArrayList<Integer>();
            rowNumber = 1;
            for (int j = 0; j < attendanceArrayList.size(); j++) {
                Attendance attendance = attendanceArrayList.get(j);

                if (classroom.getId() == attendance.getClassroomId()) {
                    if (!studentIds.contains(attendance.getStudentId())) { //another student
                        row = sheet.createRow(rowNumber);

                        HSSFCell cellStudent = row.createCell(0);
                        cellStudent.setCellStyle(excelStyleManager.getHeaderCellStyle(wb));

                        cellStudent.setCellValue(attendance.getStudentName());

                        studentIds.add(attendance.getStudentId());
                        student_row_map.put(attendance.getStudentId(), rowNumber);

                        rowNumber++;
                    }
                }
            }

            //now get column number from date columns
            //and get row number from student rows
            //match row-column pair and print into cell
            for (int j = 0; j < attendanceArrayList.size(); j++) {
                Attendance attendance = attendanceArrayList.get(j);

                if (classroom.getId() == attendance.getClassroomId()) {
                    rowNumber = student_row_map.get(attendance.getStudentId());
                    colNumber = date_column_map.get(attendance.getDateTime());

                    row = sheet.getRow(rowNumber);

                    HSSFCell cellPresence = row.createCell(colNumber);
                    cellPresence.setCellStyle(excelStyleManager.getContentCellStyle(wb));

                    cellPresence.setCellValue(attendance.getPresent());
                }
            }
        }

        if (length > 0) writeIntoFile(wb);

        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Write into an excel file
     * @param wb
     */
    private void writeIntoFile(HSSFWorkbook wb) {
        boolean isFileOperationSuccessful = true;

        FileOutputStream fileOut = null;

        DirectoryUtility.createDirectory();

        try {
            fileOut = new FileOutputStream(DirectoryUtility.getPathFolder() + FILE_NAME);
            wb.write(fileOut);
        } catch (IOException e) {
            isFileOperationSuccessful = false;
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.flush();
                    fileOut.close();
                } catch (IOException e) {
                    isFileOperationSuccessful = false;
                }
            }
        }

        //if file is successfully created and closed
        //show list dialog, otherwise display error
        if (isFileOperationSuccessful) {
            showDialogForAction();
        } else {
            excelFileError(getString(R.string.excelError));
        }
    }

    /**
     * What to do, open file or send mail
     */
    private void showDialogForAction() {
        //list dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.whatToDoWithExcel)
                .setItems(R.array.excel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                openExcelFile();
                                break;
                            case 1:
                                sendExcelByMail();
                                break;
                        }
                    }
                });
        //create and show
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Send excel file by mail
     */
    private void sendExcelByMail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        Uri attachment = Uri.parse("file:///" + DirectoryUtility.getPathFolder() + FILE_NAME);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.takeAttendance));
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        startActivityForExcel(intent);
    }

    /**
     * Open and show the created excel file
     */
    private void openExcelFile() {
        File file = new File(DirectoryUtility.getPathFolder() + FILE_NAME);

        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForExcel(intent);
        } else {
            excelFileError(getString(R.string.excelError));
        }
    }

    /**
     * Start activity either to open or to send mail the excel file
     * @param intent Intent
     */
    private void startActivityForExcel(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            excelFileError(getString(R.string.excelError));
        }
    }

    /**
     * Excel related error
     * @param errorMessage String
     */
    private void excelFileError(String errorMessage) {
        Snackbar.make(list, errorMessage,
                Snackbar.LENGTH_LONG).show();
    }
}
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

package com.ferid.app.classroom.statistics;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.adapters.OperateClassroomsAdapter;
import com.ferid.app.classroom.database.DatabaseManager;
import com.ferid.app.classroom.interfaces.AdapterClickListener;
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
    private RecyclerView list;
    private ArrayList<Classroom> classroomArrayList = new ArrayList<>();
    private OperateClassroomsAdapter classroomAdapter;

    private TextView emptyText; //empty list view text

    //excel
    private final String FILE_NAME = "attendances.xls";
    private ArrayList<Attendance> attendanceArrayList = new ArrayList<>();


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

        View rootView = inflater.inflate(R.layout.refreshable_list, container, false);

        context = rootView.getContext();

        list = (RecyclerView) rootView.findViewById(R.id.list);
        classroomAdapter = new OperateClassroomsAdapter(classroomArrayList);
        list.setAdapter(classroomAdapter);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setHasFixedSize(true);

        emptyText = (TextView) rootView.findViewById(R.id.emptyText);

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

        addAdapterClickListener();

        new SelectClassrooms().execute();


        return rootView;
    }

    /**
     * Set empty list text
     */
    private void setEmptyText() {
        if (emptyText != null) {
            if (classroomArrayList.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
            } else {
                emptyText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * List item click event
     */
    private void addAdapterClickListener() {
        classroomAdapter.setAdapterClickListener(new AdapterClickListener() {
            @Override
            public void OnItemClick(int position) {
                if (classroomArrayList != null && classroomArrayList.size() > position) {
                    Intent intent = new Intent(context, StatisticalListActivity.class);
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

                setEmptyText();
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

        for (int i = 0; i < length; i++) { //each sheet
            Classroom classroom = classroomArrayList.get(i);

            HSSFSheet sheet = wb.createSheet(classroom.getName());

            //header
            HashMap<String, Integer> date_column_map = new HashMap<>();
            ArrayList<String> dates = new ArrayList<>();
            int rowNumber = 0;
            int colNumber = 1;
            HSSFRow row = sheet.createRow(rowNumber);

            //dates columns
            for (int j = 0; j < attendanceArrayList.size(); j++) {
                Attendance attendance = attendanceArrayList.get(j);

                if (classroom.getId() == attendance.getClassroomId()
                        && !dates.contains(attendance.getDateTime())) {

                    HSSFCell cellDate = row.createCell(colNumber);
                    cellDate.setCellStyle(excelStyleManager.getHeaderCellStyle(wb));

                    cellDate.setCellValue(attendance.getDateTime());

                    dates.add(attendance.getDateTime());
                    date_column_map.put(attendance.getDateTime(), colNumber);

                    //set width of the dates columns
                    sheet.setColumnWidth(colNumber, getResources()
                            .getInteger(R.integer.statistics_excel_column_width_dates));

                    colNumber++;
                }
            }

            //set width of the students column
            //it is always the first column
            sheet.setColumnWidth(0, getResources()
                    .getInteger(R.integer.statistics_excel_column_width_students));

            //students list at the left column
            HashMap<Integer, Integer> student_row_map = new HashMap<>();
            ArrayList<Integer> studentIds = new ArrayList<>();
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

        if (DirectoryUtility.isExternalStorageMounted()) {

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
            //open file, otherwise display error
            if (isFileOperationSuccessful) {
                openExcelFile();
            } else {
                excelFileError(getString(R.string.excelError));
            }

        } else { //external storage is not available
            excelFileError(getString(R.string.mountExternalStorage));
        }
    }

    /**
     * Open and show the created excel file
     */
    private void openExcelFile() {
        if (DirectoryUtility.isExternalStorageMounted()) {

            File file = new File(DirectoryUtility.getPathFolder() + FILE_NAME);

            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                excelFileError(getString(R.string.excelError));
            }
        } else {
            excelFileError(getString(R.string.mountExternalStorage));
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
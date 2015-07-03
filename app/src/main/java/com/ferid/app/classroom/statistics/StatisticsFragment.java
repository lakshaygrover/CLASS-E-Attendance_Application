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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ferid.cafer on 4/20/2015.
 */
public class StatisticsFragment extends Fragment {
    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView list;
    private ArrayList<Classroom> arrayList;
    private ClassroomAdapter adapter;

    //excel
    private String folder = Environment.getExternalStorageDirectory() + "/attendance_taker/";
    private String file = "attendances.xls";
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
        arrayList = new ArrayList<Classroom>();
        adapter = new ClassroomAdapter(context, R.layout.simple_text_item_big, arrayList);
        list.setAdapter(adapter);

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

        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * setOnItemClickListener
     */
    private void setListItemClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    Intent intent = new Intent(context, StudentsListActivity.class);
                    intent.putExtra("classroom", arrayList.get(position));
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

            arrayList.clear();

            if (tmpList != null) {
                arrayList.addAll(tmpList);
                adapter.notifyDataSetChanged();
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

            if (tmpList != null) {
                attendanceArrayList.addAll(tmpList);

                convertToExcel();
            }
        }
    }

    private void getDataForExcel() {
        new SelectForExcel().execute();
    }

    /**
     * Header cell style
     * @return
     */
    private HSSFCellStyle getHeaderCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();

        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 8);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);

        cellStyle.setWrapText(true);

        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        return cellStyle;
    }

    /**
     * Left column cell style
     * @return
     */
    private HSSFCellStyle getLeftColumnCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();

        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 8);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);

        cellStyle.setWrapText(true);

        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        return cellStyle;
    }

    /**
     * Content cell style
     * @return
     */
    private HSSFCellStyle getContentCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();

        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 8);
        cellStyle.setFont(font);

        cellStyle.setWrapText(true);

        cellStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        return cellStyle;
    }

    /**
     * Converts all attendances into excel format
     */
    private void convertToExcel() {
        int length = arrayList.size();

        HSSFWorkbook wb = new HSSFWorkbook();
        for (int i = 0; i < length; i++) {
            Classroom classroom = arrayList.get(i);

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
                    cellDate.setCellStyle(getHeaderCellStyle(wb));

                    cellDate.setCellValue(attendance.getDateTime());

                    dates.add(attendance.getDateTime());
                    date_column_map.put(attendance.getDateTime(), colNumber);

                    colNumber++;
                }
            }

            //content
            HashMap<Integer, Integer> student_row_map = new HashMap<Integer, Integer>();
            ArrayList<Integer> studentIds = new ArrayList<Integer>();
            rowNumber = 1;
            for (int j = 0; j < attendanceArrayList.size(); j++) {
                Attendance attendance = attendanceArrayList.get(j);

                if (classroom.getId() == attendance.getClassroomId()) {
                    if (!studentIds.contains(attendance.getStudentId())) { //another student
                        row = sheet.createRow(rowNumber);

                        HSSFCell cellStudent = row.createCell(0);
                        cellStudent.setCellStyle(getLeftColumnCellStyle(wb));

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
                    cellPresence.setCellStyle(getContentCellStyle(wb));

                    cellPresence.setCellValue(attendance.getPresent());
                }
            }
        }

        if (length > 0)
            writeIntoFile(wb);

        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Write into an excel file
     * @param wb
     */
    private void writeIntoFile(HSSFWorkbook wb) {
        FileOutputStream fileOut = null;
        try {
            // Output stream
            // create a File object for the parent directory
            File directory = new File(folder);
            // have the object build the directory structure, if needed.
            directory.mkdirs();

            fileOut = new FileOutputStream(folder + file);
            wb.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.flush();
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        sendMail();
    }

    /**
     * Share info through social media
     */
    private void sendMail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        Uri attachment = Uri.parse("file:///" + folder + file);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.takeAttendance));
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_publish, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.publish:
                getDataForExcel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
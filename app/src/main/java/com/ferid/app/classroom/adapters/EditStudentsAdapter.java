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

package com.ferid.app.classroom.adapters;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.listeners.PopupClickListener;
import com.ferid.app.classroom.model.Student;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/6/2016.
 */
public class EditStudentsAdapter extends RecyclerView.Adapter<EditStudentsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Student> studentList;

    private ListPopupWindow listPopupWindow;
    private PopupClickListener popupClickListener;

    public EditStudentsAdapter(Context context, ArrayList<Student> studentList) {
        this.context = context;
        this.studentList = studentList;

        listPopupWindow = new ListPopupWindow(context);
    }

    /**
     * Set on pop-up men item click listener
     * @param popupClickListener PopupClickListener
     */
    public void setPopupClickListener(PopupClickListener popupClickListener) {
        this.popupClickListener = popupClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_student_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Student item = studentList.get(position);

        viewHolder.text.setText(item.getName());
        viewHolder.counter.setText(String.valueOf(position + 1));
        viewHolder.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPopupWindow != null) {
                    setListPopUpWindow(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList == null ? 0 : studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView counter;
        ImageButton settings;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            counter = (TextView) itemView.findViewById(R.id.counter);
            settings = (ImageButton) itemView.findViewById(R.id.settings);
        }
    }

    /**
     * List pop up menu window
     * @param anchor View
     * @param studentPosition List item's position
     */
    private void setListPopUpWindow(View anchor, final int studentPosition) {
        listPopupWindow.dismiss();

        listPopupWindow.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1,
                context.getResources().getStringArray(R.array.edit_student)));
        listPopupWindow.setAnchorView(anchor);
        listPopupWindow.setContentWidth(context.getResources()
                .getInteger(R.integer.list_pop_up_width));
        listPopupWindow.setDropDownGravity(Gravity.RIGHT);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int menuItemPosition, long id) {
                if (popupClickListener != null) {
                    popupClickListener.OnPopupClick(studentPosition, menuItemPosition);
                }

                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.show();
    }

}
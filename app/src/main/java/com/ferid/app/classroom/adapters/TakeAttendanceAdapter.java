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
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.interfaces.AdapterClickListener;
import com.ferid.app.classroom.model.Student;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/6/2016.
 */
public class TakeAttendanceAdapter extends RecyclerView.Adapter<TakeAttendanceAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Student> studentList;

    private AdapterClickListener adapterClickListener;

    public TakeAttendanceAdapter(Context context, ArrayList<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    /**
     * Set on item click listener
     * @param adapterClickListener AdapterClickListener
     */
    public void setAdapterClickListener(AdapterClickListener adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkable_text_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Student item = studentList.get(position);

        viewHolder.text.setText(item.getName());
        viewHolder.checkBox.setChecked(item.isPresent());
    }

    @Override
    public int getItemCount() {
        return studentList == null ? 0 : studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        AppCompatCheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            text = (TextView) itemView.findViewById(R.id.text);
            checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.checkBox);
        }

        @Override
        public void onClick(View v) {
            if (adapterClickListener != null) {
                adapterClickListener.OnItemClick(getAdapterPosition());
            }
        }
    }

}
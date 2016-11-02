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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.interfaces.AdapterClickListener;
import com.ferid.app.classroom.model.Attendance;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/7/2016.
 */
public class StatisticalAdapter extends RecyclerView.Adapter<StatisticalAdapter.ViewHolder> {
    private ArrayList<Attendance> attendanceList;

    private AdapterClickListener adapterClickListener;

    public StatisticalAdapter(ArrayList<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
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
                .inflate(R.layout.hash_text_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Attendance item = attendanceList.get(position);

        viewHolder.key.setText(item.getStudentName());
        viewHolder.valuePercentage.setText(item.getPresencePercentage() + "%");
        viewHolder.valueNumeric.setText(item.getAttendedClasses()
                + "/" + item.getAvailableClasses());
    }

    @Override
    public int getItemCount() {
        return attendanceList == null ? 0 : attendanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView key;
        TextView valuePercentage;
        TextView valueNumeric;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            key = (TextView) itemView.findViewById(R.id.key);
            valuePercentage = (TextView) itemView.findViewById(R.id.valuePercentage);
            valueNumeric = (TextView) itemView.findViewById(R.id.valueNumeric);
        }

        @Override
        public void onClick(View v) {
            if (adapterClickListener != null) {
                adapterClickListener.OnItemClick(getAdapterPosition());
            }
        }
    }

}
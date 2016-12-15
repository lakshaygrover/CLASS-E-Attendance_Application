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
import android.widget.ImageButton;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.listeners.AdapterClickListener;
import com.ferid.app.classroom.listeners.ItemDeleteListener;
import com.ferid.app.classroom.model.Attendance;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/6/2016.
 */
public class PastAttendancesAdapter extends RecyclerView.Adapter<PastAttendancesAdapter.ViewHolder> {
    private ArrayList<Attendance> attendanceList;

    private AdapterClickListener adapterClickListener;
    private ItemDeleteListener itemDeleteListener;

    public PastAttendancesAdapter(ArrayList<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    /**
     * Set on item click listener
     * @param adapterClickListener AdapterClickListener
     */
    public void setAdapterClickListener(AdapterClickListener adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
    }

    /**
     * Set on item delete listener
     * @param itemDeleteListener ItemDeleteListener
     */
    public void setItemDeleteListener(ItemDeleteListener itemDeleteListener) {
        this.itemDeleteListener = itemDeleteListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.past_attendance_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Attendance item = attendanceList.get(position);

        viewHolder.counter.setText(String.valueOf(position + 1));
        viewHolder.text.setText(item.getDateTime());
        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemDeleteListener != null) {
                    itemDeleteListener.OnItemDelete(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendanceList == null ? 0 : attendanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        TextView counter;
        ImageButton deleteItem;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            text = (TextView) itemView.findViewById(R.id.text);
            counter = (TextView) itemView.findViewById(R.id.counter);
            deleteItem = (ImageButton) itemView.findViewById(R.id.deleteItem);
        }

        @Override
        public void onClick(View v) {
            if (adapterClickListener != null) {
                adapterClickListener.OnItemClick(getAdapterPosition());
            }
        }
    }

}
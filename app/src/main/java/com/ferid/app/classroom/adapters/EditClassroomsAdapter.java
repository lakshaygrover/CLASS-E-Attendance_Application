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
import com.ferid.app.classroom.interfaces.AdapterClickListener;
import com.ferid.app.classroom.interfaces.PopupClickListener;
import com.ferid.app.classroom.model.Classroom;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/6/2016.
 */
public class EditClassroomsAdapter extends RecyclerView.Adapter<EditClassroomsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Classroom> classroomList;

    private ListPopupWindow listPopupWindow;
    private PopupClickListener popupClickListener;
    private AdapterClickListener adapterClickListener;

    public EditClassroomsAdapter(Context context, ArrayList<Classroom> classroomList) {
        this.context = context;
        this.classroomList = classroomList;

        listPopupWindow = new ListPopupWindow(context);
    }

    /**
     * Set on item click listener
     * @param adapterClickListener AdapterClickListener
     */
    public void setAdapterClickListener(AdapterClickListener adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
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
                .inflate(R.layout.editable_item_big, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Classroom item = classroomList.get(position);

        viewHolder.text.setText(item.getName());
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
        return classroomList == null ? 0 : classroomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        ImageButton settings;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            text = (TextView) itemView.findViewById(R.id.text);
            settings = (ImageButton) itemView.findViewById(R.id.settings);
        }

        @Override
        public void onClick(View v) {
            if (adapterClickListener != null) {
                adapterClickListener.OnItemClick(getAdapterPosition());
            }
        }
    }

    /**
     * List pop up menu window
     * @param anchor View
     * @param classroomPosition List item's position
     */
    private void setListPopUpWindow(View anchor, final int classroomPosition) {
        listPopupWindow.dismiss();

        listPopupWindow.setAdapter(new ArrayAdapter(context, android.R.layout.simple_list_item_1,
                context.getResources().getStringArray(R.array.edit_classroom)));
        listPopupWindow.setAnchorView(anchor);
        listPopupWindow.setContentWidth(300);
        listPopupWindow.setDropDownGravity(Gravity.LEFT);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int menuItemPosition, long id) {
                if (popupClickListener != null) {
                    popupClickListener.OnPopupClick(classroomPosition, menuItemPosition);
                }

                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.show();
    }

}
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

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.model.Student;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/15/2015.
 */
public class StudentAdapter extends ArrayAdapter<Student> {
    private Context context;
    private int layoutResId;
    private ArrayList<Student> items;

    public StudentAdapter(Context context, int layoutResId, ArrayList<Student> objects) {
        super(context, layoutResId, objects);
        this.items = objects;
        this.context = context;
        this.layoutResId = layoutResId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // return your progress view goes here. Ensure that it has the ID
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.counter = (TextView) convertView.findViewById(R.id.counter);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Student item = items.get(position);

        viewHolder.counter.setText(String.valueOf(position+1));
        viewHolder.text.setText(item.getName());

        return convertView;
    }

    private static class ViewHolder {
        TextView counter;
        TextView text;
    }
}

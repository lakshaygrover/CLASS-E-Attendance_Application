package com.ferid.app.classroom.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.model.Classroom;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/15/2015.
 */
public class ClassroomAdapter extends ArrayAdapter<Classroom> {
    private Context context;
    private int layoutResId;
    private ArrayList<Classroom> items;

    public ClassroomAdapter(Context context, int layoutResId, ArrayList<Classroom> objects) {
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

            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.counter = (TextView) convertView.findViewById(R.id.counter);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Classroom item = items.get(position);

        viewHolder.text.setText(item.getName());
        if (item.getStudentNumber() > 0) {
            viewHolder.counter.setText(String.valueOf(item.getStudentNumber()));
        } else {
            viewHolder.counter.setText(context.getString(R.string.plus));
        }

        return convertView;
    }

    public class ViewHolder {
        TextView text;
        TextView counter;
    }
}

package com.ferid.app.classroom.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.model.Student;

import java.util.ArrayList;

/**
 * Created by ferid.cafer on 4/16/2015.
 */
public class AttendanceAdapter extends ArrayAdapter<Student> {
    private Context context;
    private int layoutResId;
    private ArrayList<Student> items;

    public AttendanceAdapter(Context context, int layoutResId, ArrayList<Student> objects) {
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
            viewHolder.checkBox = (ImageView) convertView.findViewById(R.id.checkBox);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Student item = items.get(position);

        viewHolder.text.setText(item.getName());
        if (item.isPresent()) {
            viewHolder.checkBox.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_015);
            viewHolder.checkBox.setColorFilter(context.getResources().getColor(R.color.colourAccent));
        } else {
            viewHolder.checkBox.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_000);
            viewHolder.checkBox.setColorFilter(context.getResources().getColor(R.color.gray));
        }

        return convertView;
    }

    public class ViewHolder {
        TextView text;
        ImageView checkBox;
    }
}

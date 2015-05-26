package com.ferid.app.classroom.date_time_pickers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.interfaces.BackNavigationListener;

/**
 * Created by ferid.cafer on 3/16/2015.
 */
public class CustomTimePickerDialog extends Dialog {
    private Context context;
    private TimePicker timePicker;
    private Button buttonTamam;

    private BackNavigationListener backNavigationListener;

    public CustomTimePickerDialog(Context context__) {
        super(context__);
        setContentView(R.layout.time_picker);

        context = context__;

        backNavigationListener = (BackNavigationListener) context;

        timePicker = (TimePicker) findViewById(R.id.timePicker);

        buttonTamam = (Button) findViewById(R.id.buttonTamam);
        buttonTamam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minute = timePicker.getCurrentMinute();
                int hour = timePicker.getCurrentHour();

                if (backNavigationListener != null)
                    backNavigationListener.OnPress(minute, hour);

                dismiss();
            }
        });
    }
}

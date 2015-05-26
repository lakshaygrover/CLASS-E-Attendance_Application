package com.ferid.app.classroom.date_time_pickers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.interfaces.BackNavigationListener;


/**
 * Created by ferid.cafer on 3/10/2015.
 */
public class CustomDatePickerDialog extends Dialog {
    private Context context;
    private DatePicker datePicker;
    private Button buttonTamam;

    private BackNavigationListener backNavigationListener;

    public CustomDatePickerDialog(Context context__) {
        super(context__);
        setContentView(R.layout.date_picker);

        context = context__;

        backNavigationListener = (BackNavigationListener) context;

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.setCalendarViewShown(false);

        buttonTamam = (Button) findViewById(R.id.buttonTamam);
        buttonTamam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dayOfMonth = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                if (backNavigationListener != null)
                    backNavigationListener.OnPress(dayOfMonth, month, year);

                dismiss();
            }
        });
    }
}

package com.ferid.app.classroom.date_time_pickers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.ferid.app.classroom.interfaces.BackNavigationListener;

import java.util.Calendar;

/**
 * Created by ferid.cafer on 5/7/2015.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private BackNavigationListener backNavigationListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        backNavigationListener = (BackNavigationListener) getActivity();

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (backNavigationListener != null)
            backNavigationListener.OnPress(minute, hourOfDay);

        dismiss();
    }
}

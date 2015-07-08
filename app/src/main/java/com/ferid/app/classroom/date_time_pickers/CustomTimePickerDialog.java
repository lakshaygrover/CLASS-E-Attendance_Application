/*
 * Copyright (C) 2015 Ferid Cafer
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
    private Button buttonOk;

    private BackNavigationListener backNavigationListener;

    public CustomTimePickerDialog(Context context__) {
        super(context__);
        setContentView(R.layout.time_picker);

        context = context__;

        backNavigationListener = (BackNavigationListener) context;

        timePicker = (TimePicker) findViewById(R.id.timePicker);

        buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
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

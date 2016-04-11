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

package com.ferid.app.classroom.material_dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.interfaces.OnAlertClick;

/**
 * Created by ferid.cafer on 7/7/2015.
 */
public class CustomAlertDialog {
    private Context context;

    private String message = "";
    private String positiveButtonText = "";
    private String negativeButtonText = "";

    private OnAlertClick onAlertClick;

    /**
     * Set context
     * @param context__ Context
     */
    public CustomAlertDialog(Context context__) {
        context = context__;
    }

    /**
     * Set message
     * @param message__ String
     */
    public void setMessage(String message__) {
        message = message__;
    }

    /**
     * Set positive button's text
     * @param positiveButtonText__ String
     */
    public void setPositiveButtonText(String positiveButtonText__) {
        positiveButtonText = positiveButtonText__;
    }

    /**
     * Set negative button's text
     * @param negativeButtonText__ String
     */
    public void setNegativeButtonText(String negativeButtonText__) {
        negativeButtonText = negativeButtonText__;
    }

    /**
     * Set on click listener (positive and negative click)
     * @param onAlertClick
     */
    public void setOnClickListener(OnAlertClick onAlertClick) {
        this.onAlertClick = onAlertClick;
    }

    /**
     * Show relevant dialog
     */
    public void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //message
        builder.setMessage(message);
        //positive button
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onAlertClick != null) onAlertClick.OnPositive();
            }
        });
        //negative button
        if (negativeButtonText != null && !negativeButtonText.equals("")) {
            builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (onAlertClick != null) onAlertClick.OnNegative();
                }
            });
        }
        //create and show
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(context.getResources().getColor(R.color.dialogColour));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(context.getResources().getColor(R.color.dialogColour));
            }
        });
        alertDialog.show();
    }
}

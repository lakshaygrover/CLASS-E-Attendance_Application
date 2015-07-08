package com.ferid.app.classroom.material_dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import com.ferid.app.classroom.interfaces.OnClick;

/**
 * Created by ferid.cafer on 7/7/2015.
 */
public class CustomAlertDialog {
    private Context context;

    private String message = "";
    private String positiveButtonText = "";
    private String negativeButtonText = "";

    private OnClick onClick;

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
     * @param onClick
     */
    public void setOnClickListener(OnClick onClick) {
        this.onClick = onClick;
    }

    /**
     * Show relevant dialog
     */
    public void showDialog() {
        if (Build.VERSION.SDK_INT < 21) {
            final MaterialDialog materialDialog = new MaterialDialog(context);
            materialDialog.setContent(message);
            materialDialog.setPositiveButton(positiveButtonText);
            if (negativeButtonText != null && !negativeButtonText.equals("")) {
                materialDialog.setNegativeButton(negativeButtonText);
            }
            materialDialog.setOnClickListener(new OnClick() {
                @Override
                public void OnPositive() {
                    if (onClick != null) onClick.OnPositive();
                }

                @Override
                public void OnNegative() {
                    if (onClick != null) onClick.OnNegative();
                }
            });
            materialDialog.show();
        } else {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage(message);
            alertDialog.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (onClick != null) onClick.OnPositive();
                }
            });
            if (negativeButtonText != null && !negativeButtonText.equals("")) {
                alertDialog.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClick != null) onClick.OnNegative();
                    }
                });
            }
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
    }
}

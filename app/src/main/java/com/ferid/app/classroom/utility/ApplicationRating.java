package com.ferid.app.classroom.utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.interfaces.OnClick;
import com.ferid.app.classroom.material_dialog.MaterialDialog;

/**
 * Created by ferid.cafer on 5/14/2015.<br />
 * It asks user to rate, after checking the usage frequency
 */
public class ApplicationRating {
    private static SharedPreferences prefs;

    private static int beforeRatingViewingNumber = 0; //0 beginning, -1 never show
    private static final int frequency = 20; //usage frequency
    //after ... many times of usage the use will be prompted to rate the application

    /**
     * Manages when to prompt user
     * @param context
     */
    public static void ratingPopupManager(Context context) {
        //read from preferences
        readFromDisk(context);
        //if it is not rated yet
        if (beforeRatingViewingNumber > 0) {
            //check frequency
            if (beforeRatingViewingNumber % frequency == 0) {
                //prompt user
                showPopup(context);
            } else {
                saveOnDisk(context);
            }
        }
    }

    /**
     * Reads from shared preferences
     * @param context
     */
    private static void readFromDisk(Context context) {
        try {
            prefs = context.getSharedPreferences(context.getString(R.string.sharedPreferences), 0);
            beforeRatingViewingNumber = prefs.getInt(context
                    .getString(R.string.pref_beforeRatingViewingNumber), 0);
            if (beforeRatingViewingNumber >= 0) //if smaller than 0, it will never be shown again
                beforeRatingViewingNumber++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves to shared preferences
     * @param context
     */
    private static void saveOnDisk(Context context) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context
                .getString(R.string.pref_beforeRatingViewingNumber), beforeRatingViewingNumber);
        editor.commit();
    }

    /**
     * Shows a prompt as a pop up
     * @param context
     */
    private static void showPopup(final Context context) {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setContent(context.getString(R.string.rateApp));
        materialDialog.setPositiveButton(context.getString(R.string.yes));
        materialDialog.setNegativeButton(context.getString(R.string.no));
        materialDialog.setOnClickListener(new OnClick() {
            @Override
            public void OnPositive() {
                materialDialog.dismiss();

                //if user decides to rate it
                rateApplication(context);
                //never prompt again
                beforeRatingViewingNumber = -1;
                //save the situation
                saveOnDisk(context);
            }

            @Override
            public void OnNegative() {
                materialDialog.dismiss();

                //if user does not want to rate yet
                saveOnDisk(context);
            }
        });
        materialDialog.show();
    }

    /**
     * Leads user to store's rating panel
     * @param context
     */
    private static void rateApplication(Context context) {
        final String appName = context.getPackageName();
        try { //if app store is installed it will open
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appName)));
        } catch (android.content.ActivityNotFoundException anfe) { //otherwise brower will open
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="+appName)));
        }
    }
}
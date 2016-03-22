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

package com.ferid.app.classroom.utility;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.interfaces.PermissionGrantListener;

/**
 * Created by ferid.cafer on 3/22/2016.
 */
public class PermissionProcessor {
    private Activity activity;
    private View view;
    private PermissionGrantListener permissionGrantListener;
    public static final int REQUEST_EXTERNAL_STORAGE = 101;

    public PermissionProcessor(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
    }

    public void setPermissionGrantListener(PermissionGrantListener permissionGrantListener) {
        this.permissionGrantListener = permissionGrantListener;
    }

    /**
     * Ask for read-write external storage permission
     */
    public void askForPermissionExternalStorage() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) { //permission yet to be granted

            getPermissionExternalStorage();
        } else { //permission already granted
            if (permissionGrantListener != null) {
                permissionGrantListener.OnGranted();
            }
        }
    }

    /**
     * Request and get the permission for external storage
     */
    public void getPermissionExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Snackbar.make(view, R.string.grantPermission,
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_EXTERNAL_STORAGE);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

}
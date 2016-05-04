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

import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.ferid.app.classroom.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by ferid.cafer on 4/18/2016.
 */
public class DbBackUp {

    private final static String FILE_NAME = "AttendanceTakerBackUp.sqlite";

    /**
     * Export database
     * @param context
     * @param view
     */
    public static void exportDatabse(final Context context, View view) {
        if (DirectoryUtility.isExternalStorageMounted()) {

            DirectoryUtility.createDirectory();

            try {
                File sd = new File(DirectoryUtility.getPathFolder());
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String currentDBPath = "/data/" + context.getPackageName()
                            + "/databases/ClassroomManager";
                    String backupDBPath = FILE_NAME;
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();

                        Snackbar.make(view, context.getString(R.string.databaseExportSuccess),
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Snackbar.make(view, context.getString(R.string.databaseExportError),
                        Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(view, context.getString(R.string.mountExternalStorage),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Import database
     * @param context
     * @param view
     * @param path
     */
    public static void importDatabase(Context context, View view, String path) {
        if (DirectoryUtility.isExternalStorageMounted()) {
            File data = Environment.getDataDirectory();
            FileChannel source;
            FileChannel destination;
            String backupDBPath = "/data/" + context.getPackageName()
                    + "/databases/ClassroomManager";
            File currentDB = new File(path);
            File backupDB = new File(data, backupDBPath);

            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();

                Snackbar.make(view, context.getString(R.string.databaseImportSuccess),
                        Snackbar.LENGTH_LONG).show();
            } catch (IOException e) {
                Snackbar.make(view, context.getString(R.string.databaseImportError),
                        Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(view, context.getString(R.string.mountExternalStorage),
                    Snackbar.LENGTH_LONG).show();
        }
    }

}
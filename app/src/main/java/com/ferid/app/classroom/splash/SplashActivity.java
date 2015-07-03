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

package com.ferid.app.classroom.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.ferid.app.classroom.MainActivity;
import com.ferid.app.classroom.R;
import com.ferid.app.classroom.database.DatabaseManager;

/**
 * Created by ferid.cafer on 5/7/2015.
 */
public class SplashActivity extends Activity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        context = this;

        new GetNumberOfClassrooms().execute();
    }

    /**
     * Get number of classrooms from DB
     */
    private class GetNumberOfClassrooms extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            DatabaseManager databaseManager = new DatabaseManager(context);
            int numberOfClassrooms = databaseManager.countClassrooms();

            return numberOfClassrooms;
        }

        @Override
        protected void onPostExecute(Integer numberOfClassrooms) {
            startMainActivity(numberOfClassrooms);
        }
    }

    /**
     * finish splash screen, start main activity
     * @param numberOfClassrooms
     */
    private void startMainActivity(final int numberOfClassrooms) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("numberOfClassrooms", numberOfClassrooms);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            }
        }, 1000);
    }
}

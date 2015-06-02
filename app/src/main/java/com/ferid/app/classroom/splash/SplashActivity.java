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

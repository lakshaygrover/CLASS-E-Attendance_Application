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

package com.ferid.app.classroom;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ferid.app.classroom.attendance.AttendancesFragment;
import com.ferid.app.classroom.edit.EditClassroomFragment;
import com.ferid.app.classroom.enums.PermissionFor;
import com.ferid.app.classroom.interfaces.PermissionGrantListener;
import com.ferid.app.classroom.statistics.StatisticsFragment;
import com.ferid.app.classroom.tabs.SlidingTabLayout;
import com.ferid.app.classroom.utility.ApplicationRating;
import com.ferid.app.classroom.utility.DbBackUp;
import com.ferid.app.classroom.utility.DirectoryUtility;
import com.ferid.app.classroom.utility.PermissionProcessor;

/**
 * Created by ferid.cafer on 4/15/2015.
 */
public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;

    private SlidingTabLayout mSlidingTabLayout;

    private FloatingActionButton floatingActionButton;

    //permission for action
    private PermissionFor permissionFor = PermissionFor.NONE;
    private static final int REQUEST_IMPORT_DATABASE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        int numberOfClassrooms = 0;
        Bundle args = getIntent().getExtras();
        if (args != null) {
            numberOfClassrooms = args.getInt("numberOfClassrooms");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDividerColors(getResources().getColor(R.color.transparent));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        mSlidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.tabText);
        mSlidingTabLayout.setViewPager(viewPager);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        addOnPageChangeListener();
        //if there are already entered classrooms, just show take attendance page,
        //otherwise show edit classrooms page to add a new one.
        if (numberOfClassrooms > 0) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(0);
            //make floating button available to add classrooms
            setButtonAdd();
        }


        //rate the app
        ApplicationRating.ratingPopupManager(this);
    }

    /**
     * View Pager, page change listener.
     */
    private void addOnPageChangeListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: //editing
                        setButtonAdd();
                        break;
                    case 1: //attendance
                        setButtonHidden();
                        break;
                    case 2: //statistics
                        setButtonPublish();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * EditClassroom.<br />
     * Add a new classroom.
     */
    private void setButtonAdd() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                floatingActionButton.setImageResource(R.drawable.ic_action_add);
                floatingActionButton.show();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditClassroomFragment fragment = (EditClassroomFragment) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + viewPager.getId() + ":"
                                + mAdapter.getItemId(0));
                fragment.addClassroom();
            }
        });
    }

    /**
     * Attendance.<br />
     * Just hide the button.
     */
    private void setButtonHidden() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                floatingActionButton.hide();
            }
        });
    }

    /**
     * Statistics.<br />
     * Convert attendances into an excel file and share it.
     */
    private void setButtonPublish() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                floatingActionButton.setImageResource(R.drawable.ic_action_document);
                floatingActionButton.show();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionFor = PermissionFor.EXCEL_IMPORT;
                checkPermissionForExternal();
            }
        });
    }

    /**
     * Export attendances list as an excel file
     */
    private void exportToExcel() {
        StatisticsFragment fragment = (StatisticsFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + viewPager.getId() + ":"
                        + mAdapter.getItemId(2));
        fragment.getDataForExcel();
    }

    /**
     * Checks permission to access external storage.
     */
    public void checkPermissionForExternal() {
        PermissionProcessor permissionProcessor = new PermissionProcessor(this, viewPager);
        permissionProcessor.setPermissionGrantListener(new PermissionGrantListener() {
            @Override
            public void OnGranted() {
                if (permissionFor == PermissionFor.EXCEL_IMPORT) {
                    exportToExcel();
                } else if (permissionFor == PermissionFor.DATABASE_IMPORT) {
                    browseFiles();
                } else if (permissionFor == PermissionFor.DATABASE_EXPORT) {
                    DbBackUp.exportDatabse(MainActivity.this, viewPager);
                }

                //back to its initial state
                permissionFor = PermissionFor.NONE;
            }
        });
        permissionProcessor.askForPermissionExternalStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionProcessor.REQUEST_EXTERNAL_STORAGE: {
                //if request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (permissionFor == PermissionFor.EXCEL_IMPORT) {
                        exportToExcel();
                    } else if (permissionFor == PermissionFor.DATABASE_IMPORT) {
                        browseFiles();
                    } else if (permissionFor == PermissionFor.DATABASE_EXPORT) {
                        DbBackUp.exportDatabse(MainActivity.this, viewPager);
                    }

                    //back to its initial state
                    permissionFor = PermissionFor.NONE;
                }
                return;
            }
        }
    }

    /**
     * Get the path of the database file to import
     */
    private void browseFiles() {
        //if directory is not mounted do not start the operation
        if (DirectoryUtility.isExternalStorageMounted()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.selectFile)), REQUEST_IMPORT_DATABASE);
            } catch (ActivityNotFoundException e) {
                Snackbar.make(viewPager, getString(R.string.databaseImportError),
                        Snackbar.LENGTH_LONG).show();
            }

        } else {
            Snackbar.make(viewPager, getString(R.string.mountExternalStorage),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMPORT_DATABASE) {
            if (resultCode == RESULT_OK) {
                String filePath = data.getData().getPath();

                //only sqlite extension is allowed
                if (!filePath.endsWith("sqlite")) {
                    Snackbar.make(viewPager, getString(R.string.extensionWarningSqlite),
                            Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (filePath.contains(":")) {
                    String[] partFilePath = filePath.split(":");
                    if (partFilePath.length == 2) {
                        String fullPath = Environment.getExternalStorageDirectory()
                                + "/" + partFilePath[1];

                        DbBackUp.importDatabase(MainActivity.this, viewPager, fullPath);
                    } else {
                        Snackbar.make(viewPager, getString(R.string.databaseImportError),
                                Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    if (filePath != null) {
                        DbBackUp.importDatabase(MainActivity.this, viewPager, filePath);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.importDb:
                permissionFor = PermissionFor.DATABASE_IMPORT;
                checkPermissionForExternal();
                return true;
            case R.id.exportDb:
                permissionFor = PermissionFor.DATABASE_EXPORT;
                checkPermissionForExternal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return EditClassroomFragment.newInstance();
                case 1:
                    return AttendancesFragment.newInstance();
                case 2:
                    return StatisticsFragment.newInstance();
                default:
                    return AttendancesFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = getResources().getStringArray(R.array.main_page);
            return titles[position];
        }
    }

}
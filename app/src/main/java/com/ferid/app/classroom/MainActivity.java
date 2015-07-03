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

package com.ferid.app.classroom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ferid.app.classroom.attendance.TakeAttendanceFragment;
import com.ferid.app.classroom.edit.EditClassroomFragment;
import com.ferid.app.classroom.statistics.StatisticsFragment;
import com.ferid.app.classroom.tabs.SlidingTabLayout;
import com.ferid.app.classroom.utility.ApplicationRating;

/**
 * Created by ferid.cafer on 4/15/2015.
 */
public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;

    private SlidingTabLayout mSlidingTabLayout;

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
        mSlidingTabLayout.setViewPager(viewPager);
        mSlidingTabLayout.setDividerColors(getResources().getColor(R.color.transparent));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.white));

        //if there are already entered classrooms, just show take attendance page,
        //otherwise show edit classrooms page to add a new one.
        if (numberOfClassrooms > 0)
            viewPager.setCurrentItem(1);
        else
            viewPager.setCurrentItem(0);

        //rate the app
        ApplicationRating.ratingPopupManager(this);
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
                    return TakeAttendanceFragment.newInstance();
                case 2:
                    return StatisticsFragment.newInstance();
                default:
                    return TakeAttendanceFragment.newInstance();
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
/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nfdz.tomatito.R;
import io.github.nfdz.tomatito.data.Pomodoro;
import io.github.nfdz.tomatito.data.PreferencesUtils;
import io.github.nfdz.tomatito.utils.PomodoroUtils;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ActionMenuView.OnMenuItemClickListener {

    @BindView(R.id.appbar) AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.amvMenuRight) ActionMenuView mMenu;
    @BindView(R.id.main_pager) ViewPager mPager;
    @BindView(R.id.main_navigation) BottomNavigationView mNavigation;

    private NavigationListener mNavigationListener = new NavigationListener();
    private PagerListener mPagerListener = new PagerListener();
    private ScreenSlidePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mNavigation.setOnNavigationItemSelectedListener(mNavigationListener);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.addOnPageChangeListener(mPagerListener);
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPendingPomodoro();
    }

    /**
     * Checks if there is an ongoing pomodoro pending to be stored.
     */
    private void checkPendingPomodoro() {
        Pomodoro pomodoro = PreferencesUtils.getPomodoro(this);
        if (pomodoro.pomodoroStartTime != PreferencesUtils.POMODORO_START_TIME_DEFAULT &&
                !PomodoroUtils.isValid(pomodoro)) {
            PomodoroUtils.stopPomodoro(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, mMenu.getMenu());
        mMenu.setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return false;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PomodoroFragment.newInstance();
                case 1:
                    return RecordsFragment.newInstance();
                default:
                    Timber.e("Unexpected get item page position: " + position);
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

    private class PagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // expand app bar in order to work with non scrollable fragments
            mAppBarLayout.setExpanded(true, false);
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    mNavigation.setSelectedItemId(R.id.main_navigation_pomodoro);
                    break;
                case 1:
                    mNavigation.setSelectedItemId(R.id.main_navigation_records);
                    break;
                default:
                    Timber.e("Unexpected page selected position: " + position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // nothing to do
        }
    }

    private class NavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_navigation_pomodoro:
                    mPager.setCurrentItem(0);
                    return true;
                case R.id.main_navigation_records:
                    mPager.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    }

}

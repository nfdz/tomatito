/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.ui;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.tomatito.R;
import io.github.nfdz.tomatito.data.PreferencesUtils;
import timber.log.Timber;

public class PomodoroFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static long TIMER_REFRESH_RATE_MILLIS = 1000;

    @BindView(R.id.tv_pomodoro_time) TextView mPomodoroTime;
    @BindView(R.id.tv_pomodoros_value) TextView mPomodoros;
    @BindView(R.id.tv_breaks_value) TextView mBreaks;
    @BindView(R.id.fab_start_pomodoro) FloatingActionButton mStartButton;
    @BindView(R.id.fab_stop_pomodoro) FloatingActionButton mStopButton;
    @BindView(R.id.pb_pomodoro_progress) ProgressBar mProgressBar;

    private final Handler mTimerHandler = new Handler();
    private Runnable mTimerUpdater;

    public PomodoroFragment() {
        // required empty public constructor
    }

    public static PomodoroFragment newInstance() {
        PomodoroFragment fragment = new PomodoroFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
        updateCurrentPomodoro(PreferencesUtils.getCurrentPomodoro(getContext()));
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
        if (mTimerUpdater != null) mTimerHandler.removeCallbacks(mTimerUpdater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesUtils.CURRENT_POMODORO_KEY)) {
            updateCurrentPomodoro(PreferencesUtils.getCurrentPomodoro(getContext()));
        }
    }

    private void updateCurrentPomodoro(long currentPomodoro) {
        boolean validPomodoro = currentPomodoro != PreferencesUtils.CURRENT_POMODORO_DEFAULT;
        int pomodorosToLongBreak = PreferencesUtils.getPomodorosToLongBreak(getContext());
        if (validPomodoro) {
            mStopButton.setVisibility(View.VISIBLE);
            mStartButton.setVisibility(View.GONE);
            if (mTimerUpdater != null) mTimerHandler.removeCallbacks(mTimerUpdater);
            initPomodoro(pomodorosToLongBreak, currentPomodoro);
        } else {
            mStartButton.setVisibility(View.VISIBLE);
            mStopButton.setVisibility(View.GONE);
            if (mTimerUpdater != null) mTimerHandler.removeCallbacks(mTimerUpdater);
            clearPomodoro(pomodorosToLongBreak);
        }
    }

    @OnClick(R.id.fab_start_pomodoro)
    void startPomodoro() {
        long newPomodoro = System.currentTimeMillis();
        PreferencesUtils.setCurrentPomodoro(getContext(), newPomodoro);
    }

    @OnClick(R.id.fab_stop_pomodoro)
    void stopPomodoro() {
        PreferencesUtils.deleteCurrentPomodoro(getContext());
    }

    private void initPomodoro(int pomodorosToLongBreak, long currentPomodoro) {
        long pomodoroTime = PreferencesUtils.getPomodoroTime(getContext());
        long shortBreakTime = PreferencesUtils.getShortBreakTime(getContext());
        long longBreakTime = PreferencesUtils.getLongBreakTime(getContext());
        mTimerUpdater = new TimerUpdater(pomodoroTime, shortBreakTime, longBreakTime, currentPomodoro, pomodorosToLongBreak);
        mTimerHandler.post(mTimerUpdater);
    }

    private void clearPomodoro(int pomodorosToLongBreak) {
        mProgressBar.setProgress(0);
        mPomodoroTime.setText("00:00");
        mPomodoros.setText("0/" + pomodorosToLongBreak);
        mBreaks.setText("0/" + (pomodorosToLongBreak - 1));
    }

    private class TimerUpdater implements Runnable {

        private final long pomodoroTime;
        private final long currentPomodoro;
        private final long shortBreakTime;
        private final long longBreakTime;
        private final int pomodorosToLongBreak;

        public TimerUpdater(long pomodoroTime,
                            long shortBreakTime,
                            long longBreakTime,
                            long currentPomodoro,
                            int pomodorosToLongBreak) {
            this.pomodoroTime = pomodoroTime;
            this.currentPomodoro = currentPomodoro;
            this.shortBreakTime = shortBreakTime;
            this.longBreakTime = longBreakTime;
            this.pomodorosToLongBreak = pomodorosToLongBreak;
        }

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - currentPomodoro;
            int pomodoroCounter;

            for (pomodoroCounter = 1; pomodoroCounter <= pomodorosToLongBreak; pomodoroCounter++) {
                int breakCounter = pomodoroCounter - 1;
                long pomodoroEnd = pomodoroTime * pomodoroCounter + shortBreakTime * breakCounter;
                if (elapsedTime < pomodoroEnd) {
                    // it is working
                    long pomodoroStart = pomodoroEnd - pomodoroTime;
                    long thisPomodoroTime = elapsedTime - pomodoroStart;

                    mPomodoroTime.setText(getTimerTextFor(thisPomodoroTime));
                    mPomodoros.setText(pomodoroCounter + "/" + pomodorosToLongBreak);
                    mBreaks.setText(breakCounter + "/" + (pomodorosToLongBreak - 1));
                    setProgressBarColor(ContextCompat.getColor(getContext(), R.color.progressBarWorking));
                    int progress = (int) (((thisPomodoroTime + 0.0)/pomodoroTime) * 100);
                    mProgressBar.setProgress(progress);

                    break;
                } else if (elapsedTime < pomodoroEnd + shortBreakTime) {
                    // it is on a break
                    int currentBreak = breakCounter + 1;
                    if (currentBreak <= (pomodorosToLongBreak - 1)) {
                        // short break
                        long breakStart = pomodoroEnd;
                        long breakTime = elapsedTime - breakStart;

                        mPomodoroTime.setText(getTimerTextFor(breakTime));
                        mPomodoros.setText(pomodoroCounter + "/" + pomodorosToLongBreak);
                        mBreaks.setText(currentBreak + "/" + (pomodorosToLongBreak - 1));
                        setProgressBarColor(ContextCompat.getColor(getContext(), R.color.progressBarBreak));
                        int progress = (int) (((breakTime + 0.0)/shortBreakTime) * 100);
                        mProgressBar.setProgress(progress);
                    } else {
                        // long break, do nothing because long break is handle outside for
                    }

                    break;
                }
            }

            if (pomodoroCounter > pomodorosToLongBreak) {
                // long break
                long breakStart = pomodoroTime * pomodorosToLongBreak + shortBreakTime * (pomodorosToLongBreak - 1);
                long breakTime = elapsedTime - breakStart;

                mPomodoroTime.setText(getTimerTextFor(breakTime));
                mPomodoros.setText(pomodorosToLongBreak + "/" + pomodorosToLongBreak);
                mBreaks.setText((pomodorosToLongBreak - 1) + "/" + (pomodorosToLongBreak - 1));
                setProgressBarColor(ContextCompat.getColor(getContext(), R.color.progressBarBreak));
                int progress = (int) (((breakTime + 0.0)/longBreakTime) * 100);
                mProgressBar.setProgress(progress);
            }

            // reschedule this runnable
            mTimerHandler.postDelayed(this, TIMER_REFRESH_RATE_MILLIS);
        }
    }

    private String getTimerTextFor(long time) {
        String minutes = Long.toString(TimeUnit.MILLISECONDS.toMinutes(time));
        if (minutes.length() == 1) minutes = "0" + minutes;
        String seconds = Long.toString(TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        if (seconds.length() == 1) seconds = "0" + seconds;
        return minutes + ":" + seconds;
    }

    private void setProgressBarColor(@ColorInt int color) {
        mProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        mProgressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}

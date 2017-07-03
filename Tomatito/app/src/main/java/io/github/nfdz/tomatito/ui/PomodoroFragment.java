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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.tomatito.R;
import io.github.nfdz.tomatito.data.Pomodoro;
import io.github.nfdz.tomatito.data.PreferencesUtils;
import io.github.nfdz.tomatito.utils.PomodoroUtils;

public class PomodoroFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static long TIMER_REFRESH_RATE_MILLIS = 1000;

    @BindView(R.id.tv_pomodoro_time) TextView mPomodoroTime;
    @BindView(R.id.tv_pomodoro_total_time) TextView mPomodoroTotalTime;
    @BindView(R.id.tv_pomodoros_value) TextView mPomodoros;
    @BindView(R.id.tv_breaks_value) TextView mBreaks;
    @BindView(R.id.fab_start_pomodoro) FloatingActionButton mStartButton;
    @BindView(R.id.fab_stop_pomodoro) FloatingActionButton mStopButton;
    @BindView(R.id.pb_pomodoro_progress) ProgressBar mProgressBar;

    @Nullable @BindView(R.id.iv_pomodoro_state) ImageView mStateGif;

    private int currentGif = -1;

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
        updateCurrentPomodoro();
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
        if (key.equals(PreferencesUtils.POMODORO_START_TIME_KEY)) {
            updateCurrentPomodoro();
        }
    }

    private void updateCurrentPomodoro() {
        if (mTimerUpdater != null) mTimerHandler.removeCallbacks(mTimerUpdater);
        Pomodoro pomodoro = PreferencesUtils.getPomodoro(getContext());
        if (PomodoroUtils.isValid(pomodoro)) {
            initPomodoro(pomodoro);
        } else {
            clearPomodoro(pomodoro);
        }
    }

    @OnClick(R.id.fab_start_pomodoro)
    void startPomodoro() {
        PomodoroUtils.startPomodoro(getContext());
    }

    @OnClick(R.id.fab_stop_pomodoro)
    void stopPomodoro() {
        PomodoroUtils.stopPomodoro(getContext());
    }

    private void initPomodoro(Pomodoro pomodoro) {
        mStopButton.setVisibility(View.VISIBLE);
        mStartButton.setVisibility(View.GONE);

        mTimerUpdater = new TimerUpdater(pomodoro);
        mTimerHandler.post(mTimerUpdater);
    }

    private void clearPomodoro(Pomodoro pomodoro) {
        mStartButton.setVisibility(View.VISIBLE);
        mStopButton.setVisibility(View.GONE);

        mProgressBar.setProgress(0);
        mPomodoroTime.setText("00:00");
        mPomodoroTotalTime.setText("/ " + PomodoroUtils.getTimerTextFor(pomodoro.pomodoroTime));
        mPomodoros.setText("0/" + pomodoro.pomodorosToLongBreak);
        mBreaks.setText("0/" + (pomodoro.pomodorosToLongBreak - 1));
        disableGif();
    }

    private class TimerUpdater implements Runnable {

        private final Pomodoro pomodoro;

        public TimerUpdater(Pomodoro pomodoro) {
            this.pomodoro = pomodoro;
        }

        @Override
        public void run() {
            int progress;
            PomodoroUtils.PomodoroState state = PomodoroUtils.getPomodoroState(pomodoro);
            switch (state.flag) {
                case PomodoroUtils.INVALID_STATE:
                    disableGif();
                    clearPomodoro(pomodoro);
                    return;
                case PomodoroUtils.WORKING_STATE:
                    mPomodoroTime.setText(PomodoroUtils.getTimerTextFor(state.stateProgressTime));
                    mPomodoros.setText(state.pomodoroCounter + "/" + pomodoro.pomodorosToLongBreak);
                    mBreaks.setText(state.breakCounter + "/" + (pomodoro.pomodorosToLongBreak - 1));
                    setProgressBarColor(ContextCompat.getColor(getContext(), R.color.progressBarWorking));
                    progress = (int) (((state.stateProgressTime + 0.0)/pomodoro.pomodoroTime) * 100);
                    mProgressBar.setProgress(progress);
                    mPomodoroTotalTime.setText("/ " + PomodoroUtils.getTimerTextFor(pomodoro.pomodoroTime));
                    setWorkingGif();
                    break;
                case PomodoroUtils.SHORT_BREAK_STATE:
                    mPomodoroTime.setText(PomodoroUtils.getTimerTextFor(state.stateProgressTime));
                    mPomodoros.setText(state.pomodoroCounter + "/" + pomodoro.pomodorosToLongBreak);
                    mBreaks.setText(state.pomodoroCounter + "/" + (pomodoro.pomodorosToLongBreak - 1));
                    setProgressBarColor(ContextCompat.getColor(getContext(), R.color.progressBarBreak));
                    progress = (int) (((state.stateProgressTime + 0.0)/pomodoro.shortBreakTime) * 100);
                    mProgressBar.setProgress(progress);
                    mPomodoroTotalTime.setText("/ " + PomodoroUtils.getTimerTextFor(pomodoro.shortBreakTime));
                    setBreakGif();
                    break;
                case PomodoroUtils.LONG_BREAK_STATE:
                    mPomodoroTime.setText(PomodoroUtils.getTimerTextFor(state.stateProgressTime));
                    mPomodoros.setText(pomodoro.pomodorosToLongBreak + "/" + pomodoro.pomodorosToLongBreak);
                    mBreaks.setText((pomodoro.pomodorosToLongBreak - 1) + "/" + (pomodoro.pomodorosToLongBreak - 1));
                    setProgressBarColor(ContextCompat.getColor(getContext(), R.color.progressBarBreak));
                    progress = (int) (((state.stateProgressTime + 0.0)/pomodoro.longBreakTime) * 100);
                    mProgressBar.setProgress(progress);
                    mPomodoroTotalTime.setText("/ " + PomodoroUtils.getTimerTextFor(pomodoro.longBreakTime));
                    setFireworksGif();
                    break;
            }

            // reschedule this runnable
            mTimerHandler.postDelayed(this, TIMER_REFRESH_RATE_MILLIS);
        }
    }

    private void setProgressBarColor(@ColorInt int color) {
        mProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        mProgressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void setWorkingGif() {
        if (mStateGif != null && currentGif != R.raw.tomato_working) {
            currentGif = R.raw.tomato_working;
            Glide.with(getContext())
                    .load(R.raw.tomato_working)
                    .into(mStateGif);
        }
    }

    private void setBreakGif() {
        if (mStateGif != null && currentGif != R.raw.tomato_break) {
            currentGif = R.raw.tomato_break;
            Glide.with(getContext())
                    .load(R.raw.tomato_break)
                    .into(mStateGif);
        }
    }

    private void disableGif() {
        if (mStateGif != null && currentGif != -1) {
            currentGif = -1;
            Glide.with(getContext())
                    .load(null)
                    .into(mStateGif);
        }
    }

    private void setFireworksGif() {
        if (mStateGif != null && currentGif != R.raw.fireworks) {
            currentGif = R.raw.fireworks;
            Glide.with(getContext())
                    .load(R.raw.fireworks)
                    .into(mStateGif);
        }
    }

}

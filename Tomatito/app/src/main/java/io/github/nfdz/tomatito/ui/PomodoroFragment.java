/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.tomatito.R;
import io.github.nfdz.tomatito.data.PreferencesUtils;

public class PomodoroFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.tv_pomodoro_time) TextView mPomodoroTime;
    @BindView(R.id.tv_pomodoros_value) TextView mPomodoros;
    @BindView(R.id.tv_breaks_value) TextView mBreaks;
    @BindView(R.id.fab_start_pomodoro) FloatingActionButton mStartButton;
    @BindView(R.id.fab_stop_pomodoro) FloatingActionButton mStopButton;

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
        if (validPomodoro) {
            mStartButton.setVisibility(View.GONE);

        } else {
            mStopButton.setVisibility(View.GONE);

        }
    }

    @OnClick(R.id.fab_start_pomodoro)
    void startPomodoro() {
    }

    @OnClick(R.id.fab_stop_pomodoro)
    void stopPomodoro() {
    }
}

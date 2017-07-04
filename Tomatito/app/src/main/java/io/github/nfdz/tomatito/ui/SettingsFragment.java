package io.github.nfdz.tomatito.ui;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import io.github.nfdz.tomatito.R;
import io.github.nfdz.tomatito.data.DatabaseManager;
import io.github.nfdz.tomatito.data.PreferencesUtils;
import io.github.nfdz.tomatito.utils.AlarmUtils;
import io.github.nfdz.tomatito.utils.PomodoroUtils;

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference restoreDefaultPrefs = findPreference(getString(R.string.pref_restore_default_key));
        restoreDefaultPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                PreferencesUtils.restoreDefaultPomodoroPrefs(getActivity());
                return true;
            }
        });

        Preference clearStoragePrefs = findPreference(getString(R.string.pref_storage_clear_key));
        clearStoragePrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        DatabaseManager.getInstance(getActivity()).removeAllPomodoros();
                        return null;
                    }
                }.execute();
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_alarm_key))) {
            AlarmUtils.scheduleAlarm(getActivity());
        } else if (key.equals(getString(R.string.pref_pomodoro_time_key))) {
            int defaultValue = Integer.parseInt(getString(R.string.pref_pomodoro_time_default));
            ((NumberPickerPreference) findPreference(key))
                    .setValue(sharedPreferences.getInt(key, defaultValue));
            PomodoroUtils.stopPomodoro(getActivity());
        } else if (key.equals(getString(R.string.pref_short_break_time_key))) {
            int defaultValue = Integer.parseInt(getString(R.string.pref_short_break_time_default));
            ((NumberPickerPreference) findPreference(key))
                    .setValue(sharedPreferences.getInt(key, defaultValue));
            PomodoroUtils.stopPomodoro(getActivity());
        } else if (key.equals(getString(R.string.pref_long_break_time_key))) {
            int defaultValue = Integer.parseInt(getString(R.string.pref_long_break_time_default));
            ((NumberPickerPreference) findPreference(key))
                    .setValue(sharedPreferences.getInt(key, defaultValue));
            PomodoroUtils.stopPomodoro(getActivity());
        } else if (key.equals(getString(R.string.pref_pomodoros_to_long_break_key))) {
            int defaultValue = Integer.parseInt(getString(R.string.pref_pomodoros_to_long_break_default));
            ((NumberPickerPreference) findPreference(key))
                    .setValue(sharedPreferences.getInt(key, defaultValue));
            PomodoroUtils.stopPomodoro(getActivity());
        } else if (key.equals(getString(R.string.pref_storage_limit_key))) {
            // TODO
        }
    }
}

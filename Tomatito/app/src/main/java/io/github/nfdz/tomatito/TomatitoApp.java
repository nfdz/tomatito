/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito;

import android.app.Application;

import io.github.nfdz.tomatito.utils.AlarmUtils;
import timber.log.Timber;

public class TomatitoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }
        AlarmUtils.initialize(getApplicationContext());
    }
}
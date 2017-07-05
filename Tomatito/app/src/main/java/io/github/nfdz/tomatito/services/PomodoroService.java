package io.github.nfdz.tomatito.services;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import io.github.nfdz.tomatito.utils.PomodoroUtils;

/**
 * This service manages ongoing pomodoro in an asynchronous and safe way.
 * It uses intent action to resolve the operation to perform.
 */
public class PomodoroService extends IntentService {

    public static final String STOP_POMODORO = "stop";
    public static final String RESTART_POMODORO = "restart";

    public PomodoroService() {
        super(PomodoroService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        switch(action) {
            case STOP_POMODORO:
                PomodoroUtils.stopPomodoro(this);
                break;
            case RESTART_POMODORO:
                PomodoroUtils.startPomodoro(this);
                break;
        }
    }
}

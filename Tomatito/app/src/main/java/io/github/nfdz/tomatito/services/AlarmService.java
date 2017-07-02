package io.github.nfdz.tomatito.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import io.github.nfdz.tomatito.data.PreferencesUtils;
import io.github.nfdz.tomatito.utils.AlarmUtils;
import io.github.nfdz.tomatito.utils.NotificationUtils;
import timber.log.Timber;

public class AlarmService extends IntentService {

    public static final String WORK_ALARM = "work";
    public static final String SHORT_BREAK_ALARM = "short-break";
    public static final String LONG_BREAK_ALARM = "long-break";
    public static final String END_ALARM = "end";

    public AlarmService() {
        super(AlarmService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Timber.d("Alarm service triggered, action: " + action);

        boolean alarmEnabled = PreferencesUtils.getAlarmEnabled(this);
        if (alarmEnabled) {

            // notify
            switch(action) {
                case WORK_ALARM:
                    NotificationUtils.notifyWork(this);
                    break;
                case SHORT_BREAK_ALARM:
                    NotificationUtils.notifyShortBreak(this);
                    break;
                case LONG_BREAK_ALARM:
                    NotificationUtils.notifyLongBreak(this);
                    break;
                case END_ALARM:
                    NotificationUtils.notifyEnd(this);
                    break;
            }

            // schedule the next one
            AlarmUtils.scheduleAlarm(this);
        }

    }
}

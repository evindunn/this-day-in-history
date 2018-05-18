package com.emdevsite.todayhist.sync;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.emdevsite.todayhist.utils.DateUtils;

/**
 * Class for scheduling sync jobs every night at midnight
 */
public class SyncIntentService extends IntentService {
    private static final String SERVICE_NAME = "com.emdevsite.todayhist.SERVICE_SYNC_SCHEDULER";
    private static final String ACTION_SCHEDULE_SYNC = "com.emdevsite.todayhist.ACTION_SCHEDULE_SYNC";

    public SyncIntentService() {
        super(SERVICE_NAME);
    }

    /**
     * Creates an instance of the inner class FirebaseSyncJobService and dispatches it; The job
     * will run as soon as the network is available
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        FirebaseSyncJobService.dispatchScheduledSync(this);
    }

    /**
     * Schedules a ScheduleSyncIntentService to run every night at midnight
     * @param context The context from which the sync is being scheduled
     * @throws NullPointerException Thrown if the system Alarm Service cannot be reached
     */
    public static void scheduleSync(Context context) throws NullPointerException {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent scheduleIntent = new Intent(context, SyncIntentService.class);
        scheduleIntent.setAction(ACTION_SCHEDULE_SYNC);
        PendingIntent pendingSchedule = PendingIntent.getService(
            context,
            0,
            scheduleIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        );

        long midnight = DateUtils.getMidnight();
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            midnight,
            AlarmManager.INTERVAL_DAY,
            pendingSchedule
        );

        // Log it!
        Log.d(SyncIntentService.class.getSimpleName(), String.format(
            "Recurring sync starting on %s at %s",
            DateUtils.sDateFormatter.format(midnight),
            DateUtils.sTimeFormatter.format(midnight)
        ));
    }
}

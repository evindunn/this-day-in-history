package com.emdevsite.todayhist.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

public class SyncUtils {
    private static final String SYNC_TAG = "com.emdevsite.todayhist.todayhist-sync";

    // Once every 5-6 mins
    private static final int SYNC_INTERVAL_SECONDS = 60;
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS * 2;

    // Once every 3-4 days
    private static final int CLEAN_INTERVAL_SECONDS = 3600 * 24 * 3;
    private static final int CLEAN_INTERVAL_FLEX = 3600 * 24 * 4;

    private static boolean sInitialized;

    // TODO: See sunshine
    public static void initialize(final Context context) {
        if (sInitialized) { return; }
        sInitialized = true;

        // Schedule recurring sync
        scheduleSync(context);

        // Schedule recurring clean
        scheduleClean(context);

        // Check if db has data for today
        Thread checkDb = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] projection = new String[] { EventDbContract.EventTable._ID };
                String selection = String.format(
                    "%s = ?", EventDbContract.EventTable.COLUMN_TIMESTAMP
                );

                Cursor cursor = context.getContentResolver().query(
                    EventDbContract.EventTable.CONTENT_URI,
                    projection,
                    selection,
                    new String[] { String.valueOf(DateUtils.getTimestamp()) },
                    null
                );

                if (cursor == null || cursor.getCount() == 0) {
                    SyncUtils.syncNow(context);
                }

                if (cursor != null) { cursor.close(); }
            }
        });

        // Start check in separate thread
        checkDb.start();
    }

    public static void scheduleSync(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job eventSyncJob = dispatcher.newJobBuilder()
            .setService(EventFirebaseSyncService.class)
            .setTag(SYNC_TAG)
            .setLifetime(Lifetime.FOREVER)
            .setRecurring(true)
            .setTrigger(Trigger.executionWindow(SYNC_INTERVAL_SECONDS, SYNC_FLEXTIME_SECONDS))
            .setReplaceCurrent(true)
            .build();

        dispatcher.schedule(eventSyncJob);
        LogUtils.logMessage('d', SyncUtils.class, "Recurring sync scheduled");
    }

    public static void scheduleClean(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job eventSyncJob = dispatcher.newJobBuilder()
            .setService(EventFirebaseCleanService.class)
            .setTag(SYNC_TAG)
            .setConstraints(Constraint.ON_ANY_NETWORK)
            .setLifetime(Lifetime.FOREVER)
            .setRecurring(true)
            .setTrigger(Trigger.executionWindow(CLEAN_INTERVAL_SECONDS, CLEAN_INTERVAL_FLEX))
            .setReplaceCurrent(true)
            .build();

        dispatcher.schedule(eventSyncJob);
        LogUtils.logMessage('d', SyncUtils.class, "Recurring clean scheduled");
    }

    /**
     * Immediately syncs the event db
     * @param context The context for the sync service
     */
    public static void syncNow(Context context) {
        Intent immediateSync = new Intent(context, SyncIntentService.class);
        context.startService(immediateSync);
    }

    /**
     * Immediately cleans the db of stale events (not from today)
     * @param context The context for the clean service
     */
    public static void cleanNow(Context context) {
        Intent immediateClean = new Intent(context, CleanIntentService.class);
        context.startService(immediateClean);
    }
}

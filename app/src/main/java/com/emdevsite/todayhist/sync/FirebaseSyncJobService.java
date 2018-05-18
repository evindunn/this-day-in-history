package com.emdevsite.todayhist.sync;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

/**
 * Class for dispatching & running sync jobs
 */
public class FirebaseSyncJobService extends JobService {
    private static final String SYNC_TAG = "com.emdevsite.todayhist.SYNC_JOB";
    private static final String SYNC_TAG_SCHEDULED = "com.emdevsite.todayhist.SYNC_JOB_SCHEDULED";
    private static final String LOG_TAG = FirebaseSyncJobService.class.getSimpleName();

    /**
     * Runs when the job is started
     * @return Whether work is still going on when the method returns
     */
    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(LOG_TAG, "Sync job started...");
        new Thread(new SyncRunnable(job)).start();
        return true;
    }

    /**
     * Runs when the job is stopped
     * @return Whether the job should be retried
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(LOG_TAG, "Sync job stopped");
        return true;
    }

    /**
     * When the sync has completed in a background thread, calls jobFinished()
     */
    private void onSyncFinished(JobParameters job) {
        jobFinished(job, false); // Is a reschedule needed?
        Log.d(LOG_TAG, "Sync job finished");
    }

    /**
     * Dispatches a sync for as soon as the network is available
     * @param context The context requesting the sync
     */
    public static void dispatchSyncNow(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job syncJob = dispatcher.newJobBuilder()
            .setTag(SYNC_TAG)
            .setService(FirebaseSyncJobService.class)
            .setTrigger(Trigger.NOW)
            .setRecurring(false)
            .setReplaceCurrent(true)
            .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
            .addConstraint(Constraint.ON_ANY_NETWORK)
            .build();
        dispatcher.mustSchedule(syncJob);
        Log.d(LOG_TAG, "Sync job dispatched");
    }

    /**
     * Dispatches a sync for midnight on the day it was triggered; The sync will run as soon
     * as the network is available after midnight
     * @param context The context requesting the sync
     */
    public static void dispatchScheduledSync(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job syncJob = dispatcher.newJobBuilder()
            .setTag(SYNC_TAG_SCHEDULED)
            .setService(FirebaseSyncJobService.class)
            .setTrigger(Trigger.NOW)
            .setRecurring(false)
            .setReplaceCurrent(true)
            .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
            .addConstraint(Constraint.ON_ANY_NETWORK)
            .build();
        dispatcher.mustSchedule(syncJob);
        Log.d(LOG_TAG, "Recurring sync job dispatched");
    }

    /**
     * Runnable for syncing the db in a background thread
     */
    private class SyncRunnable implements Runnable {
        private JobParameters mJob;

        SyncRunnable(JobParameters job) {
            mJob = job;
        }

        @Override
        public void run() {
            try {
                SyncTask.syncEvents(getBaseContext());
                onSyncFinished(mJob);
            } catch (Exception e) {
                Log.e(LOG_TAG, String.format("Error syncing database: %s", e.getMessage()));
            }
        }
    }
}

package com.emdevsite.todayhist.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class EventFirebaseSyncService extends JobService {
    AsyncTask<Void, Void, Void> mSyncEventsTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mSyncEventsTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                SyncUtils.syncNow(context);
                jobFinished(job, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        mSyncEventsTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mSyncEventsTask != null) {
            mSyncEventsTask.cancel(true);
        }
        return true;
    }
}

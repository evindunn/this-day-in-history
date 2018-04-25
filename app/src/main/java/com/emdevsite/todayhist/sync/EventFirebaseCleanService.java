package com.emdevsite.todayhist.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class EventFirebaseCleanService extends JobService {
    AsyncTask<Void, Void, Void> mCleanEventsTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mCleanEventsTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                SyncUtils.cleanNow(context);
                jobFinished(job, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        mCleanEventsTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mCleanEventsTask != null) {
            mCleanEventsTask.cancel(true);
        }
        return true;
    }
}

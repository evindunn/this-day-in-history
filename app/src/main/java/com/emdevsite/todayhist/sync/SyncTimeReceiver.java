package com.emdevsite.todayhist.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;

public class SyncTimeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(@NonNull final Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
            final PendingResult pendingResult = goAsync();

            // Do the work in a background thread
            final AsyncTask<Void, Void, Void> syncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Cursor cursor = context.getContentResolver().query(
                        EventDbContract.EventTable.CONTENT_URI,
                        new String[] {EventDbContract.EventTable.COLUMN_TIMESTAMP},
                        String.format("%s = ?", EventDbContract.EventTable.COLUMN_TIMESTAMP),
                        new String[] { String.valueOf(DateUtils.getTimestamp()) },
                        null
                    );

                    if (cursor == null || cursor.getCount() == 0) {
                        LogUtils.logMessage(
                            'd',
                            SyncTimeReceiver.class,
                            "No entries for today, going to sync"
                        );

                        SyncUtils.syncNow(context);

                    } else {
                        LogUtils.logMessage(
                            'd',
                            SyncTimeReceiver.class,
                            "Entries for date are present, no sync required"
                        );
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    pendingResult.finish();
                    super.onPostExecute(aVoid);
                }
            };

            syncTask.execute();
        }
    }
}

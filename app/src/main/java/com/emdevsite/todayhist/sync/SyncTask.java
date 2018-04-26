package com.emdevsite.todayhist.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.preference.PreferenceManager;

import com.emdevsite.todayhist.R;
import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.data.HistoryGetter;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;

import java.util.Locale;

public class SyncTask {

    synchronized public static void syncEvents(Context context) {
        try {
            long today = DateUtils.getTimestamp();

            // Get today's events from server
            ContentValues[] values = HistoryGetter.asContentValues(today);

            if (values != null && values.length > 0) {
                ContentResolver resolver = context.getContentResolver();

                // Insert new data
                int inserted = resolver.bulkInsert(EventDbContract.EventTable.CONTENT_URI, values);

                LogUtils.logMessage(
                        'd',
                        SyncTask.class,
                        String.format(
                            "Added %d new db entries for %s",
                            inserted,
                            DateUtils.getTimestampAsString(today)
                        )
                );
            }
        } catch (Exception e) {
            LogUtils.logError('w', SyncTask.class, e);
        }
    }

    synchronized public static void cleanEvents(Context context) {
        try {
            LogUtils.logMessage(
                'd',
                SyncTask.class,
                "Starting clean task..."
            );

            // Delete data not from today
            int deleted = context.getContentResolver().delete(
                EventDbContract.EventTable.CONTENT_URI,
                String.format("%s != ?", EventDbContract.EventTable.COLUMN_TIMESTAMP),
                new String[]{String.valueOf(DateUtils.getTimestamp())}
            );

            LogUtils.logMessage(
                'd',
                SyncTask.class,
                String.format("Deleted %d stale db entries", deleted)
            );
        } catch (Exception e) {
            LogUtils.logError('w', SyncTask.class, e);
        }
    }
}

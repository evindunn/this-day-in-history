package com.emdevsite.todayhist.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.v4.util.LogWriter;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.data.HistoryGetter;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;

import java.util.Calendar;
import java.util.Locale;

public class SyncTask {

    synchronized public static void syncEvents(Context context) {
        try {

            long today = DateUtils.getTimestamp();
            ContentValues[] values = HistoryGetter.asContentValues(today);

            if (values != null && values.length > 0) {
                ContentResolver resolver = context.getContentResolver();

                // Delete all values not from today
                int deleted = resolver.delete(
                        EventDbContract.EventTable.CONTENT_URI,
                        String.format("%s != ?", EventDbContract.EventTable.COLUMN_DATE),
                        new String[] { String.valueOf(today) }
                );

                LogUtils.logMessage(
                        'd',
                        SyncTask.class,
                        String.format(Locale.getDefault(), "Deleted %d stale db entries", deleted)
                );

                // Insert new data
                int inserted = resolver.bulkInsert(EventDbContract.EventTable.CONTENT_URI, values);

                LogUtils.logMessage(
                        'd',
                        SyncTask.class,
                        String.format(Locale.getDefault(), "Added %d new db entries", inserted)
                );

                resolver.notifyChange(EventDbContract.EventTable.CONTENT_URI, null);
            }
        } catch (Exception e) {
            LogUtils.logError('w', SyncTask.class, e);
        }
    }
}

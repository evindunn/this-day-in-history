package com.emdevsite.todayhist.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.data.HistoryGetter;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;

import java.util.Calendar;

public class SyncTask {

    synchronized public static void syncEvents(Context context) {
        try {

            long today = DateUtils.getTimestamp();

            ContentValues[] values = HistoryGetter.asContentValues(today);

            if (values != null && values.length > 0) {
                ContentResolver resolver = context.getContentResolver();

                // Delete all values not from today
                resolver.delete(
                        EventDbContract.EventTable.CONTENT_URI,
                        String.format("%s != ?", EventDbContract.EventTable.COLUMN_DATE),
                        new String[] { String.valueOf(today) }
                );

                // Insert new data
                resolver.bulkInsert(EventDbContract.EventTable.CONTENT_URI, values);
            }
        } catch (Exception e) {
            LogUtils.logError('w', SyncTask.class, e);
        }
    }
}

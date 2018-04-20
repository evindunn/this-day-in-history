package com.emdevsite.todayhist.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.LogWriter;

import com.emdevsite.todayhist.R;
import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.data.HistoryGetter;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;

import java.util.Calendar;
import java.util.Locale;

public class SyncTask {

    synchronized public static void syncEvents(Context context) {
        try {
            // Skip if already synced today
            long today = DateUtils.getTimestamp();
            long lastUpdate = PreferenceManager.getDefaultSharedPreferences(context)
                    .getLong(context.getString(R.string.prefs_key_lastUpdate), -1);
            if (lastUpdate == today) {
                LogUtils.logMessage('d', SyncTask.class, "Already synced today, skipping");
                return;
            }

            ContentValues[] values = HistoryGetter.asContentValues(today);

            if (values != null && values.length > 0) {
                ContentResolver resolver = context.getContentResolver();

                // TODO: Delete all values not from today
                int deleted = resolver.delete(
                        EventDbContract.EventTable.CONTENT_URI,
                        null,
                        null
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

                // Log the update time
                PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putLong(
                            context.getString(R.string.prefs_key_lastUpdate),
                            DateUtils.getTimestamp()
                        ).apply();
            }
        } catch (Exception e) {
            LogUtils.logError('w', SyncTask.class, e);
        }
    }
}

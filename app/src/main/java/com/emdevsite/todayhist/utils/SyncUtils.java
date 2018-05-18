package com.emdevsite.todayhist.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.sync.FirebaseSyncJobService;
import com.emdevsite.todayhist.sync.SyncIntentService;

public class SyncUtils {
    private static final String ERR_TAG = SyncUtils.class.getSimpleName();
    private static boolean sInitialized;

    /**
     * Called once per app lifecycle to initialize, make sure we have today's values
     * @param context The context calling the initialization
     */
    public static synchronized void initialize(@NonNull final Context context) {
        // Only perform initialization once per app lifetime
        if (sInitialized) { return; }
        sInitialized = true;

        // Schedule nightly sync
        SyncIntentService.scheduleSync(context);

        // Check if the db has entries for today
        Thread checkDbEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor cursor = context.getContentResolver().query(
                        EventDbContract.EventTable.CONTENT_URI,
                        new String[]{EventDbContract.EventTable.COLUMN_TIMESTAMP},
                        String.format("%s = ?", EventDbContract.EventTable.COLUMN_TIMESTAMP),
                        new String[]{String.valueOf(DateUtils.getTimestamp())},
                        null
                    );

                    // If no entries for today, sync
                    if (cursor == null || cursor.getCount() == 0) {
                        FirebaseSyncJobService.dispatchSyncNow(context);
                    }

                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception e) {
                    Log.e(
                        ERR_TAG,
                        String.format("Error querying the database: %s", e.getMessage())
                    );
                }
            }
        });
        checkDbEmpty.start();
    }
}

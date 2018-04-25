package com.emdevsite.todayhist.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;

public class SyncTimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull final Context context, Intent intent) {

        long today = DateUtils.getTimestamp();
        long lastUpdate = LogUtils.getLastUpdate(context);

        if (lastUpdate == today) {
            LogUtils.logMessage('d', SyncTask.class, "Already synced today, skipping");
            return;
        }

        // Log the day this update occured on
        LogUtils.logUpdate(context);

        // Sync in a background thread
        Thread syncThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SyncUtils.syncNow(context);
            }
        });
        syncThread.start();
    }
}

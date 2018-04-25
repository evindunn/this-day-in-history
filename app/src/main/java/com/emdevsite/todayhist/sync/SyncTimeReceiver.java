package com.emdevsite.todayhist.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;

public class SyncTimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        long today = DateUtils.getTimestamp();
        long lastUpdate = LogUtils.getLastUpdate(context);

        if (lastUpdate == today) {
            LogUtils.logMessage('d', SyncTask.class, "Already synced today, skipping");
            return;
        }
    }
}

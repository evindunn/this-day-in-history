package com.emdevsite.todayhist.sync;

import android.content.Context;
import android.content.Intent;

import com.emdevsite.todayhist.utils.DateUtils;

public class SyncUtils {

    /**
     * Immediately syncs the event db
     * @param context The context for the sync service
     */
    public static void syncNow(Context context) {
        Intent immediateSync = new Intent(context, SyncIntentService.class);
        context.startService(immediateSync);
    }

}

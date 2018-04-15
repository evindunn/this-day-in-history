package com.emdevsite.todayhist.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.emdevsite.todayhist.utils.LogUtils;

public class SyncIntentService extends IntentService {

    public SyncIntentService() {
        super("history-sync-service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SyncTask.syncEvents(this);
    }
}

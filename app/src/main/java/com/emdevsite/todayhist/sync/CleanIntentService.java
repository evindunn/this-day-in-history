package com.emdevsite.todayhist.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class CleanIntentService extends IntentService {

    public CleanIntentService() {
        super("history-clean-service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) { SyncTask.cleanEvents(this); }
}

package com.emdevsite.todayhist.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by edunn on 3/12/18.
 * Class for organizing history event Db
 */

public final class EventDbContract {
    public static final String AUTHORITY = "com.emdevsite.todayhist";
    public static final Uri BASE_CONTENT_URI = Uri.parse(String.format("content://%s", AUTHORITY));
    public static final String PATH_EVENTS = "events";

    private EventDbContract() {}

    public static final class EventTable implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_EVENTS)
                .build();

        public static final String TABLE_NAME = "events";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_TEXT = "text";
    }
}

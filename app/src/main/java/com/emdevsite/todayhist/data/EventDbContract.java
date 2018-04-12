package com.emdevsite.todayhist.data;

import android.provider.BaseColumns;

/**
 * Created by edunn on 3/12/18.
 * Class for organizing history event Db
 */

public final class EventDbContract {
    private EventDbContract() {}

    public static final class EventTable implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_TEXT = "text";
    }
}

package com.emdevsite.todayhist.data;

import android.provider.BaseColumns;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by edunn on 3/12/18.
 * Class for organizing history event Db
 */

final class EventDbHelper {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.US);

    private EventDbHelper() {}

    public static final class EventTable implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_TEXT = "text";
    }

    public static int getDate() {
        try { return Integer.valueOf(DATE_FORMAT.format(Calendar.getInstance().getTime())); }
        catch (Exception e) {
            Log.w(
                EventDbHelper.class.getSimpleName(),
                String.format("%s: %s", e.getClass(), e.getMessage())
            );
        }
        return -1;
    }
}

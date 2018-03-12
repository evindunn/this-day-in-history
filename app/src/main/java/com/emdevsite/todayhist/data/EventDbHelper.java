package com.emdevsite.todayhist.data;

import android.provider.BaseColumns;
import android.util.Log;

import com.emdevsite.todayhist.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by edunn on 3/12/18.
 * Class for organizing history event Db
 */

public final class EventDbHelper {
    private EventDbHelper() {}

    public static final class EventTable implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_TEXT = "text";
    }
}

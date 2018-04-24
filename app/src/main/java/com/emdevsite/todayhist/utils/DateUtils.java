package com.emdevsite.todayhist.utils;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Assorted utilities for manipulation of dates/timestamps
 */
public class DateUtils {
    private static final String DATE_FMT = "EEEE, MMMM dd";

    /**
     * @param timestamp The requested date
     * @return Formatted string representation of the requested date
     */
    public static String getTimestampAsString(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FMT, Locale.getDefault());
        calendar.setTimeInMillis(timestamp);
        return formatter.format(calendar.getTime());
    }

    /**
     * @param field The desired Calendar.[FIELD]
     * @param timestamp The timestamp to get the requested field from
     * @return
     */
    public static int getFieldFromTimestamp(int field, long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int val = calendar.get(field);
        if (field == Calendar.MONTH) {
            val++;
        }
        return val;
    }

    /**
     * @return Today's date as a long timestamp
     */
    public static long getTimestamp() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * @return The long timestamp for the requested month & day
     * Year is always the current year and time is always midnight
     */
    public static long getTimestamp(int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}

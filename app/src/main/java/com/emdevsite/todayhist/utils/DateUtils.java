package com.emdevsite.todayhist.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Assorted utilities for manipulation of dates/timestamps
 */
public class DateUtils {
    private static final String DATE_FMT = "MMMM dd";
    private static final Calendar sCalendar = new GregorianCalendar(Locale.getDefault());

    public static final SimpleDateFormat sDateFormatter = new SimpleDateFormat(
        "EEEE MMM dd yyyy",
        Locale.getDefault()
    );

    public static final SimpleDateFormat sTimeFormatter = new SimpleDateFormat(
        "hh:mm a",
        Locale.getDefault()
    );

    /**
     * @param timestamp The requested date
     * @return Formatted string representation of the requested date
     */
    public static String getTimestampAsString(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FMT, Locale.getDefault());
        sCalendar.setTimeInMillis(timestamp);
        return formatter.format(sCalendar.getTime());
    }

    /**
     * @param field The desired Calendar.[FIELD]
     * @param timestamp The timestamp to get the requested field from
     * @return The integer value for the specified field
     */
    public static int getFieldFromTimestamp(int field, long timestamp) {
        sCalendar.setTimeInMillis(timestamp);
        int val = sCalendar.get(field);
        if (field == Calendar.MONTH) {
            val++;
        }
        return val;
    }

    /**
     * @return Today's date as a long timestamp
     */
    public static long getTimestamp() {
        sCalendar.setTimeInMillis(System.currentTimeMillis());
        sCalendar.set(Calendar.HOUR_OF_DAY, 0);
        sCalendar.set(Calendar.MINUTE, 0);
        sCalendar.set(Calendar.SECOND, 0);
        sCalendar.set(Calendar.MILLISECOND, 0);

        return sCalendar.getTimeInMillis();
    }

    /**
     * @return Tonight at midnight in seconds since the epoch
     */
    public static long getMidnight() {
        // 12:01 am tomorrow
        sCalendar.setTimeInMillis(System.currentTimeMillis());
        sCalendar.add(Calendar.DAY_OF_YEAR, 1);
        sCalendar.set(Calendar.HOUR_OF_DAY, 0);
        sCalendar.set(Calendar.MINUTE, 1);
        sCalendar.set(Calendar.SECOND, 0);
        sCalendar.set(Calendar.MILLISECOND, 0);
        return sCalendar.getTimeInMillis();
    }
}

package com.emdevsite.todayhist.utils;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
    private static final String DATE_FMT = "EEEE, MMMM dd";

    public static String getTimestampAsString() {
        long timestamp = getTimestamp();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FMT, Locale.getDefault());
        calendar.setTimeInMillis(timestamp);
        return formatter.format(calendar.getTime());
    }

    public static String getTimestampAsString(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FMT, Locale.getDefault());
        calendar.setTimeInMillis(timestamp);
        return formatter.format(calendar.getTime());
    }

    public static int getFieldFromTimestamp(int field, long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int val = calendar.get(field);
        if (field == Calendar.MONTH) {
            val++;
        }
        return val;
    }

    public static long getTimestamp() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

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

package com.emdevsite.todayhist.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {

    private static final String DATE_FMT = "EEE, MMMM dd";

    public static long getMonth() {
        return new GregorianCalendar().get(Calendar.MONTH);
    }

    public static long getDay() {
        return new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @param month The 01-12 month
     * @param day The 01-31 day
     * @return The given day/month of this year in seconds since the epoch
     */
    public static long getDate(int month, int day) {
        GregorianCalendar c = new GregorianCalendar();
        c.set(c.get(Calendar.YEAR), month, day);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * @return Today as milliseconds since the epoch
     */
    public static long getDate() {
        GregorianCalendar c = new GregorianCalendar();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * @param date The date in milliseconds since the epoch
     * @return Today's date for the current locale as a String in the format EEE, MMMM, dd
     */
    public static String getDateAsStr(long date) {
        return new SimpleDateFormat(DATE_FMT, Locale.getDefault()).format(new Date(date));
    }
}

package com.emdevsite.todayhist.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    private static final String DATE_FMT = "EEE, MMMM dd";

    /**
     * @param month The 01-12 month
     * @param day The 01-31 day
     * @return The given day/month of this year in seconds since the epoch
     */
    public static long getDate(int month, int day) {
        GregorianCalendar c = new GregorianCalendar(TimeZone.getDefault());
        c.set(c.get(Calendar.YEAR), month, day);
        return c.getTimeInMillis();
    }

    /**
     * @return Today as milliseconds since the epoch
     */
    public static long getDate() {
        GregorianCalendar c = new GregorianCalendar(TimeZone.getDefault());
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

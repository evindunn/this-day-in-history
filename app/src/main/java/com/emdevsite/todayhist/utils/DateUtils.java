package com.emdevsite.todayhist.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {
    private static final DateFormat DATE_FORMAT_INT = new SimpleDateFormat("MMdd", Locale.US);
    private static final DateFormat DATE_FORMAT_STR = new SimpleDateFormat("E, MMMM dd", Locale.US);

    /**
     * @return Today's date as an integer, formatted yyyymmdd
     */
    public static int getDateAsInt() {
        try { return Integer.valueOf(DATE_FORMAT_INT.format(Calendar.getInstance().getTime())); }
        catch (Exception e) {
            LogUtils.logError('w', NetworkUtils.class, e);
            return -1;
        }
    }

    /**
     * @return Today's date for the current locale as a String
     */
    public static String getDateAsStr() {
        return DATE_FORMAT_STR.format(Calendar.getInstance().getTime());
    }
}

package com.emdevsite.todayhist.utils;

import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateUtils {
    private static final String DATE_FMT = "EEE, MMMM dd";
    private static GregorianCalendar sCalendar = new GregorianCalendar();

    public static final int JANUARY = 1;
    public static final int FEBRUARY = 2;
    public static final int MARCH = 3;
    public static final int APRIL = 4;
    public static final int MAY = 5;
    public static final int JUNE = 6;
    public static final int JULY = 7;
    public static final int AUGUST = 8;
    public static final int SEPTEMBER = 9;
    public static final int OCTOBER = 10;
    public static final int NOVEMBER = 11;
    public static final int DECEMBER = 12;

    public static final String sJANUARY = "january";
    public static final String sFEBRUARY = "february";
    public static final String sMARCH = "march";
    public static final String sAPRIL = "april";
    public static final String sMAY = "may";
    public static final String sJUNE = "june";
    public static final String sJULY = "july";
    public static final String sAUGUST = "august";
    public static final String sSEPTEMBER = "september";
    public static final String sOCTOBER = "october";
    public static final String sNOVEMBER = "november";
    public static final String sDECEMBER = "december";

    public static int getFieldFromTimestamp(int field, long timestamp) {
        sCalendar.clear();
        sCalendar.setTimeInMillis(timestamp);
        int val = sCalendar.get(field);
        if (field == Calendar.MONTH) {
            val++;
        }
        return val;
    }

    public static long getTimestamp(int month, int day) {
        sCalendar.clear();
        sCalendar.set(Calendar.MONTH, month - 1);
        sCalendar.set(Calendar.DAY_OF_MONTH, day);
        return sCalendar.getTimeInMillis();
    }

    public static int getToday(int field) {
        sCalendar.clear();
        sCalendar.setTimeInMillis(System.currentTimeMillis());
        if (field == Calendar.MONTH) {
            return sCalendar.get(field) + 1;
        } else {
            return sCalendar.get(field);
        }
    }

    /**
     * @param month A String representation of a month
     * @return The int representation of the given month, or -1 if an error occured
     */
    public static int getMonth(String month) {
        switch (month.toLowerCase()) {
            case sJANUARY:
                return JANUARY;
            case sFEBRUARY:
                return FEBRUARY;
            case sMARCH:
                return MARCH;
            case sAPRIL:
                return APRIL;
            case sMAY:
                return MAY;
            case sJUNE:
                return JUNE;
            case sJULY:
                return JULY;
            case sAUGUST:
                return AUGUST;
            case sSEPTEMBER:
                return SEPTEMBER;
            case sOCTOBER:
                return OCTOBER;
            case sNOVEMBER:
                return NOVEMBER;
            case sDECEMBER:
                return DECEMBER;
            default:
                return -1;
        }
    }

    /**
     * @param month The integer representation of a month (1-12)
     * @return The String representation for the given month, or null if an error occured
     */
    @Nullable
    public static String getMonth(int month) {
        switch (month) {
            case JANUARY:
                return sJANUARY;
            case FEBRUARY:
                return sFEBRUARY;
            case MARCH:
                return sMARCH;
            case APRIL:
                return sAPRIL;
            case MAY:
                return sMAY;
            case JUNE:
                return sJUNE;
            case JULY:
                return sJULY;
            case AUGUST:
                return sAUGUST;
            case SEPTEMBER:
                return sSEPTEMBER;
            case OCTOBER:
                return sOCTOBER;
            case NOVEMBER:
                return sNOVEMBER;
            case DECEMBER:
                return sDECEMBER;
            default:
                return null;
        }
    }
}

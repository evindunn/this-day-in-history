package com.emdevsite.todayhist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.emdevsite.todayhist.data.EventDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by edunn on 2/27/18.
 * Various useful static methods
 */

public final class Utils {

    private static final DateFormat DATE_FORMAT_INT = new SimpleDateFormat("yyyyMMdd", Locale.US);
    private static final DateFormat DATE_FORMAT_STR = new SimpleDateFormat("E, MMMM dd", Locale.US);

    /**
     * @param context The current Android context
     * @return Whether there is an internet connection for the current context
     */
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager conn_mg = (ConnectivityManager) context.getSystemService(
            CONNECTIVITY_SERVICE
        );

        if (conn_mg != null) {
            NetworkInfo network = conn_mg.getActiveNetworkInfo();
            return network != null && network.isConnectedOrConnecting();
        }

        return false;
    }

    /**
     * @param cls The offending class
     * @param e The exception to log
     */
    public static void logError(Class cls, Exception e) {
        Log.w(cls.getSimpleName(), String.format("%s: %s", e.getClass(), e.getMessage()));
    }

    /**
     * @return Today's date as an integer, formatted yyyymmdd
     */
    public static int getDateAsInt() {
        try { return Integer.valueOf(DATE_FORMAT_INT.format(Calendar.getInstance().getTime())); }
        catch (Exception e) {
            Utils.logError(Utils.class, e);
            return -1;
        }
    }

    /**
     * @return Today's date for the current locale as a String
     */
    public static String getDate() {
        return DATE_FORMAT_STR.format(Calendar.getInstance().getTime());
    }

    public static int extractInt(String in) {
        return Integer.valueOf(in.replaceAll("\\D+", ""));
    }
}

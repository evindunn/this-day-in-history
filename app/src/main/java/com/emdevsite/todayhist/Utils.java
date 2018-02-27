package com.emdevsite.todayhist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by edunn on 2/27/18.
 * Various useful static methods
 */

class Utils {

    /**
     * @param context The current Android context
     * @return Whether there is an internet connection for the current context
     */
    static boolean checkInternetConnection(Context context) {
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
     * @return Today's date for the current locale
     */
    static String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        Locale locale = Locale.getDefault();
        String day_of_week = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);
        int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format(locale, "%s, %s %d", day_of_week, month, day_of_month);
    }
}

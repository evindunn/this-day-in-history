package com.emdevsite.todayhist.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by edunn on 2/27/18.
 * Various useful static methods
 */

public class NetworkUtils {
    private static final String DOMAIN = "history.muffinlabs.com";

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
     * @param timestamp The date to get the url for
     * @return The url for this day in history
     */
    @Nullable
    public static URL getHistoryUrl(long timestamp) {
        int month = DateUtils.getFieldFromTimestamp(Calendar.MONTH, timestamp);
        int day = DateUtils.getFieldFromTimestamp(Calendar.DAY_OF_MONTH, timestamp);
        Uri uri = getBaseHistoryUri().buildUpon()
                .appendPath("date")
                .appendPath(String.valueOf(month))
                .appendPath(String.valueOf(day))
                .build();

        try {
            LogUtils.logMessage('i', NetworkUtils.class, uri.toString());
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            LogUtils.logError('w', NetworkUtils.class, e);
            return null;
        }
    }

    /**
     * @return The base url for our history data with no specific date
     */
    private static Uri getBaseHistoryUri() {
        return new Uri.Builder()
                .scheme("http")
                .authority(DOMAIN)
                .build();
    }
}

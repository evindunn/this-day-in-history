package com.emdevsite.todayhist.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by edunn on 2/27/18.
 * Various useful static methods
 */

public class NetworkUtils {
    private static final String DOMAIN = "history.muffinlabs.com";
    private static final Uri BASE_HISTORY_URI = getBaseHistoryUri();

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

    @Nullable
    public static URL getHistoryUrl() {
        long today = DateUtils.getDate();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(today);
        Uri uri = BASE_HISTORY_URI.buildUpon()
                .appendPath(String.valueOf(calendar.get(Calendar.MONTH)))
                .appendPath(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            LogUtils.logError('w', NetworkUtils.class, e);
            return null;
        }
    }

    @Nullable
    public static URL getHistoryUrl(long date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(date);
        Uri uri = BASE_HISTORY_URI.buildUpon()
                .appendPath(String.valueOf(calendar.get(Calendar.MONTH)))
                .appendPath(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            LogUtils.logError('w', NetworkUtils.class, e);
            return null;
        }
    }

    private static Uri getBaseHistoryUri() {
        return new Uri.Builder()
                .scheme("http")
                .authority(DOMAIN)
                .build();
    }
}

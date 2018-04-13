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
        int month = DateUtils.getToday(Calendar.MONTH);
        int day = DateUtils.getToday(Calendar.DAY_OF_MONTH);
        return getHistoryUrl(month, day);
    }

    @Nullable
    public static URL getHistoryUrl(int month, int day) {
        Uri uri = BASE_HISTORY_URI.buildUpon()
                .appendPath("date")
                .appendPath(String.valueOf(month))
                .appendPath(String.valueOf(day))
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

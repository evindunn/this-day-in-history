package com.emdevsite.todayhist.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by edunn on 2/27/18.
 * Various useful static methods
 */

public final class NetworkUtils {
    public static final URL BASE_HISTORY_URL = getBaseHistoryUrl();

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
    private static URL getBaseHistoryUrl() {
        try {
            return new URL("http://history.muffinlabs.com");
        } catch (MalformedURLException e) {
            LogUtils.logError('w', NetworkUtils.class, e);
            return null;
        }
    }
}

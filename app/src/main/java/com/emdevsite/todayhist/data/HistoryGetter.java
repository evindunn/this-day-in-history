package com.emdevsite.todayhist.data;

import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by edunn on 2/24/18.
 * Class for retrieving this day in history
 * from <a href="http://history.muffinlabs.com">history.muffinlabs.com</a>
 */

public class HistoryGetter {
    private static final String TAG = HistoryGetter.class.getSimpleName();

    public static JSONObject getJSON(String s_url) {
        URL url;
        JSONObject json_data = null;
        try {
            url = new URL(s_url);
        } catch (MalformedURLException e) {
            Log.w(TAG, getErrorString(e));
            return null;
        }

        try {
            json_data = new JSONObject(pullRawData(url));
        } catch (Exception e) {
            Log.w(TAG, getErrorString(e));
        }

        return json_data;
    }

    private static String pullRawData(URL url) {
        HttpURLConnection connection = null;
        String data = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            InputStream stream = connection.getInputStream();

            Scanner s = new Scanner(stream);
            s.useDelimiter("\\A");

            boolean hasInput = s.hasNext();
            if (hasInput) {
                data = s.next();
            } else {
                data = null;
            }
        } catch (Exception e) {
            Log.w(TAG, String.format("Error connecting to %s", url.toString()));
            Log.w(TAG, getErrorString(e));

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return data;
    }

    private static String getErrorString(Exception e) {
        return String.format("%s: %s", e.getClass(), e.getMessage());
    }
}

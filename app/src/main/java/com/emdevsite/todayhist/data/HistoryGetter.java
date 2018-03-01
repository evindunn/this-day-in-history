package com.emdevsite.todayhist.data;

import android.util.Log;

import com.emdevsite.todayhist.R;
import com.emdevsite.todayhist.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by edunn on 2/24/18.
 * Class for retrieving this day in history
 * from <a href="http://history.muffinlabs.com">history.muffinlabs.com</a>
 */

public class HistoryGetter {
    private static final String TAG = HistoryGetter.class.getSimpleName();
    private static final String KEY_DATA = "data";
    private static final String KEY_EVENTS = "Events";
    public static final String KEY_YEAR = "year";
    public static final String KEY_TEXT = "text";

    public static TreeMap<Integer, HashMap<String, String>> getMap(String s_url) {
        JSONObject json = getJSON(s_url);
        if (json == null) { return null; }

        TreeMap<Integer, HashMap<String, String>> map = new TreeMap<>(
            Collections.<Integer>reverseOrder());

        try {
            // TODO: Remove strings.xml replaced by constants
            JSONArray events = json.getJSONObject(KEY_DATA)
                .getJSONArray(KEY_EVENTS);

            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                if (event.has(KEY_YEAR) && event.has(KEY_TEXT)) {

                    String year = event.getString(KEY_YEAR);
                    String text = event.getString(KEY_TEXT);
                    int year_int = Utils.extractInt(year);

                    HashMap<String, String> inner_map;
                    if (map.containsKey(year_int)) {
                        inner_map = map.get(year_int);
                        inner_map.put(
                            KEY_TEXT,
                            String.format("%s\n\n%s", inner_map.get(KEY_TEXT), text)
                        );
                    } else {
                        inner_map = new HashMap<>();
                        inner_map.put(KEY_YEAR, year);
                        inner_map.put(KEY_TEXT, text);
                    }

                    map.put(year_int, inner_map);
                }
            }
        } catch (Exception e) {
            Log.w(
                HistoryGetter.class.getSimpleName(),
                String.format("%s: %s", e.getClass(), e.getMessage())
            );
            return null;
        }

        return map;
    }

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

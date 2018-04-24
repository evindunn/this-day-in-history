package com.emdevsite.todayhist.data;

import android.content.ContentValues;
import android.support.annotation.Nullable;
import android.util.Log;

import com.emdevsite.todayhist.utils.LogUtils;
import com.emdevsite.todayhist.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by edunn on 2/24/18.
 * Class for retrieving this day in history
 * from <a href="http://history.muffinlabs.com">history.muffinlabs.com</a>
 */
public class HistoryGetter {
    private static final String TAG = HistoryGetter.class.getSimpleName();
    private static final String FIELD_BASE_DATA = "data";
    private static final String FIELD_EVENT_ARRAY = "Events";
    private static final String FIELD_YEAR = "year";
    private static final String FIELD_TEXT = "text";
    private static final String FIELD_URL_ARRAY = "links";
    private static final String FIELD_URL = "link";

    @Nullable
    public static ContentValues[] asContentValues(long timestamp) {
        URL url = NetworkUtils.getHistoryUrl(timestamp);
        String raw_data = pullRawData(url);

        // Grab the array of "Event" json objects
        JSONArray events;
        try {
            events = new JSONObject(raw_data)
                    .getJSONObject(FIELD_BASE_DATA)
                    .getJSONArray(FIELD_EVENT_ARRAY);
        } catch (JSONException e) {
            LogUtils.logError('w', HistoryGetter.class, e);
            return null;
        }

        //

        ArrayList<ContentValues> values = new ArrayList<>();

        for (int i = 0; i < events.length(); i++) {

            ContentValues row = new ContentValues();
            String text;
            String year;
            String sUrl;

            try {
                // Grab event year and text
                year = events.getJSONObject(i).getString(FIELD_YEAR);
                text = events.getJSONObject(i).getString(FIELD_TEXT);

                // Build array of links for the event
                JSONArray links = events.getJSONObject(i).getJSONArray(FIELD_URL_ARRAY);
                StringBuilder sBuilder = new StringBuilder();
                for (int j = 0; j < links.length(); j++) {
                    sBuilder.append(links.getJSONObject(j).getString(FIELD_URL));
                    if (j < links.length() - 1) {
                        sBuilder.append(";");
                    }
                }

                sUrl = sBuilder.toString();

            } catch (JSONException e) {
                LogUtils.logError('w', HistoryGetter.class, e);
                continue;
            }

            boolean replaced = false;
            for (int k = 0; k < values.size(); k++) {
                ContentValues oldVals = values.get(k);
                if (oldVals.getAsString(EventDbContract.EventTable.COLUMN_YEAR).equals(year)) {
                    String oldText = oldVals.getAsString(EventDbContract.EventTable.COLUMN_TEXT);
                    oldVals.put(
                        EventDbContract.EventTable.COLUMN_TEXT,
                        String.format("%s\n\n%s", oldText, text));
                    replaced = true;
                    break;
                }
            }

            if (!replaced) {
                row.put(EventDbContract.EventTable.COLUMN_TIMESTAMP, timestamp);
                row.put(EventDbContract.EventTable.COLUMN_YEAR, year);
                row.put(EventDbContract.EventTable.COLUMN_TEXT, text);
                row.put(EventDbContract.EventTable.COLUMN_URL, sUrl);
                values.add(row);
            }
        }

        if (values.size() == 0) {
            return null;
        }

        return values.toArray(new ContentValues[values.size()]);
    }

    /**
     * Pulls the event data, which muffinlabs hosts as a json
     * @param url The url for the given date
     * @return The raw json data in String form, or null if a problem occured
     */
    public static String pullRawData(URL url) {
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
            LogUtils.logError('w', HistoryGetter.class, e);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return data;
    }
}

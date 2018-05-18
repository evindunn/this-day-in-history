package com.emdevsite.todayhist.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class that represents a task for syncing the Event database
 */
class SyncTask {
    private static final String LOG_TAG = SyncTask.class.getSimpleName();
    private static final String FIELD_BASE_DATA = "data";
    private static final String FIELD_EVENT_ARRAY = "Events";
    private static final String FIELD_YEAR = "year";
    private static final String FIELD_TEXT = "text";

    /**
     * Pulls today's history data from the web and stores it in the Event database (immediately)
     * Should be called as an asynchronous task
     */
    public static synchronized void syncEvents(Context context)
        throws SQLException, IOException, JSONException, NullPointerException {

        URL urlToday = NetworkUtils.getHistoryUrl(DateUtils.getTimestamp());
        String rawData = pullRawData(urlToday);
        ContentValues[] values = asContentValues(DateUtils.getTimestamp(), rawData);

        ContentResolver resolver = context.getContentResolver();
        int deleted = resolver.delete(EventDbContract.EventTable.CONTENT_URI, null, null);
        int inserted = resolver.bulkInsert(EventDbContract.EventTable.CONTENT_URI, values);
        Log.d(LOG_TAG, String.format("Dropped %d stale rows", deleted));
        Log.d(LOG_TAG, String.format("Inserted %d new rows", inserted));
    }

    /**
     * Parses the output of pullRawData() and returns ContentValues for the Event db
     */
    @Nullable
    private static ContentValues[] asContentValues(long timestamp, String jsonStr)
        throws JSONException {

        // Grab the array of "Event" json objects
        JSONArray events;
        events = new JSONObject(jsonStr)
                .getJSONObject(FIELD_BASE_DATA)
                .getJSONArray(FIELD_EVENT_ARRAY);

        ArrayList<ContentValues> values = new ArrayList<>();
        for (int i = 0; i < events.length(); i++) {

            ContentValues row = new ContentValues();

            // Grab event year and text
            String year = events.getJSONObject(i).getString(FIELD_YEAR);
            String text = events.getJSONObject(i).getString(FIELD_TEXT);

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
    private static String pullRawData(URL url) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            int c;
            StringBuilder sb = new StringBuilder();
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }

            return sb.toString();

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

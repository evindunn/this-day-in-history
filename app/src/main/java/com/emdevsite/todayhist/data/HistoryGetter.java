package com.emdevsite.todayhist.data;

import android.content.AsyncTaskLoader;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.emdevsite.todayhist.R;
import com.emdevsite.todayhist.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

    public static ContentValues[] asContentValues(URL url) {
        String raw_data = pullRawData(url);
        JSONArray events;
        try {
            // TODO: Clean up hardcoded values
            events = new JSONObject(raw_data).getJSONObject("data").getJSONArray("Events");
        } catch (JSONException e) {
            Utils.logError(HistoryGetter.class, e);
            return null;
        }

        int date = Utils.getMonthAndDayAsInt();
        ArrayList<ContentValues> outputVals = new ArrayList<>(events.length());

        for (int i = 0; i < events.length(); i++) {

            ContentValues row = new ContentValues();
            String text;
            String year;

            try {
                text = events.getJSONObject(i).getString("text");
                year = events.getJSONObject(i).getString("year");
            } catch (JSONException e) {
                Utils.logError(HistoryGetter.class, e);
                continue;
            }

            row.put(EventDbContract.EventTable.COLUMN_DATE, date);
            row.put(EventDbContract.EventTable.COLUMN_YEAR, year);
            row.put(EventDbContract.EventTable.COLUMN_TEXT, text);

            outputVals.add(row);
        }

        if (outputVals.size() == 0) {
            return null;
        }

        return outputVals.toArray(new ContentValues[outputVals.size()]);
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
            Utils.logError(HistoryGetter.class, e);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return data;
    }
}

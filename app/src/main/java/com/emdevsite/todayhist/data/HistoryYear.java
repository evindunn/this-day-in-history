package com.emdevsite.todayhist.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.emdevsite.todayhist.R;
import com.emdevsite.todayhist.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.TreeSet;

/**
 * Created by edunn on 2/28/18.
 * Class for representing a year with a string & integer representation
 */

public class HistoryYear implements Comparable<HistoryYear> {
    private int asInt;
    private String asString;
    private String data;

    public HistoryYear(String year) {
        try {
            asInt = Utils.extractInt(year);
        } catch (Exception e) {
            asInt = 0;
            Log.w(
                HistoryYear.class.getSimpleName(),
                String.format("%s: %s", e.getClass(), e.getMessage())
            );
        } finally {
            asString = year;
            data = "";
        }
    }

    @Override
    public String toString() { return asString; }
    public int toInt() { return asInt; }

    @Override
    public int compareTo(@NonNull HistoryYear other) {
        if (asString.toLowerCase().contains("bc") && !other.asString.toLowerCase().contains("bc")) {
            return -1;
        }
        if (asInt > other.asInt) {
            return 1;
        } else if (asInt == other.asInt) {
            return 0;
        } else {
            return -1;
        }
    }

    public boolean equals(HistoryYear other) {
        return other != null && asInt == other.asInt && asString.equals(other.asString);
    }

    public void setData(String data) { this.data = data; }
    public String getData() { return data; }

    public static HistoryYear[] parseData(Context context, JSONObject json) {
        TreeSet<HistoryYear> data = new TreeSet<>(Collections.<HistoryYear>reverseOrder());

        try {
            JSONArray events = json.getJSONObject(context.getString(R.string.json_data_key))
                .getJSONArray(context.getString(R.string.json_event_key));

            String jyear_key = context.getString(R.string.json_event_year_key);
            String jtext_key = context.getString(R.string.json_event_text_key);

            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                if (event.has(jyear_key) && event.has(jtext_key)) {

                    String year = event.getString(jyear_key);
                    String text = event.getString(jtext_key);

                    HistoryYear history_year = new HistoryYear(year);

                    if (data.contains(history_year)) {
                        history_year.setData(
                            String.format("%s\n\n%s", history_year.getData(), text)
                        );
                    } else {
                        history_year.setData(text);
                    }

                    data.add(history_year);
                }
            }
        } catch (Exception e) {
            Log.w(
                HistoryYear.class.getSimpleName(),
                String.format("%s: %s", e.getClass(), e.getMessage())
            );
            return null;
        }

        return data.toArray(new HistoryYear[data.size()]);
    }
}

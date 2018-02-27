package com.emdevsite.todayhist;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collection;

/**
 * Created by edunn on 2/26/18.
 * Class representing year with string & integer
 * representation
 */

class YearKey implements Comparable<YearKey> {
    private int asInt;
    private String asStr;

    YearKey(String year) {
        asStr = year;
        try {
            asInt = extractInt(year);
        } catch (Exception e) {
            Log.w(
                getClass().getSimpleName(),
                String.format("%s: %s", e.getClass(), e.getMessage())
            );
            asInt = 0;
        }
    }

    int asInt() { return asInt; }
    String asString() { return asStr; }

    @Override
    public int compareTo(@NonNull YearKey other) {
        if (asInt == other.asInt) { return 0; }
        else if (asInt < other.asInt) { return -1; }
        else { return 1; }
    }

    public boolean equals(YearKey other) {
        return asInt == other.asInt && asStr.equals(other.asStr);
    }

    static String[] toStrings(Collection<YearKey> keys) {
        int len = keys.size();
        String[] ret = new String[len];

        int i = 0;
        for (YearKey key : keys) {
            ret[i] = key.asStr;
            i++;
        }

        return ret;
    }

    private static int extractInt(String in) {
        return Integer.valueOf(in.replaceAll("\\D+", ""));
    }
}

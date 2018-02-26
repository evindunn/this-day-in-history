package com.emdevsite.todayhist;

/**
 * Created by edunn on 2/26/18.
 * Static helper methods
 */

class Utils {
    static int extractInt(String in) {
        return Integer.valueOf(in.replaceAll("\\D+", ""));
    }
}

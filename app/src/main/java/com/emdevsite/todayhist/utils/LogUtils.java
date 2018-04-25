package com.emdevsite.todayhist.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.emdevsite.todayhist.R;

public class LogUtils {

    /**
     * Logs an update in SharedPreferences
     * @param context
     */
    public static synchronized void logUpdate(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putLong(context.getString(R.string.prefs_key_lastUpdate), DateUtils.getTimestamp())
            .apply();
    }

    public static synchronized long getLastUpdate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getLong(context.getString(R.string.prefs_key_lastUpdate), -1);
    }

    /**
     * Logs an exception to the android console
     * @param level The log level for the error
     * @param cls The offending class
     * @param e The exception to log
     */
    public static void logError(char level, Class cls, Exception e) {

        // Log levels in descending order
        switch (level) {
            // Verbose
            case 'v': {
                Log.v(cls.getSimpleName(), String.format("%s: %s", e.getClass(), e.getMessage()));
                break;
            }

            // Debug
            case 'd': {
                Log.d(cls.getSimpleName(), String.format("%s: %s", e.getClass(), e.getMessage()));
                break;
            }

            // Info
            case 'i': {
                Log.i(cls.getSimpleName(), String.format("%s: %s", e.getClass(), e.getMessage()));
                break;
            }

            // Warning
            case 'w': {
                Log.w(cls.getSimpleName(), String.format("%s: %s", e.getClass(), e.getMessage()));
                break;
            }

            // Error
            case 'e': {
                Log.e(cls.getSimpleName(), String.format("%s: %s", e.getClass(), e.getMessage()));
                break;
            }

            default:
                break;
        }
    }

    /**
     * Logs a message to the android console
     * @param level The log level for the error
     * @param cls The class that producted the message
     * @param msg The messsage to log
     */
    public static void logMessage(char level, Class cls, String msg) {

        // Log levels in descending order
        switch (level) {
            // Verbose
            case 'v': {
                Log.v(cls.getSimpleName(), msg);
                break;
            }

            // Debug
            case 'd': {
                Log.d(cls.getSimpleName(), msg);
                break;
            }

            // Info
            case 'i': {
                Log.i(cls.getSimpleName(), msg);
                break;
            }

            // Warning
            case 'w': {
                Log.w(cls.getSimpleName(), msg);
                break;
            }

            // Error
            case 'e': {
                Log.e(cls.getSimpleName(), msg);
                break;
            }

            default:
                break;
        }
    }
}

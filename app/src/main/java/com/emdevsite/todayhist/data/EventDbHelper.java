package com.emdevsite.todayhist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by edunn on 3/12/18.
 * Database for storing today's history events
 */

public class EventDbHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String FILENAME = "events.db";

    public static final String CONTENT_AUTH = "com.emdevsite.todayhist";
    public static final String BASE_CONTENT_PATH = "data";
    public static final String BY_DATE_PATH = "date";
    public static final Uri BASE_CONTENT_URI = new Uri.Builder()
            .scheme("content")
            .authority(CONTENT_AUTH)
            .appendPath(BASE_CONTENT_PATH)
            .build();

    private static final String CREATE_TABLE_FMT =
        "CREATE TABLE %s(%s INTEGER AUTOINCREMENT PRIMARY KEY, %s INTEGER, %s TEXT, %s TEXT);";
    private static final String DELETE_TABLE_FMT =
        "DROP TABLE IF EXISTS %s";

    public EventDbHelper(Context context) {
        super(context, FILENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TBL = String.format(
            CREATE_TABLE_FMT,
            EventDbContract.EventTable.TABLE_NAME,
            EventDbContract.EventTable._ID,
            EventDbContract.EventTable.COLUMN_DATE,
            EventDbContract.EventTable.COLUMN_YEAR,
            EventDbContract.EventTable.COLUMN_TEXT
        );

        db.execSQL(CREATE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format(DELETE_TABLE_FMT, EventDbContract.EventTable.TABLE_NAME));
        onCreate(db);
    }
}

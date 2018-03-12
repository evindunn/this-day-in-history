package com.emdevsite.todayhist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by edunn on 3/12/18.
 * Database for storing today's history events
 */

public class EventDbWrapper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String FILENAME = "events.db";

    private static final String CREATE_TABLE_FMT =
        "CREATE TABLE %s(%s INTEGER AUTOINCREMENT PRIMARY KEY, %s INTEGER, %s TEXT, %s TEXT);";
    private static final String DELETE_TABLE_FMT =
        "DROP TABLE IF EXISTS %s";

    public EventDbWrapper(Context context) {
        super(context, FILENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TBL = String.format(
            CREATE_TABLE_FMT,
            EventDbHelper.EventTable.TABLE_NAME,
            EventDbHelper.EventTable._ID,
            EventDbHelper.EventTable.COLUMN_DATE,
            EventDbHelper.EventTable.COLUMN_YEAR,
            EventDbHelper.EventTable.COLUMN_TEXT
        );

        db.execSQL(CREATE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format(DELETE_TABLE_FMT, EventDbHelper.EventTable.TABLE_NAME));
        onCreate(db);
    }
}

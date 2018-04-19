package com.emdevsite.todayhist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Locale;

/**
 * Created by edunn on 3/12/18.
 * Low-level management of the event database
 */
public class EventDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;   // Increment if db schema is changed
    private static final String FILENAME = "events.db";
    private static final String SQL_DELETE_TBL = String.format(
            "DROP TABLE IF EXISTS %s",
            EventDbContract.EventTable.TABLE_NAME
    );
    private static final String SQL_CREATE_TBL = String.format(
            "CREATE TABLE %s(" +
            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "%s INTEGER NOT NULL, " +
            "%s TEXT UNIQUE NOT NULL, " +
            "%s TEXT NOT NULL" +
            ");",
            EventDbContract.EventTable.TABLE_NAME,
            EventDbContract.EventTable._ID,
            EventDbContract.EventTable.COLUMN_DATE,
            EventDbContract.EventTable.COLUMN_YEAR,
            EventDbContract.EventTable.COLUMN_TEXT
    );

    EventDbHelper(Context context) {
        super(context, FILENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TBL);
        onCreate(db);
    }
}

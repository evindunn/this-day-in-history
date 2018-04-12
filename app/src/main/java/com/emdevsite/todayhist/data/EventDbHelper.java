package com.emdevsite.todayhist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by edunn on 3/12/18.
 * Low-level management of the event database
 */
public class EventDbHelper extends SQLiteOpenHelper {

    public static final String FILENAME = "events.db";
    private static final int VERSION = 1;   // Increment if db schema is changed

    public EventDbHelper(Context context) {
        super(context, FILENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_FMT =
                "CREATE TABLE %s(%s INTEGER AUTOINCREMENT PRIMARY KEY, %s INTEGER NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL);";

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
        final String DELETE_TABLE_FMT =
                "DROP TABLE IF EXISTS %s";

        db.execSQL(String.format(DELETE_TABLE_FMT, EventDbContract.EventTable.TABLE_NAME));
        onCreate(db);
    }
}

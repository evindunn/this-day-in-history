package com.emdevsite.todayhist.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.emdevsite.todayhist.utils.LogUtils;

//TODO: Clean up
public class EventProvider extends ContentProvider {
    private static final int CODE_ALL = 0;
    private static final int CODE_DATE = 1;
    private static final String FMT_QUERY_SELECTION = "%s = ?";
    private static final String FMT_URI_ERR = "Invalid URI: %s";

    private EventDbHelper mDbHelper;
    private static final UriMatcher sURI_MATCHER = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mDbHelper = new EventDbHelper(getContext());
        return true;
    }

    /**
     * Queries the database. Only URI is used, with its format dictating
     * query params
     * @param uri content://com.emdevsite.todayhist/data/[selection]/[selection arg]
     * @param projection unused
     * @param selection unused
     * @param selectionArgs unused
     * @param sortOrder unused
     * @return A cursor containing the results, always formatted by date
     */
    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {

        int match = sURI_MATCHER.match(uri);
        Cursor cursor;

        switch (match) {

            // Whole table
            case CODE_ALL: {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                cursor = db.query(
                        EventDbContract.EventTable.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        EventDbContract.EventTable.COLUMN_DATE
                );
                break;
            }

            // Rows with a specific date
            case CODE_DATE: {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String date = uri.getLastPathSegment();
                cursor = db.query(
                        EventDbContract.EventTable.TABLE_NAME,
                        projection,
                        String.format(FMT_QUERY_SELECTION, EventDbContract.EventTable.COLUMN_DATE),
                        new String[] { date },
                        null,
                        null,
                        EventDbContract.EventTable.COLUMN_DATE
                );
                break;
            }

            default: {
                throw new IllegalArgumentException(String.format(FMT_URI_ERR, uri));
            }
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Inserts all given values into the table at the given uri
     * @param uri content://com.emdevsite/date
     * @param values The values to insert
     * @return The number of rows inserted
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int inserted = 0;
        switch (sURI_MATCHER.match(uri)) {
            // Only accepted uri is the whole table
            case CODE_ALL: {
                for (ContentValues value : values) {
                    if (insert(uri, value) != null) {
                        inserted++;
                    }
                }
                return inserted;
            }

            default: {
                return super.bulkInsert(uri, values);
            }
        }
    }

    /**
     * Deletes rows from the table @ the given uri matching the given seletion / selection args
     * @param uri content://com.emdevsite.todayhist/date
     * @param selection unused
     * @param selectionArgs unused
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sURI_MATCHER.match(uri);
        int deleted;
        switch (match) {
            case CODE_ALL: {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();

                // If we pass null, it'll delete all the rows, but won't return the number of
                // rows by default; If we pass "1" it'll do both
                if (selection == null) {
                    selection = "1";
                }

                db.beginTransaction();
                try {
                    deleted = db.delete(
                            EventDbContract.EventTable.TABLE_NAME,
                            selection,
                            selectionArgs
                    );
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                break;
            }

            default: {
                throw new IllegalArgumentException(String.format(FMT_URI_ERR, uri));
            }
        }

        if (deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    /**
     * @param uri The Uri for the requested data
     * @return The MIME type for the data at the given uri
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("EventProvider.getType() isn't implemented yet");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sURI_MATCHER.match(uri);
        Uri rUri;

        switch (match) {
            case CODE_ALL: {
                db.beginTransaction();
                try {
                    long id = db.insert(
                            EventDbContract.EventTable.TABLE_NAME,
                            null,
                            values
                    );

                    if (id == -1) {
                        LogUtils.logMessage('w', getClass(), "Error inserting ContentValues into db");
                    }

                    db.setTransactionSuccessful();
                    rUri = EventDbContract.EventTable.buildUriWithDate(
                            values.getAsLong(EventDbContract.EventTable.COLUMN_DATE)
                    );
                }
                finally {
                    db.endTransaction();
                }

                return rUri;
            }

            default: {
                throw new IllegalArgumentException(String.format(FMT_URI_ERR, uri));
            }
        }
    }

    @Override
    public int update(
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs) {

        throw new RuntimeException("EventProvider.update() isn't implemented yet");
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(EventDbContract.AUTHORITY, EventDbContract.PATH_DATE, CODE_ALL);
        matcher.addURI(
                EventDbContract.AUTHORITY,
                EventDbContract.PATH_DATE + "/#",
                CODE_DATE
        );
        return matcher;
    }
}

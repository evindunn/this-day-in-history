package com.emdevsite.todayhist.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class EventProvider extends ContentProvider {
    private static final int CODE_EVENTS = 0;
    private static final String ERR_FMT_URI = "Invalid uri: %s";
    private static final UriMatcher sUriMatcher = createUriMatcher();

    private EventDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new EventDbHelper(getContext());
        return true;
    }

    /**
     * @param uri The Event DB uri
     * @param projection The requested DB columns
     * @param selection The DB query string
     * @param selectionArgs The DB query args
     * @param sortOrder The sort order for the results
     * @return A Cursor containing the results, or null
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTS: {
                try {
                    Cursor results = mDbHelper.getReadableDatabase().query(
                        EventDbContract.EventTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                    );
                    results.setNotificationUri(
                        getContext().getContentResolver(),
                        EventDbContract.EventTable.CONTENT_URI
                    );
                    return results;
                } catch (SQLException e) {
                    String className = getClass().getSimpleName();
                    Log.w(className, "Error retrieving db values");
                    Log.w(className, e.getMessage());
                    return null;
                }
            }

            default: { throw new IllegalArgumentException(String.format(ERR_FMT_URI, uri)); }
        }
    }


    /**
     * @param uri The Event DB uri
     * @param values The values to insert
     * @return The DB uri if successful & values were inserted, null if not
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTS: {
                Uri result = null;
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    long inserted = db.insertWithOnConflict(
                        EventDbContract.EventTable.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                    );

                    if (inserted > 0) {
                        db.setTransactionSuccessful();
                        result = uri;
                        getContext().getContentResolver().notifyChange(
                            EventDbContract.EventTable.CONTENT_URI,
                            null
                        );
                    }
                } catch (SQLException e) {
                    String className = getClass().getSimpleName();
                    Log.w(className, "Error inserting db values");
                    Log.w(className, e.getMessage());

                } finally {
                    db.endTransaction();
                }

                return result;
            }

            default: { throw new IllegalArgumentException(String.format(ERR_FMT_URI, uri)); }
        }
    }

    /**
     * @param uri The Event DB uri
     * @param selection The DB query string for rows to delete
     * @param selectionArgs The DB query args
     * @return The number of successfully deleted rows
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTS: {
                int deleted = 0;
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    deleted = db.delete(
                        EventDbContract.EventTable.TABLE_NAME,
                        selection,
                        selectionArgs
                    );

                    if (deleted > 0) {
                        db.setTransactionSuccessful();
                        getContext().getContentResolver().notifyChange(
                            EventDbContract.EventTable.CONTENT_URI,
                            null
                        );
                    }

                } catch (SQLException e) {
                    String className = getClass().getSimpleName();
                    Log.w(className, "Error deleting db values");
                    Log.w(className, e.getMessage());
                } finally {
                    db.endTransaction();
                }

                return deleted;
            }

            default: { throw new IllegalArgumentException(String.format(ERR_FMT_URI, uri)); }
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("EventProvider.update() isn't implemented");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("EventProvider.getType() isn't implemented");
    }

    /**
     * @return A new URI matcher for the Event DB
     */
    private static UriMatcher createUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(EventDbContract.AUTHORITY, EventDbContract.PATH_EVENTS, CODE_EVENTS);
        return matcher;
    }
}

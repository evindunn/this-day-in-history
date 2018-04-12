package com.emdevsite.todayhist.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// TODO: This could use another look/cleanup
public class EventProvider extends ContentProvider {
    private static final int URI_ALL = 0;
    private static final int URI_DATE = 1;
    private static final String FMT_QUERY_SELECTION = "WHERE %s = ?";
    private static final String FMT_URI_ERR = "Invalid URI: %s";

    private EventDbHelper mDbHelper;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

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

        int match = URI_MATCHER.match(uri);
        Cursor cursor = null;

        switch (match) {
            case URI_ALL: {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                cursor = db.query(
                        EventDbContract.EventTable.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        EventDbContract.EventTable.COLUMN_DATE
                );
                break;
            }

            case URI_DATE: {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String date = uri.getPathSegments().get(1);
                cursor = db.query(
                        EventDbContract.EventTable.TABLE_NAME,
                        null,
                        String.format(FMT_QUERY_SELECTION, "date"),
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

        return cursor;
    }
    /**
     * Adds a new row with the given contentvalues at the given uri
     * @param uri content://com.emdevsite.todayhist/data/[selection]/[selection args]
     * @param values The column-value pairs
     * @return This should be the same uri that was given as a parameter
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_ALL: {
                throw new IllegalArgumentException("Must specify a more specific path");
            }

            case URI_DATE: {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                long rowid = db.insert(
                        EventDbContract.EventTable.TABLE_NAME,
                        null,
                        values
                );

                if (rowid == -1) {
                    throw new IllegalStateException(
                            String.format("Could not insert row at %s", uri)
                    );
                }

                return uri;
            }

            default: {
                throw new IllegalArgumentException(String.format(FMT_URI_ERR, uri));
            }
        }
    }

    /**
     * Inserts all given values into the table at the given uri
     * @param uri content://com.emdevsite/data
     * @param values The values to insert
     * @return The number of rows inserted
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int inserted = 0;
        for (ContentValues vals : values) {
            String date = vals.getAsString("date");
            Uri newRowUri = EventDbHelper.BASE_CONTENT_URI.buildUpon()
                    .appendPath(EventDbHelper.BASE_CONTENT_PATH)
                    .appendPath(EventDbHelper.BY_DATE_PATH)
                    .appendPath(date)
                    .build();
            // We don't have to check the ret val, insert() throws an exception if its not
            // successful
            insert(newRowUri, vals);
            inserted++;
        }
        return inserted;
    }

    /**
     * Deletes rows matching the given uri pattern
     * @param uri content://com.emdevsite.todayhist/data/[selection]/[selection args]
     * @param selection unused
     * @param selectionArgs unused
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        int deleted;
        switch (match) {
            case URI_ALL: {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                deleted = db.delete(
                        EventDbContract.EventTable.TABLE_NAME,
                        null,
                        null
                );
                break;
            }

            case URI_DATE: {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String date = uri.getPathSegments().get(1);
                deleted = db.delete(
                        EventDbContract.EventTable.TABLE_NAME,
                        String.format(FMT_QUERY_SELECTION, "date"),
                        new String[] { date }
                );
                break;
            }

            default: {
                throw new IllegalArgumentException(String.format(FMT_URI_ERR, uri));
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return deleted;
    }

    /**
     * Updates the row(s) matching the criteria given in the uri
     * @param uri content://com.emdevsite.todayhist/data/[selection]/[selection args]
     * @param values The new values for the row
     * @param selection unused
     * @param selectionArgs unused
     * @return The number of rows updated; Will usually be 1
     */
    @Override
    public int update(
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs) {

        int match = URI_MATCHER.match(uri);
        int updated;
        switch (match) {
            case URI_ALL: {
                updated = 0;
                break;
            }

            case URI_DATE: {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                String date = uri.getPathSegments().get(1);
                updated = db.update(
                        EventDbContract.EventTable.TABLE_NAME,
                        values,
                        String.format(FMT_QUERY_SELECTION, "date"),
                        new String[] { date }
                );
                break;
            }

            default: {
                throw new IllegalArgumentException(String.format(FMT_URI_ERR, uri));
            }
        }

        return updated;
    }

    /**
     * @param uri The Uri for the requested data
     * @return The MIME type for the data at the given uri
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_ALL: {
                // TODO: This returns a whole table, how is that even represented in MIME?
                return null;
            }

            case URI_DATE: {
                return "INTEGER";
            }

            default: {
                throw new IllegalArgumentException(String.format(FMT_URI_ERR, uri));
            }
        }
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(EventDbHelper.CONTENT_AUTH, EventDbHelper.BASE_CONTENT_PATH, URI_ALL);
        matcher.addURI(
                EventDbHelper.CONTENT_AUTH,
                EventDbHelper.BASE_CONTENT_PATH + EventDbHelper.BY_DATE_PATH + "/#",
                URI_DATE
        );
        return matcher;
    }
}

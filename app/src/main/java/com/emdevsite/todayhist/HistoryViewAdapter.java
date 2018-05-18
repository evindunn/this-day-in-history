package com.emdevsite.todayhist;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.utils.DateUtils;

/**
 * Created by edunn on 2/27/18.
 * Class for providing a new HistoryFragment to the user following a swipe
 */

class HistoryViewAdapter extends FragmentStatePagerAdapter {
    private Cursor cursor;

    HistoryViewAdapter(FragmentManager fm) {
        super(fm);
        cursor = null;
    }

    public void swapCursor(@Nullable Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
    
    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new HistoryFragment();
        try {
            Bundle args = new Bundle();
            cursor.moveToPosition(i);

            int year_col = cursor.getColumnIndex(EventDbContract.EventTable.COLUMN_YEAR);
            int text_col = cursor.getColumnIndex(EventDbContract.EventTable.COLUMN_TEXT);
            int tstamp_col = cursor.getColumnIndex(EventDbContract.EventTable.COLUMN_TIMESTAMP);

            args.putString(EventDbContract.EventTable.COLUMN_YEAR, cursor.getString(year_col));
            args.putString(EventDbContract.EventTable.COLUMN_TEXT, cursor.getString(text_col));
            args.putString(
                EventDbContract.EventTable.COLUMN_TIMESTAMP,
                DateUtils.getTimestampAsString(cursor.getLong(tstamp_col))
            );

            fragment.setArguments(args);

        } catch (Exception e) {
            Log.e(
                getClass().getSimpleName(),
                String.format("[%s] %s", e.getCause(), e.getMessage())
            );
        }
        return fragment;
    }

    @Override
    public int getCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        // Fragements should always be rebuilt when notifyDatasetChanged() is called
        return POSITION_NONE;
    }
}
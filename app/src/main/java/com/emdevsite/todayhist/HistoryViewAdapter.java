package com.emdevsite.todayhist;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.emdevsite.todayhist.HistoryFragment;
import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.utils.LogUtils;

/**
 * Created by edunn on 2/27/18.
 * Class for providing a new HistoryFragment to the user following a swipe
 */

public class HistoryViewAdapter extends FragmentStatePagerAdapter {
    int count;
    Cursor cursor;

    HistoryViewAdapter(FragmentManager fm) {
        super(fm);
        cursor = null;
        count = 0;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new HistoryFragment();
        try {
            Bundle args = new Bundle();

            cursor.moveToPosition(i);

            int text_col = cursor.getColumnIndex(EventDbContract.EventTable.COLUMN_TEXT);
            args.putString("text", cursor.getString(text_col));

            fragment.setArguments(args);

        } catch (Exception e) {
            LogUtils.logError('w', getClass(), e);
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
}
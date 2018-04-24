package com.emdevsite.todayhist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.data.HistoryGetter;
import com.emdevsite.todayhist.databinding.ActivityMainBinding;

import java.util.Calendar;
import java.util.Locale;


/**
 * Fragment for displaying history event data
 */
public class HistoryFragment extends Fragment {
    private String mYear;
    private String mText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null &&
            savedInstanceState.containsKey(EventDbContract.EventTable.COLUMN_YEAR) &&
            savedInstanceState.containsKey(EventDbContract.EventTable.COLUMN_TEXT)) {

            mYear = savedInstanceState.getString(EventDbContract.EventTable.COLUMN_YEAR);
            mText = savedInstanceState.getString(EventDbContract.EventTable.COLUMN_TEXT);
        } else {
            Bundle args = getArguments();
            if (args != null &&
                args.containsKey(EventDbContract.EventTable.COLUMN_TEXT) &&
                args.containsKey(EventDbContract.EventTable.COLUMN_YEAR)) {

                mYear = args.getString(EventDbContract.EventTable.COLUMN_YEAR);
                mText = args.getString(EventDbContract.EventTable.COLUMN_TEXT);
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root_view = inflater.inflate(R.layout.fragment_history, container, false);
        TextView year_view = root_view.findViewById(R.id.tv_year);
        TextView text_view = root_view.findViewById(R.id.tv_history);

        year_view.setText(mYear);
        text_view.setText(mText);

        return root_view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(
            EventDbContract.EventTable.COLUMN_YEAR,
            mYear
        );

        outState.putString(
            EventDbContract.EventTable.COLUMN_TEXT,
            mText
        );

        super.onSaveInstanceState(outState);
    }
}

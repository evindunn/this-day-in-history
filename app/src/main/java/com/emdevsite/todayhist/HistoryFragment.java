package com.emdevsite.todayhist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.data.HistoryGetter;

import java.util.Calendar;
import java.util.Locale;


/**
 * Fragment for displaying history event data
 */
public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root_view = inflater.inflate(R.layout.fragment_history, container, false);

        TextView year_view = root_view.findViewById(R.id.tv_year);
        TextView data_view = root_view.findViewById(R.id.tv_history);

        Bundle args = getArguments();
        if (args != null &&
                args.containsKey(EventDbContract.EventTable.COLUMN_TEXT) &&
                args.containsKey(EventDbContract.EventTable.COLUMN_YEAR)) {

            year_view.setText(args.getString(EventDbContract.EventTable.COLUMN_YEAR));
            data_view.setText(args.getString(EventDbContract.EventTable.COLUMN_TEXT));
        }
        return root_view;
    }
}

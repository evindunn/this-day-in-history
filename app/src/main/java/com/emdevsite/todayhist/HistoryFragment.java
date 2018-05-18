package com.emdevsite.todayhist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emdevsite.todayhist.data.EventDbContract;


/**
 * Fragment for displaying history event data
 */
public class HistoryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root_view = inflater.inflate(R.layout.fragment_history, container, false);
        TextView date_view = root_view.findViewById(R.id.tv_date);
        TextView year_view = root_view.findViewById(R.id.tv_year);
        TextView text_view = root_view.findViewById(R.id.tv_history);
        ImageView bg_view = root_view.findViewById(R.id.iv_background);

        Bundle args = getArguments();
        if (args != null &&
            args.containsKey(EventDbContract.EventTable.COLUMN_TEXT) &&
            args.containsKey(EventDbContract.EventTable.COLUMN_YEAR) &&
            args.containsKey(EventDbContract.EventTable.COLUMN_TIMESTAMP)) {

            String date = args.getString(EventDbContract.EventTable.COLUMN_TIMESTAMP);
            String year = args.getString(EventDbContract.EventTable.COLUMN_YEAR);
            String text = args.getString(EventDbContract.EventTable.COLUMN_TEXT);

            date_view.setText(date);
            year_view.setText(year);
            text_view.setText(text);
        }

        return root_view;
    }
}

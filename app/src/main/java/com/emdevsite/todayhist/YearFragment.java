package com.emdevsite.todayhist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emdevsite.todayhist.data.EventDbContract;


/**
 * Fragment for displaying history event data
 */
public class YearFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root_view = inflater.inflate(R.layout.fragment_year, container, false);
        TextView data_view = root_view.findViewById(R.id.tv_year);
        Bundle args = getArguments();

        // TODO: Use strings.xml
        if (args != null && args.containsKey(EventDbContract.EventTable.COLUMN_YEAR)) {
            data_view.setText(args.getString(EventDbContract.EventTable.COLUMN_YEAR));
        }

        return root_view;
    }
}

package com.emdevsite.todayhist;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Loader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.emdevsite.todayhist.data.HistoryGetter;

import java.net.URL;
import java.util.HashMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String S_URL = "http://history.muffinlabs.com/date";

    TreeMap<Integer, HashMap<String, String>> history_data;
    Integer[] history_keys;

    private ViewPager view_pager;
    private HistoryViewAdapter view_adapter;
    private Button b_year;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view_pager = findViewById(R.id.view_pager);
        view_adapter = new HistoryViewAdapter(getSupportFragmentManager());
        view_pager.setAdapter(view_adapter);

        progress_bar = findViewById(R.id.progress_bar);
        b_year = findViewById(R.id.b_year);
        b_year.setOnClickListener(this);

        // TODO: Set the year when page is swiped
        view_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                super.onPageSelected(i);
            }
        });

        Toast.makeText(this, R.string.swipe_hint, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getTitle().equals(Utils.getDate())) {
            refresh();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mi_license) {
            // TODO: DialogFragment
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.license_content)
                    .setNeutralButton(
                            R.string.license_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }
                    )
                    .create()
                    .show();
        } else if (item.getItemId() == R.id.mi_refresh) {
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.b_year) {
            showYearDialog();
        }
    }

    // TODO: Show year dialog, or replace with drawer
    private void showYearDialog() {

    }

    private void refresh() {
        setTitle(Utils.getDate());
        history_data = null;

        if (Utils.checkInternetConnection(this)) {
            new GetHistoryTask().execute();
        } else {
            Toast.makeText(
                    this,
                    R.string.connect_err,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Created by edunn on 2/27/18.
     * Class for providing a new HistoryFragment to the user following a swipe
     */

    class HistoryViewAdapter extends FragmentStatePagerAdapter {
        int count;

        HistoryViewAdapter(FragmentManager fm) {
            super(fm);
            count = 0;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new HistoryFragment();
            try {
                Bundle args = new Bundle();
                int key = history_keys[i];
                // TODO: Put putString(event text) in fragment
                fragment.setArguments(args);
            } catch (Exception e) {
                Log.w(getLocalClassName(), String.format("%s: %s", e.getClass(), e.getMessage()));
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return count;
        }
        void setCount(int count) {
            this.count = count;
        }
    }

    // TODO: Replace
    private class GetHistoryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            b_year.setVisibility(View.INVISIBLE);
            view_pager.setVisibility(View.INVISIBLE);
            progress_bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // TODO: GetHistoryTask.doInBackground()
            history_data = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            super.onPostExecute(nothing);
            if (history_data != null) {
                view_adapter.setCount(history_data.size());
                // TODO: Set the year
                b_year.setVisibility(View.VISIBLE);
                view_pager.setVisibility(View.VISIBLE);
            } else {
                view_adapter.setCount(1);
            }
            view_adapter.notifyDataSetChanged();
            progress_bar.setVisibility(View.INVISIBLE);
        }
    }
}

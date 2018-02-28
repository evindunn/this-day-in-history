package com.emdevsite.todayhist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String S_URL = "http://history.muffinlabs.com/date";

    private TreeMap<YearKey, String> history_data;
    private YearKey[] history_keys;

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

        view_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                super.onPageSelected(i);
                b_year.setText(history_keys[i].asString());
            }
        });

        Toast.makeText(this, R.string.swipe_hint, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getTitle().equals(Utils.getTodaysDate())) {
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

    // TODO: This is awful, Drawer instead
    private void showYearDialog() {
        final String[] s_keys = YearKey.toStrings(history_data.keySet());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.year)
            .setItems(s_keys, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    view_pager.setCurrentItem(i);
                }
            });
        builder.create().show();
    }

    private void refresh() {
        setTitle(Utils.getTodaysDate());

        if (history_data != null) {
            history_data.clear();
        } else {
            history_data = new TreeMap<>(Collections.<YearKey>reverseOrder());
        }
        history_keys = null;

        if (Utils.checkInternetConnection(this)) {
            new GetHistoryTask().execute(S_URL);
        } else {
            Toast.makeText(
                    this,
                    R.string.connect_err,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void parseData(JSONObject json) {
        try {
            JSONArray events = json.getJSONObject(getString(R.string.json_data_key))
                    .getJSONArray(getString(R.string.json_event_key));
            String jyear_key = getString(R.string.json_event_year_key);
            String jtext_key = getString(R.string.json_event_text_key);
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                if (event.has(jyear_key) && event.has(jtext_key)) {
                    YearKey year = new YearKey(event.getString(jyear_key));
                    String text = event.getString(jtext_key);
                    if (history_data.containsKey(year)) {
                        String data = history_data.get(year);
                        history_data.put(year, String.format("%s\n\n%s", data, text));
                    } else {
                        history_data.put(year, text);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                // TODO: Use strings.xml
                args.putString("data", history_data.get(history_keys[i]));
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
    private class GetHistoryTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            b_year.setText("");
            b_year.setVisibility(View.INVISIBLE);
            view_pager.setVisibility(View.INVISIBLE);

            progress_bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            if (strings.length > 0 && strings[0] != null) {
                return HistoryGetter.getJSON(strings[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            parseData(jsonObject);
            if (history_data != null) {
                Set<YearKey> keys = history_data.keySet();
                history_keys = keys.toArray(new YearKey[keys.size()]);
                view_adapter.setCount(history_keys.length);
                b_year.setText(history_keys[0].asString());
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

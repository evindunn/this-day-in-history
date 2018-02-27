package com.emdevsite.todayhist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

    private TreeMap<YearKey, ArrayList<String>> history_data;
    private YearKey current_year;

    private TextView tv_history;
    private Button b_year;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b_year = findViewById(R.id.b_year);
        tv_history = findViewById(R.id.tv_history);
        progress_bar = findViewById(R.id.progress_bar);

        Button b_prev = findViewById(R.id.b_prev);
        Button b_next = findViewById(R.id.b_next);

        b_year.setOnClickListener(this);
        b_prev.setOnClickListener(this);
        b_next.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getTitle().equals(getTodaysDate())) {
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

    private void refresh() {
        setTitle(getTodaysDate());

        if (history_data != null) {
            history_data.clear();
        } else {
            history_data = new TreeMap<>(Collections.<YearKey>reverseOrder());
        }

        if (checkInternetConnection(this)) {
            new GetHistoryTask().execute(S_URL);
        } else {
            Toast.makeText(
                    this,
                    R.string.connect_err,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private static boolean checkInternetConnection(Context context) {
        ConnectivityManager conn_mg = (ConnectivityManager) context.getSystemService(
                CONNECTIVITY_SERVICE
        );

        if (conn_mg != null) {
            NetworkInfo network = conn_mg.getActiveNetworkInfo();
            return network != null && network.isConnectedOrConnecting();
        }

        return false;
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
                    if (history_data.containsKey(year)) {
                        ArrayList<String> text = history_data.get(year);
                        text.add(event.getString(jtext_key));
                    } else {
                        ArrayList<String> text = new ArrayList<>();
                        text.add(event.getString(jtext_key));
                        history_data.put(year, text);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        Locale locale = Locale.getDefault();
        String day_of_week = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);
        int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format(locale, "%s, %s %d", day_of_week, month, day_of_month);
    }

    private void prevYear() {
        if (current_year.equals(history_data.firstKey())) {
            current_year = history_data.lastKey();
        } else {
            current_year = history_data.lowerKey(current_year);
        }
        textRefresh();
    }

    private void nextYear() {
        if (current_year.equals(history_data.lastKey())) {
            current_year = history_data.firstKey();
        } else {
            current_year = history_data.higherKey(current_year);
        }
        textRefresh();
    }

    private void textRefresh() {
        b_year.setText(current_year.asString());
        tv_history.setText("");
        for (String event : history_data.get(current_year)) {
            tv_history.append(event + "\n\n");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.b_next) {
            nextYear();
        } else if (view.getId() == R.id.b_prev) {
            prevYear();
        } else if (view.getId() == R.id.b_year) {
            showYearDialog();
        }
    }

    // TODO: This is awful, use DialogFragment
    private void showYearDialog() {
        final YearKey[] keys = history_data.keySet()
            .toArray(new YearKey[history_data.keySet().size()]);
        final String[] s_keys = YearKey.toStrings(history_data.keySet());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.year)
                .setItems(s_keys, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        current_year = keys[i];
                        textRefresh();
                    }
                });
        builder.create().show();
    }

    private class GetHistoryTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            b_year.setText("");
            tv_history.setText("");

            b_year.setVisibility(View.INVISIBLE);
            tv_history.setVisibility(View.INVISIBLE);
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
                current_year = history_data.firstKey();
                textRefresh();
            }
            progress_bar.setVisibility(View.INVISIBLE);
            b_year.setVisibility(View.VISIBLE);
            tv_history.setVisibility(View.VISIBLE);
        }
    }
}

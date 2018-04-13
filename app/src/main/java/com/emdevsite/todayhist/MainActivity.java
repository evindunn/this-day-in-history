package com.emdevsite.todayhist;

import android.app.AlertDialog;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.content.DialogInterface;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.data.EventDbHelper;
import com.emdevsite.todayhist.sync.SyncUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPager view_pager;
    private HistoryViewAdapter view_adapter;
    private Button b_year;
    private ProgressBar progress_bar;

    private static final int ID_LOADER_EVENTS = 14;
    private static final String[] DB_PROJECTION = new String[] {
            EventDbContract.EventTable.COLUMN_YEAR,
            EventDbContract.EventTable.COLUMN_TEXT
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view_pager = findViewById(R.id.view_pager);
        view_adapter = new HistoryViewAdapter(getSupportFragmentManager());
        view_pager.setAdapter(view_adapter);

        progress_bar = findViewById(R.id.progress_bar);
        b_year = findViewById(R.id.b_year);

        view_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                super.onPageSelected(i);
            }
        });

        // TODO: Temporary for db testing
        SyncUtils.syncNow(this);
        getSupportLoaderManager().initLoader(ID_LOADER_EVENTS, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
            // TODO: Refresh
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle args) {
        switch (loader_id) {
            case ID_LOADER_EVENTS: {
                Uri events_uri = EventDbContract.EventTable.CONTENT_URI;
                String sort_order = EventDbContract.EventTable.COLUMN_DATE + " DESC";

                return new CursorLoader(
                        this,
                        events_uri,
                        EventDbHelper.EVENT_SELECTION,
                        null,
                        null,
                        sort_order
                );
            }
            default:
                throw new RuntimeException("Requested loader is not implemented");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        view_adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

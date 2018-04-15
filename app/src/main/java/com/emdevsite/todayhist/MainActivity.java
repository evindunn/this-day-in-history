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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.data.EventDbHelper;
import com.emdevsite.todayhist.sync.SyncUtils;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;
import com.emdevsite.todayhist.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPager mViewPager;
    private HistoryViewAdapter mViewAdapter;
    private Button mYearButton;
    private ProgressBar mProgressBar;

    private static final int ID_LOADER_EVENTS = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.view_pager);
        mViewAdapter = new HistoryViewAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewAdapter);

        mProgressBar = findViewById(R.id.progress_bar);
        mYearButton = findViewById(R.id.b_year);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                super.onPageSelected(i);
                Cursor cursor = mViewAdapter.getCursor();
                cursor.moveToPosition(i);
                int idx = cursor.getColumnIndex(EventDbContract.EventTable.COLUMN_YEAR);
                mYearButton.setText(cursor.getString(idx));
            }
        });

        // TODO: Temporary for db testing
        getSupportLoaderManager().initLoader(ID_LOADER_EVENTS, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle args) {
        switch (loader_id) {
            case ID_LOADER_EVENTS: {
                LogUtils.logMessage('i', getClass(), "Loading...");
                showProgressBar(true);
                return new CursorLoader(
                        this,
                        EventDbContract.EventTable.CONTENT_URI,
                        null,
                        String.format("%s = ?", EventDbContract.EventTable.COLUMN_DATE),
                        new String[] { String.valueOf(DateUtils.getTimestamp()) },
                        EventDbContract.EventTable.COLUMN_DATE + " DESC"
                );
            }
            default: {
                throw new RuntimeException("Requested loader is not implemented");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        showProgressBar(false);
        if (data != null && data.getCount() > 0) {
            LogUtils.logMessage('i', getClass(), "Load finished.");
            mViewAdapter.swapCursor(data);
        } else {
            LogUtils.logMessage('i', getClass(), "Load returned no results.");
        }
    }

    public void showProgressBar(boolean visible) {
        if (visible) {
            mViewPager.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
        }
    }
}

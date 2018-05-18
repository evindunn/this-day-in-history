package com.emdevsite.todayhist;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.sync.FirebaseSyncJobService;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.NetworkUtils;
import com.emdevsite.todayhist.utils.SyncUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private HistoryViewAdapter mHistoryViewAdapter;
    private ProgressBar mProgressBar;
    private ViewPager mViewPager;
    private TextView mErrorView;

    private static final int ID_LOADER_EVENTS = 14;
    private static final String KEY_CURRENT_PAGE = "page";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Creates a new MainActivity for This Day in History
     * @param savedInstanceState Saved instance of last usage of the app, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up members
        mProgressBar = findViewById(R.id.progress_bar);
        mViewPager = findViewById(R.id.view_pager);
        mErrorView = findViewById(R.id.error_view);

        mHistoryViewAdapter = new HistoryViewAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mHistoryViewAdapter);

        // Initialize sync & start the loader
        SyncUtils.initialize(this);
        getSupportLoaderManager().initLoader(ID_LOADER_EVENTS, null, this);
    }

    /**
     * Save the current ViewPager page
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(KEY_CURRENT_PAGE, mViewPager.getCurrentItem());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * Restore the last ViewPager page
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(KEY_CURRENT_PAGE)) {
            int page = savedInstanceState.getInt(KEY_CURRENT_PAGE);
            mViewPager.setCurrentItem(page, true);
        }
    }

    /**
     * @param visible Whether the "loading" progress bar should be shown
     */
    private void showProgressBar(boolean visible) {
        if (visible) {
            mProgressBar.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
        } else {
            mViewPager.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * @param visible Whether the error view should be shown
     *                TODO: animate
     */
    private void showErrorView(boolean visible) {
        if (visible) {
            mErrorView.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
        } else {
            mViewPager.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.GONE);
        }
    }

    /**
     * Syncs the event database
     */
    private void refresh() {
        if (!NetworkUtils.checkInternetConnection(this)) {
            Toast.makeText(this, R.string.netRequired, Toast.LENGTH_LONG).show();
        } else {
            showProgressBar(true);
            FirebaseSyncJobService.dispatchSyncNow(this);
        }
    }

    /**
     * Play the animation hint whenever user resumes the app
     */
    @Override
    protected void onResume() {
        // Load the saved page, if any
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mViewPager.setCurrentItem(prefs.getInt(KEY_CURRENT_PAGE, 0));
        super.onResume();
    }

    /**
     * Cancel the animation hint if the app loses focus
     */
    @Override
    protected void onPause() {
        // Save the current page
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt(KEY_CURRENT_PAGE, mViewPager.getCurrentItem()).apply();
        super.onPause();
    }

    /**
     * @param menu The menu object to inflate
     * @return Whether config of menu was successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * @param item The selected menu item (Refresh or License)
     * @return Whether selection was successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mi_refresh) {
            refresh();
        } else if (item.getItemId() == R.id.mi_license) {
            new LicenseFragment().show(getSupportFragmentManager(), LicenseFragment.TAG);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates/reinitializes our db loader
     * @param loader_id ID of the loader to init
     * @param args Any startup args (unused)
     * @return A CursorLoader with our db query results
     */
    @Override
    @NonNull
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle args) {
        switch (loader_id) {
            case ID_LOADER_EVENTS: {
                long today = DateUtils.getTimestamp();
                Log.d(
                    LOG_TAG,
                    String.format(
                        "Loading results for %s...",
                        DateUtils.sDateFormatter.format(today)
                    )
                );
                showProgressBar(true);
                return new CursorLoader(
                        this,
                        EventDbContract.EventTable.CONTENT_URI,
                        null,
                        String.format("%s=?", EventDbContract.EventTable.COLUMN_TIMESTAMP),
                        new String[] { String.valueOf(today) },
                        EventDbContract.EventTable.COLUMN_YEAR
                );
            }
            default: {
                throw new RuntimeException("Requested loader is not implemented");
            }
        }
    }

    /**
     * Called when data has finished loading. Should populate views.
     * @param loader The CursorLoader returned by onCreateLoader()
     * @param data The data from our db
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mHistoryViewAdapter.swapCursor(data);
        showProgressBar(false);
        if (data != null && data.getCount() > 0) {
            Log.d(LOG_TAG, "Load finished");
            showErrorView(false);
        } else {
            Log.d(LOG_TAG, "Load returned no results");
            if (NetworkUtils.checkInternetConnection(this)) {
                refresh();
            } else {
                showErrorView(true);
            }
        }
    }

    /**
     * Invalidates our view data when loader is reset
     * @param loader The loader to reset
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mHistoryViewAdapter.swapCursor(null);
    }
}

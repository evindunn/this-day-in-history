package com.emdevsite.todayhist;

import android.animation.ValueAnimator;
import android.app.IntentService;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.databinding.ActivityMainBinding;
import com.emdevsite.todayhist.sync.SyncIntentService;
import com.emdevsite.todayhist.sync.SyncUtils;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;
import com.emdevsite.todayhist.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ValueAnimator.AnimatorUpdateListener {
    
    private HistoryViewAdapter mHistoryViewAdapter;
    private ValueAnimator mAlphaAnimation;
    private ActivityMainBinding mActivityData;

    private static final int ID_LOADER_EVENTS = 14;

    /**
     * Creates a new MainActivity for This Day in History
     * @param savedInstanceState Saved instance of last usage of the app, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityData = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mHistoryViewAdapter = new HistoryViewAdapter(getSupportFragmentManager());
        mActivityData.vpText.setAdapter(mHistoryViewAdapter);

        // For swipe hinting
        mActivityData.vpText.setPageMargin(
            -getResources().getDimensionPixelSize(R.dimen.dimen_view_pager_margin)
        );

        initAnimation();
        SyncUtils.initialize(this);
        getSupportLoaderManager().initLoader(ID_LOADER_EVENTS, null, this);
    }

    /**
     * @param visible Whether to hide the main view and show the "loading" progress bar
     */
    public void showProgressBar(boolean visible) {
        if (visible) {
            mActivityData.vpText.setVisibility(View.INVISIBLE);
            mActivityData.progressBar.setVisibility(View.VISIBLE);
        } else {
            mActivityData.progressBar.setVisibility(View.GONE);
            mActivityData.vpText.setVisibility(View.VISIBLE);
        }
    }

    public synchronized void refresh() {
        if (!NetworkUtils.checkInternetConnection(this)) {
            Toast.makeText(this, R.string.netRequired, Toast.LENGTH_LONG).show();
            return;
        }
        showProgressBar(true);
        SyncUtils.syncNow(this);
        getSupportLoaderManager().restartLoader(ID_LOADER_EVENTS, null, this);
    }

    /**
     * Play the animation hint whenever user resumes the app
     */
    @Override
    protected void onResume() {
        mAlphaAnimation.start();
        super.onResume();
    }

    /**
     * Cancel the animation hint if the app loses focus
     */
    @Override
    protected void onPause() {
        mAlphaAnimation.cancel();
        super.onPause();
    }

    /**
     * Release all resources associated with the animation hint
     */
    @Override
    protected void onDestroy() {
        mAlphaAnimation.removeAllUpdateListeners();
        super.onDestroy();
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
                LogUtils.logMessage('d', getClass(), "Loading...");
                showProgressBar(true);
                return new CursorLoader(
                        this,
                        EventDbContract.EventTable.CONTENT_URI,
                        new String[] {
                            EventDbContract.EventTable.COLUMN_YEAR,
                            EventDbContract.EventTable.COLUMN_TEXT,
                            EventDbContract.EventTable.COLUMN_TIMESTAMP
                        },
                        String.format("%s=?", EventDbContract.EventTable.COLUMN_TIMESTAMP),
                        new String[] { String.valueOf(DateUtils.getTimestamp()) },
                        EventDbContract.EventTable.COLUMN_YEAR + " DESC"
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
        showProgressBar(false);
        if (data != null && data.getCount() > 0) {
            mHistoryViewAdapter.swapCursor(data);
            mActivityData.vpText.setCurrentItem(0, true);
            LogUtils.logMessage('d', getClass(), "Load finished.");
        } else {
            LogUtils.logMessage('d', getClass(), "Load returned no results.");
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

    /**
     * Steps to perform when animation is updated
     * @param animation The swipe hint animation that is triggered
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float v = (float) animation.getAnimatedValue();
        mActivityData.ivLeft.setAlpha(v);
        mActivityData.ivRight.setAlpha(v);
        mActivityData.tvSwipeHint.setAlpha(v);
    }

    /**
     * Helper method that initializes our swipe hint animation, called in onCreate()
     */
    private void initAnimation() {
        final int TIME_ANIMATION = 2000;
        final int REPEAT_ANIMATION = 7;

        mAlphaAnimation = ValueAnimator.ofFloat(0f, 1f);
        mAlphaAnimation.setDuration(TIME_ANIMATION);
        mAlphaAnimation.setRepeatCount(REPEAT_ANIMATION);
        mAlphaAnimation.setRepeatMode(ValueAnimator.REVERSE);
        mAlphaAnimation.addUpdateListener(this);
    }
}

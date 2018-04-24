package com.emdevsite.todayhist;

import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.databinding.ActivityMainBinding;
import com.emdevsite.todayhist.sync.SyncUtils;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ValueAnimator.AnimatorUpdateListener {
    
    private HistoryViewAdapter mHistoryViewAdapter;
    private ValueAnimator mAlphaAnimation;
    private ActivityMainBinding mActivityData;

    private static final int ID_LOADER_EVENTS = 14;

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
        getSupportLoaderManager().initLoader(ID_LOADER_EVENTS, null, this);
    }

    public void showProgressBar(boolean visible) {
        if (visible) {
            mActivityData.vpText.setVisibility(View.INVISIBLE);
            mActivityData.progressBar.setVisibility(View.VISIBLE);
        } else {
            mActivityData.progressBar.setVisibility(View.GONE);
            mActivityData.vpText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        SyncUtils.syncNow(this);
        mAlphaAnimation.start();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAlphaAnimation.removeAllUpdateListeners();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

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
                        null,
                        null,
                        null,
                        EventDbContract.EventTable.COLUMN_YEAR
                );
            }
            default: {
                throw new RuntimeException("Requested loader is not implemented");
            }
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        showProgressBar(false);
        if (data != null && data.getCount() > 0) {
            long lastUpdate = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getLong(getString(R.string.prefs_key_lastUpdate), DateUtils.getTimestamp());

            getSupportActionBar().setTitle(DateUtils.getTimestampAsString(lastUpdate));
            mHistoryViewAdapter.swapCursor(data);
            mActivityData.vpText.setCurrentItem(data.getCount() / 2, true);

            LogUtils.logMessage('d', getClass(), "Load finished.");
        } else {
            LogUtils.logMessage('d', getClass(), "Load returned no results.");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mHistoryViewAdapter.swapCursor(null);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float v = (float) animation.getAnimatedValue();
        mActivityData.ivLeft.setAlpha(v);
        mActivityData.ivRight.setAlpha(v);
        mActivityData.tvSwipeHint.setAlpha(v);
    }

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

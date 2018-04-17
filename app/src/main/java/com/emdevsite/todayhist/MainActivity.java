package com.emdevsite.todayhist;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emdevsite.todayhist.data.EventDbContract;
import com.emdevsite.todayhist.sync.SyncUtils;
import com.emdevsite.todayhist.utils.DateUtils;
import com.emdevsite.todayhist.utils.LogUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ValueAnimator.AnimatorUpdateListener {

    private ViewPager mHistoryViewPager;
    private HistoryViewAdapter mHistoryViewAdapter;
    private ProgressBar mProgressBar;

    private ValueAnimator mAlphaAnimation;
    private ImageView mArrowLeft;
    private ImageView mArrowRight;
    private TextView mSwipeTextView;

    private static final int ID_LOADER_EVENTS = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHistoryViewPager = findViewById(R.id.vp_text);
        mProgressBar = findViewById(R.id.progress_bar);

        mHistoryViewAdapter = new HistoryViewAdapter(getSupportFragmentManager());
        mHistoryViewPager.setAdapter(mHistoryViewAdapter);
        mHistoryViewPager.setClipToPadding(false);

        int vp_padding = dpToPixel(getResources().getDimension(R.dimen.dimen_view_pager_padding));
        mHistoryViewPager.setPadding(vp_padding, vp_padding, vp_padding, vp_padding);
        mHistoryViewPager.setPageMargin(dpToPixel(8));

        initAnimation();

        getSupportLoaderManager().initLoader(ID_LOADER_EVENTS, null, this);
    }

    public void showProgressBar(boolean visible) {
        if (visible) {
            mHistoryViewPager.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mHistoryViewPager.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        // TODO: Temporary for db testing
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
                        EventDbContract.EventTable.COLUMN_DATE
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
            mHistoryViewAdapter.swapCursor(data);
        } else {
            LogUtils.logMessage('i', getClass(), "Load returned no results.");
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float v = (float) animation.getAnimatedValue();
        mArrowLeft.setAlpha(v);
        mArrowRight.setAlpha(v);
        mSwipeTextView.setAlpha(v);
    }

    private void initAnimation() {
        final int TIME_ANIMATION = 2000;
        final int REPEAT_ANIMATION = 7;

        mArrowLeft = findViewById(R.id.iv_left);
        mArrowRight = findViewById(R.id.iv_right);
        mSwipeTextView = findViewById(R.id.tv_swipe_hint);

        mAlphaAnimation = ValueAnimator.ofFloat(0f, 1f);
        mAlphaAnimation.setDuration(TIME_ANIMATION);
        mAlphaAnimation.setRepeatCount(REPEAT_ANIMATION);
        mAlphaAnimation.setRepeatMode(ValueAnimator.REVERSE);
        mAlphaAnimation.addUpdateListener(this);
    }

    private int dpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return Math.round(dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}

package com.afollestad.twitter.ui.theming;

import android.os.Bundle;
import com.afollestad.silk.activities.SilkDrawerActivity;
import com.afollestad.twitter.R;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Provides a standardized base for all activities that automatically theme themselves based on
 * the application's theme setting; this is basically every activity in the app.
 *
 * @author Aidan Follestad (afollestad)
 */
public abstract class ThemedDrawerActivity extends SilkDrawerActivity {

    private int mTheme;

    private PullToRefreshAttacher mPullToRefreshAttacher;

    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTheme = ThemedActivity.getBoidTheme(this);
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ThemedActivity.shouldRecreate(this, mTheme)) recreate();
    }

    @Override
    public int getDrawerIndicatorRes() {
        if (ThemedActivity.getBoidTheme(this) == R.style.Theme_Boidlight)
            return R.drawable.ic_navigation_drawer_dark;
        else return R.drawable.ic_navigation_drawer_light;
    }
}
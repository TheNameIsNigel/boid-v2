package com.teamboid.twitter.fragments.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.afollestad.silk.cache.SilkComparable;
import com.afollestad.silk.fragments.SilkLastUpdatedFragment;
import com.devspark.appmsg.AppMsg;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.ui.MainActivity;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public abstract class BoidListFragment<T extends SilkComparable> extends SilkLastUpdatedFragment<T> {

    private PullToRefreshAttacher mPullToRefreshAttacher;

    public BoidListFragment(String cacheTitle) {
        super(cacheTitle, BoidApp.getSilkCache());
    }

    public final int getPageLength() {
        //TODO configurable setting
        return 200;
    }

    @Override
    public void onError(String message) {
        AppMsg.makeText(getActivity(), message, new AppMsg.Style(AppMsg.LENGTH_LONG,
                android.R.color.holo_blue_dark), R.layout.app_msg_themed).show();
    }

    @Override
    public boolean getShouldShowLastUpdated() {
        return getLastUpdatedTime() != null;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_list_lastupdated_themed;
    }

    @Override
    public void onUserRefresh() {
        mPullToRefreshAttacher.setRefreshing(true);
        super.onUserRefresh();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
        mPullToRefreshAttacher.addRefreshableView(getListView(), new PullToRefreshAttacher.OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                performRefresh(false);
            }
        });
    }

    @Override
    public void setLoadComplete(boolean error) {
        super.setLoadComplete(error);
        mPullToRefreshAttacher.setRefreshComplete();
    }

    @Override
    protected boolean onPreLoad() {
        saveScrollPos();
        return super.onPreLoad();
    }

    @Override
    protected void onPostLoad(T[] results) {
        super.onPostLoad(results);
        if (getAdapter().getCount() > results.length) {
            // Items were added to the top of the list instead of overwriting the adapter, restore scroll position
            restoreScrollPos(results.length);
            Toast.makeText(getActivity(), "Added " + results.length + " items", Toast.LENGTH_LONG).show();
        }
    }

    public final void saveScrollPos() {
        int mSavedIndex = getListView().getFirstVisiblePosition();
        View v = getListView().getChildAt(0);
        int mSavedFromTop = (v == null) ? 0 : v.getTop();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putInt(getCacheTitle() + "_saved_index", mSavedIndex)
                .putInt(getCacheTitle() + "_saved_top", mSavedFromTop).commit();
    }

    public final void restoreScrollPos(int addedCount) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int mSavedIndex = prefs.getInt(getCacheTitle() + "_saved_index", -1);
        if (mSavedIndex == -1) return;
        else if (mSavedIndex > getAdapter().getCount() - 1) {
            // The saved scroll position is out of date with the cache
            prefs.edit().remove(getCacheTitle() + "_saved_index").remove(getCacheTitle() + "_saved_top").commit();
            return;
        }
        int mSavedFromTop = prefs.getInt(getCacheTitle() + "_saved_top", 0);
        getListView().clearFocus();
        ((ListView) getListView()).setSelectionFromTop(mSavedIndex + addedCount, mSavedFromTop);
        getListView().requestFocus();
        getListView().requestFocusFromTouch();
    }
}
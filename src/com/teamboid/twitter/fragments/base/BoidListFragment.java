package com.teamboid.twitter.fragments.base;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import com.afollestad.silk.cache.SilkComparable;
import com.afollestad.silk.fragments.SilkLastUpdatedFragment;
import com.devspark.appmsg.AppMsg;
import com.teamboid.twitter.BoidApp;

public abstract class BoidListFragment<T extends SilkComparable> extends SilkLastUpdatedFragment<T> {

    public BoidListFragment(String cacheTitle) {
        super(cacheTitle, BoidApp.getSilkCache());
    }

    public final int getPageLength() {
        //TODO configurable setting
        return 200;
    }

    @Override
    public void onError(String message) {
        AppMsg.makeText(getActivity(), message, AppMsg.STYLE_ALERT).show();
    }

    public final void saveScrollPos() {
        int mSavedIndex = getListView().getFirstVisiblePosition();
        View v = getListView().getChildAt(0);
        int mSavedFromTop = (v == null) ? 0 : v.getTop();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putInt("saved_index", mSavedIndex).putInt("saved_top", mSavedFromTop).commit();
    }

    public final void restoreScrollPos(int addedCount) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int mSavedIndex = prefs.getInt("saved_index", -1);
        if (mSavedIndex == -1) return;
        else if (mSavedIndex > getAdapter().getCount() - 1) {
            // The saved scroll position is out of date with the cache
            prefs.edit().remove("saved_index").remove("saved_top").commit();
            return;
        }
        int mSavedFromTop = prefs.getInt("saved_top", 0);
        getListView().clearFocus();
        ((ListView) getListView()).setSelectionFromTop(mSavedIndex + addedCount, mSavedFromTop);
        getListView().requestFocus();
    }
}
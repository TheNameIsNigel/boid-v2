package com.afollestad.twitter.fragments.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.afollestad.silk.caching.SilkComparable;
import com.afollestad.silk.fragments.SilkCachedFeedFragment;

import java.util.List;

/**
 * Extended by the {@link BoidListFragment}, handles saving list positions and restoring them later.
 *
 * @author Aidan Follestad (afollestad)
 */
abstract class StateListFragment<ItemType extends SilkComparable<ItemType>> extends SilkCachedFeedFragment<ItemType> {

    private boolean mShouldRestoreScroll = false;
    private int mSavedIndex = -1;
    private int mSavedTop = -1;

    @Override
    protected void onPreLoad() {
        super.onPreLoad();
        mShouldRestoreScroll = !getAdapter().isEmpty();
        if (mShouldRestoreScroll)
            saveScrollPos();
    }

    @Override
    protected void onPostLoad(List<ItemType> results, boolean paginated) {
        super.onPostLoad(results, paginated);
        // Only restore the scroll position if the list was not empty before refreshing
        if (mShouldRestoreScroll)
            restoreScrollPos(results.size());
    }

    @Override
    protected void onPostLoadFromCache(List<ItemType> results) {
        appendToAdapter(results, false);
        restoreScrollPos(-1);
        setLoadComplete(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getAdapter().isEmpty())
            restoreScrollPos(-1);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveScrollPos();
    }

    private SharedPreferences getPrefs() {
        return getActivity().getSharedPreferences("[column-positions]", Context.MODE_PRIVATE);
    }

    private int[] getPersistence() {
        if (getActivity() == null || (mSavedIndex > -1 && mSavedTop > -1))
            return new int[]{mSavedIndex, mSavedTop};
        String persisted = getPrefs().getString(getCacheName(), null);
        if (persisted == null) return new int[]{-1, -1};
        String[] split = persisted.split(":");
        getPrefs().edit().remove(getCacheName()).commit();
        mSavedIndex = Integer.parseInt(split[0]);
        mSavedTop = Integer.parseInt(split[1]);
        return new int[]{mSavedIndex, mSavedTop};
    }

    private void setPersistence(int position, int top) {
        getPrefs().edit().putString(getCacheName(), position + ":" + top).commit();
    }

    final void saveScrollPos() {
        mSavedIndex = getListView().getFirstVisiblePosition();
        View v = getListView().getChildAt(0);
        mSavedTop = (v == null) ? 0 : v.getTop();
        setPersistence(mSavedIndex, mSavedTop);
        Log.d("StateListFragment", "List position saved; index: " + mSavedIndex + ", top: " + mSavedTop);
    }

    final void restoreScrollPos(final int addedCount) {
        int[] saved = getPersistence();
        final int index = addedCount == -1 ? saved[0] : addedCount;
        if (index == -1) return;
        else if (index > getAdapter().getCount() - 1) {
            Log.d("StateListFragment", "Saved scroll position larger than list size...");
            return;
        }
        final int top = saved[1];
        getListView().post(new Runnable() {
            @Override
            public void run() {
                getListView().clearFocus();
                ((ListView) getListView()).setSelectionFromTop(index, top);
                Log.d("StateListFragment", "Scroll position restored; index: " + index + ", top: " + top);
            }
        });
    }
}
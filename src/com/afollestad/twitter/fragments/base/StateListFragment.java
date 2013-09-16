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

    protected boolean mShouldRestoreScroll = false;

    /**
     * Saves the scroll position before refreshing if the list is not currently empty.
     */
    @Override
    protected void onPreLoad() {
        super.onPreLoad();
        mShouldRestoreScroll = !getAdapter().isEmpty();
    }

    /**
     * Restores the scroll position after refreshing is complete.
     */
    @Override
    protected void onPostLoad(List<ItemType> results, boolean paginated) {
        super.onPostLoad(results, paginated);
        // Only restore the scroll position if the list was not empty before refreshing
        if (mShouldRestoreScroll) {
            // The scroll position is set to the number of items added after refresh
            restoreScrollPos(results.size());
        }
    }

    /**
     * Loads items that were brought in from the cache into the list and restores the last saved scroll position.
     */
    @Override
    protected void onPostLoadFromCache(List<ItemType> results) {
        getAdapter().set(onUpdateItems(results, false));
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
        if (getActivity() == null)
            throw new RuntimeException("Could not get state list persistence, activity is null...");
        return getActivity().getSharedPreferences("[column-positions]", Context.MODE_PRIVATE);
    }

    private int[] getPersistence() {
        String persisted = getPrefs().getString(getCacheName(), null);
        if (persisted == null) return new int[]{-1, -1};
        String[] split = persisted.split(":");
        return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
    }

    private void setPersistence(int position, int top) {
        getPrefs().edit().putString(getCacheName(), position + ":" + top).commit();
    }

    final void saveScrollPos() {
        int mSavedIndex = getListView().getFirstVisiblePosition();
        View v = getListView().getChildAt(0);
        int mSavedFromTop = (v == null) ? 0 : v.getTop();
        setPersistence(mSavedIndex, mSavedFromTop);
        Log.d("StateListFragment", "List position saved; index: " + mSavedIndex + ", top: " + mSavedFromTop);
    }

    protected final void setScrollPosition(final int index, final int top) {
        getListView().post(new Runnable() {
            @Override
            public void run() {
                getListView().clearFocus();
                ((ListView) getListView()).setSelectionFromTop(index, top);
                Log.d("StateListFragment", "Scroll position set; index: " + index + ", top: " + top);
            }
        });
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
        setScrollPosition(index, top);
    }
}
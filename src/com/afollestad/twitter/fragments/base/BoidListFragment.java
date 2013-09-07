package com.afollestad.twitter.fragments.base;

import android.os.Bundle;
import android.view.View;
import com.afollestad.silk.caching.LimiterBehavior;
import com.afollestad.silk.caching.SilkCache;
import com.afollestad.silk.caching.SilkCacheLimiter;
import com.afollestad.silk.caching.SilkComparable;
import com.afollestad.silk.fragments.SilkCachedFeedFragment;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.MainActivity;
import com.afollestad.twitter.ui.SearchActivity;
import twitter4j.Paging;
import twitter4j.Twitter;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import java.util.ArrayList;
import java.util.List;

public abstract class BoidListFragment<ItemType extends SilkComparable<ItemType>> extends SilkCachedFeedFragment<ItemType> {

    private PullToRefreshAttacher mPullToRefreshAttacher;

    public final int getPageLength() {
        // TODO configurable setting
        return 200;
    }

    @Override
    protected void onPostLoad(List<ItemType> results) {
        super.onPostLoad(results);
        // Cache will expire 10 minutes after refreshing
        getCache().setExpiration(0, 0, 0, 10);
    }

    @Override
    protected SilkCache<ItemType> onCacheInitialized(SilkCache<ItemType> cache) {
        if (!cache.hasLimiter()) {
            // Limit caches to 700 tweets, older tweets are taken off when the limit is reached
            // TODO this will cause problems with pagination?
            cache.setLimiter(new SilkCacheLimiter(700, LimiterBehavior.REMOVE_BOTTOM));
        }
        return cache;
    }

    @Override
    public final void onError(Exception e) {
        BoidApp.showAppMsgError(getActivity(), e);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_list_themed;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof MainActivity)
            mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
        else mPullToRefreshAttacher = ((SearchActivity) getActivity()).getPullToRefreshAttacher();
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
    protected final List<ItemType> refresh() throws Exception {
        if (!isPaginationEnabled()) {
            return load(BoidApp.get(getActivity()).getClient(), null);
        }
        Paging paging = new Paging();
        paging.setCount(getPageLength());
        if (getAdapter().getCount() > 0) {
            // Get tweets newer than the most recent tweet in the adapter
            paging.setSinceId(getAdapter().getItemId(0));
            // Refresh in a loop to fill gaps until all tweets are retrieved
            List<ItemType> results = new ArrayList<ItemType>();
            while (true) {
                List<ItemType> temp = load(BoidApp.get(getActivity()).getClient(), paging);
                if (temp == null || temp.size() == 0) break;
                int index = 0;
                for (ItemType item : temp) {
                    results.add(index, item);
                    index++;
                }
                paging.setSinceId(getItemId(temp.get(0)));
            }
            return results;
        }
        return load(BoidApp.get(getActivity()).getClient(), paging);
    }

    protected abstract List<ItemType> load(Twitter client, Paging paging) throws Exception;

    protected abstract long getItemId(ItemType item);

    protected abstract boolean isPaginationEnabled();

    // TODO make scroll position saving/restoring work correctly.

//    public final void saveScrollPos() {
//        int mSavedIndex = getListView().getFirstVisiblePosition();
//        View v = getListView().getChildAt(0);
//        int mSavedFromTop = (v == null) ? 0 : v.getTop();
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        prefs.edit().putInt(getCacheTitle() + "_saved_index", mSavedIndex)
//                .putInt(getCacheTitle() + "_saved_top", mSavedFromTop).commit();
//    }
//
//    public final void restoreScrollPos(int addedCount) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        int mSavedIndex = prefs.getInt(getCacheTitle() + "_saved_index", -1);
//        if (mSavedIndex == -1) return;
//        else if (mSavedIndex > getAdapter().getCount() - 1) {
//            // The saved scroll position is out of date with the cache
//            prefs.edit().remove(getCacheTitle() + "_saved_index").remove(getCacheTitle() + "_saved_top").commit();
//            return;
//        }
//        int mSavedFromTop = prefs.getInt(getCacheTitle() + "_saved_top", 0);
//        getListView().clearFocus();
//        ((ListView) getListView()).setSelectionFromTop(mSavedIndex + addedCount, mSavedFromTop);
//        getListView().requestFocus();
//        getListView().requestFocusFromTouch();
//    }
}

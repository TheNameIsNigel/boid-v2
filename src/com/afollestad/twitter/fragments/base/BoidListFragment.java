package com.afollestad.twitter.fragments.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import com.afollestad.silk.cache.CacheLimiter;
import com.afollestad.silk.cache.SilkCacheManager;
import com.afollestad.silk.cache.SilkComparable;
import com.afollestad.silk.fragments.SilkLastUpdatedFragment;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.MainActivity;
import com.afollestad.twitter.ui.SearchActivity;
import com.devspark.appmsg.AppMsg;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class BoidListFragment<T extends SilkComparable> extends SilkLastUpdatedFragment<T> {

    private PullToRefreshAttacher mPullToRefreshAttacher;

    public final int getPageLength() {
        //TODO configurable setting
        return 200;
    }

    @Override
    protected final File getCacheDirectory() {
        return BoidApp.getSilkCache();
    }

    @Override
    protected SilkCacheManager<T> onCacheInitialized(SilkCacheManager<T> cache) {
        // Limit caches to 700 tweets, older tweets are taken off when the limit is reached
        cache.setLimiter(new CacheLimiter(700, CacheLimiter.TrimMode.BOTTOM));
        if (!cache.hasExpiration()) {
            // Caches expire after 5 days
            cache.setExpiration(Calendar.getInstance().getTimeInMillis() + (1000 * 60 * 60 * 24 * 5));
        }
        return cache;
    }

    @Override
    public void onError(Exception e) {
        String msg = e.getMessage();
        if (e instanceof TwitterException) {
            TwitterException te = (TwitterException) e;
            if (te.exceededRateLimitation())
                msg = getString(R.string.rate_limit_error);
            else if (te.isCausedByNetworkIssue())
                msg = getString(R.string.network_error);
            else if (te.isErrorMessageAvailable())
                msg = te.getErrorMessage();
        }
        AppMsg.makeText(getActivity(), msg, new AppMsg.Style(AppMsg.LENGTH_LONG,
                android.R.color.holo_blue_dark), R.layout.app_msg_themed).show();
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
    protected boolean onPreLoad() {
        saveScrollPos();
        return super.onPreLoad();
    }

    @Override
    protected void onPostLoad(List<T> results) {
        super.onPostLoad(results);
        if (getAdapter().getCount() > results.size()) {
            // Items were added to the top of the list instead of overwriting the adapter, restore scroll position
            restoreScrollPos(results.size());
        }
    }

    @Override
    protected final List<T> refresh() throws Exception {
        if (!isPaginationEnabled()) {
            return load(BoidApp.get(getActivity()).getClient(), null);
        }
        Paging paging = new Paging();
        paging.setCount(getPageLength());
        if (getAdapter().getCount() > 0) {
            // Get tweets newer than the most recent tweet in the adapter
            paging.setSinceId(getAdapter().getItemId(0));
            // Refresh in a loop to fill gaps until all tweets are retrieved
            List<T> results = new ArrayList<T>();
            while (true) {
                List<T> temp = load(BoidApp.get(getActivity()).getClient(), paging);
                if (temp == null || temp.size() == 0) break;
                int index = 0;
                for (T item : temp) {
                    results.add(index, item);
                    index++;
                }
                paging.setSinceId(getItemId(temp.get(0)));
            }
            return results;
        }
        return load(BoidApp.get(getActivity()).getClient(), paging);
    }

    protected abstract List<T> load(Twitter client, Paging paging) throws Exception;

    protected abstract long getItemId(T item);

    protected abstract boolean isPaginationEnabled();

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
package com.afollestad.twitter.fragments.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import com.afollestad.silk.caching.LimiterBehavior;
import com.afollestad.silk.caching.SilkCache;
import com.afollestad.silk.caching.SilkCacheLimiter;
import com.afollestad.silk.caching.SilkComparable;
import com.afollestad.silk.fragments.SilkCachedFeedFragment;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.theming.ThemedDrawerActivity;
import com.afollestad.twitter.ui.theming.ThemedPtrActivity;
import twitter4j.Paging;
import twitter4j.Twitter;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import java.util.ArrayList;
import java.util.List;

public abstract class BoidListFragment<ItemType extends SilkComparable<ItemType>> extends SilkCachedFeedFragment<ItemType> {

    private PullToRefreshAttacher mPullToRefreshAttacher;
    private long mCursor = -1;
    private boolean mShouldRestoreScroll = false;

    @Override
    protected int getAddIndex() {
        return 0;
    }

    public final int getPageLength() {
        return 200;
    }

    @Override
    public void setLoading(boolean progress) {
        super.setLoading(progress);
        if (mPullToRefreshAttacher != null && !getAdapter().isEmpty())
            mPullToRefreshAttacher.setRefreshing(true);
    }

    @Override
    public void setLoadComplete(boolean error) {
        super.setLoadComplete(error);
        if (mPullToRefreshAttacher != null)
            mPullToRefreshAttacher.setRefreshComplete();
    }

    @Override
    protected void onPreLoad() {
        super.onPreLoad();
        mShouldRestoreScroll = getAdapter().getCount() > 0;
        saveScrollPos();
    }

    @Override
    protected void onPostLoad(List<ItemType> results, boolean paginated) {
        super.onPostLoad(results, paginated);
        // Cache will expire 15 minutes after refreshing
        if (getCache() != null)
            getCache().setExpiration(0, 0, 0, 15);
        // Only restore the scroll position if the list was not empty before refreshing
        if (mShouldRestoreScroll)
            restoreScrollPos(results.size());
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
        if (getActivity() instanceof ThemedDrawerActivity)
            mPullToRefreshAttacher = ((ThemedDrawerActivity) getActivity()).getPullToRefreshAttacher();
        else if (getActivity() instanceof ThemedPtrActivity)
            mPullToRefreshAttacher = ((ThemedPtrActivity) getActivity()).getPullToRefreshAttacher();
        if (mPullToRefreshAttacher != null) {
            mPullToRefreshAttacher.addRefreshableView(getListView(), new PullToRefreshAttacher.OnRefreshListener() {
                @Override
                public void onRefreshStarted(View view) {
                    performRefresh(false);
                }
            });
        }
    }

    @Override
    protected final List<ItemType> refresh() throws Exception {
        Paging paging = new Paging();
        paging.setCount(getPageLength());
        if (isPageCursorMode()) {
            mCursor = -1;
        } else if (!getAdapter().isEmpty()) {
            // Get tweets newer than the most recent tweet in the adapter
            paging.setSinceId(getAdapter().getItemId(getAddIndex()));
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
                paging.setSinceId(getAdapter().getItemId(temp.get(0)));
            }
            return results;
        }
        return load(BoidApp.get(getActivity()).getClient(), paging);
    }

    @Override
    public void performPaginate(boolean showProgress) {
        if (!isPaginationEnabled()) return;
        super.performPaginate(showProgress);
    }

    public final void setCursor(long cursor) {
        mCursor = cursor;
    }

    @Override
    protected final List<ItemType> paginate() throws Exception {
        Paging paging = new Paging();
        paging.setCount(getPageLength());
        if (!isPageCursorMode()) {
            // Get tweets older than the oldest one in the adapter
            paging.setMaxId(getAdapter().getItemId(getAdapter().getCount() - 1) - 1);
        }
        return load(BoidApp.get(getActivity()).getClient(), paging);
    }

    protected abstract List<ItemType> load(Twitter client, Paging paging) throws Exception;

    protected abstract boolean isPaginationEnabled();

    protected long getCursor() {
        return mCursor;
    }

    protected boolean isPageCursorMode() {
        return false;
    }

    public final void saveScrollPos() {
        int mSavedIndex = getListView().getFirstVisiblePosition();
        View v = getListView().getChildAt(0);
        int mSavedFromTop = (v == null) ? 0 : v.getTop();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putInt(getCacheName() + "_saved_index", mSavedIndex)
                .putInt(getCacheName() + "_saved_top", mSavedFromTop).commit();
    }

    public final void restoreScrollPos(final int addedCount) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final int mSavedIndex = prefs.getInt(getCacheName() + "_saved_index", -1);
        if (mSavedIndex == -1) return;
        else if (mSavedIndex > getAdapter().getCount() - 1) {
            // The saved scroll position is out of date with the cache
            prefs.edit().remove(getCacheName() + "_saved_index").remove(getCacheName() + "_saved_top").commit();
            return;
        }
        final int mSavedFromTop = prefs.getInt(getCacheName() + "_saved_top", 0);
        getListView().post(new Runnable() {
            @Override
            public void run() {
                getListView().clearFocus();
                ((ListView) getListView()).setSelectionFromTop(addedCount, mSavedFromTop);
            }
        });
    }
}
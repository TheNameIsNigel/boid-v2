package com.afollestad.twitter.fragments.base;

import android.os.Bundle;
import android.view.View;
import com.afollestad.silk.caching.LimiterBehavior;
import com.afollestad.silk.caching.SilkCache;
import com.afollestad.silk.caching.SilkCacheLimiter;
import com.afollestad.silk.caching.SilkComparable;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.theming.ThemedDrawerActivity;
import com.afollestad.twitter.ui.theming.ThemedPtrActivity;
import twitter4j.Paging;
import twitter4j.Twitter;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import java.util.ArrayList;
import java.util.List;

/**
 * The fragment class extended by most other fragments in the app (including all column fragments).
 * <p/>
 * Automatically handles many things used by all the columns, such as action bar pull to refresh, cache expiration,
 * and pagination of statuses or users.
 *
 * @author Aidan Follestad (afollestad)
 */
public abstract class BoidListFragment<ItemType extends SilkComparable<ItemType>> extends StateListFragment<ItemType> {

    private PullToRefreshAttacher mPullToRefreshAttacher;
    private long mCursor = -1;

    final int getPageLength() {
        return 200;
    }

    @Override
    protected void onPreLoad() {
        super.onPreLoad();
        if (mPullToRefreshAttacher != null && !getAdapter().isEmpty())
            mPullToRefreshAttacher.setRefreshing(true);
    }

    protected abstract boolean doesCacheExpire();

    public final void jumpToTop() {
        setScrollPosition(0, 0);
    }

    @Override
    protected List<ItemType> onUpdateItems(List<ItemType> results, boolean paginated) {
        if (mShouldRestoreScroll)
            saveScrollPos();
        List<ItemType> items = getAdapter().getItems();
        if (results != null) {
            if (paginated) {
                items.addAll(results);
            } else {
                // Tweets are added to the beginning of the list instead of overwriting everything like the default implementation
                items.addAll(0, results);
            }
        }
        return items;
    }

    @Override
    protected void onPostLoad(List<ItemType> results, boolean paginated) {
        super.onPostLoad(results, paginated);
        if (mPullToRefreshAttacher != null)
            mPullToRefreshAttacher.setRefreshComplete();
        // Cache will expire a day after refreshing
        if (getCache() != null && doesCacheExpire())
            getCache().setExpiration(0, 1, 0, 0);
    }

    @Override
    protected SilkCache<ItemType> onCacheInitialized(SilkCache<ItemType> cache) {
        if (!cache.hasLimiter()) {
            // Limit caches to 700 tweets, older tweets are taken off when the limit is reached
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
        if (!isPaginationEnabled()) {
            return load(BoidApp.get(getActivity()).getClient(), null);
        }
        Paging paging = new Paging();
        paging.setCount(getPageLength());
        if (isPageCursorMode()) {
            mCursor = -1;
        } else if (!getAdapter().isEmpty()) {
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
                paging.setSinceId(getAdapter().getItemId(temp.get(0)));
            }
            return results;
        }
        return load(BoidApp.get(getActivity()).getClient(), paging);
    }

    @Override
    public void performPaginate(boolean showProgress) {
        if (!isPaginationEnabled()) return;
        if (mPullToRefreshAttacher != null && !showProgress) {
            // Override show progress value to show the action bar refresh indicator at all times
            mPullToRefreshAttacher.setRefreshing(true);
        }
        super.performPaginate(showProgress);
    }

    protected final void setCursor(long cursor) {
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
}
package com.teamboid.twitter.fragments.base;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import com.devspark.appmsg.AppMsg;
import com.teamboid.twitter.DrawerActivity;
import com.teamboid.twitter.R;
import twitter4j.TwitterException;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Provides a standardized base for list fragments that retrieve and display a feed. Also handles
 * attaching to action bar refresh.
 *
 * @param <T> The class contained in the fragment's list adapter, usually Status or DirectMessage.
 * @author Aidan Follestad (afollestad)
 */
public abstract class FeedFragment<T> extends BoidListFragment<T> {

    public FeedFragment(boolean paginationEnabled, boolean cachingEnabled) {
        super(cachingEnabled);
        mPaginationEnabled = paginationEnabled;
    }

    private boolean mPaginationEnabled = true;
    private boolean mRefreshing;

    public final static int PAGE_LENGTH = 100;


    public abstract T[] refresh() throws TwitterException;

    public abstract T[] paginate() throws TwitterException;


    private void performRefresh() {
        if (mRefreshing)
            return;
        mRefreshing = true;
        Log.d("CacheableListFragment", "performRefresh()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final T[] items = refresh();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getAdapter().add(items, false);
                        }
                    });
                } catch (final TwitterException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (e.exceededRateLimitation()) {
                                AppMsg.makeText(getActivity(), R.string.rate_limit_error, AppMsg.STYLE_ALERT).show();
                            } else {
                                AppMsg.makeText(getActivity(), e.getMessage(), AppMsg.STYLE_ALERT).show();
                            }
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshing = false;
                        setListShown(true);
                        Activity act = getActivity();
                        if (act instanceof DrawerActivity)
                            ((DrawerActivity) act).getPullToRefreshAttacher().setRefreshComplete();
                    }
                });
            }
        }).start();
    }

    private void performPaginate() {
        if (mRefreshing)
            return;
        mRefreshing = true;
        Activity act = getActivity();
        if (act instanceof DrawerActivity)
            ((DrawerActivity) act).getPullToRefreshAttacher().setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final T[] items = paginate();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getAdapter().add(items, true);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppMsg.makeText(getActivity(), e.getMessage(), AppMsg.STYLE_ALERT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshing = false;
                        Activity act = getActivity();
                        if (act instanceof DrawerActivity)
                            ((DrawerActivity) act).getPullToRefreshAttacher().setRefreshComplete();
                    }
                });
            }
        }).start();
    }


    @Override
    public void onStart() {
        super.onStart();

        // If pagination is enabled, listen for scroll events in the list view
        if (mPaginationEnabled) {
            getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (visibleItemCount < totalItemCount && (firstVisibleItem + visibleItemCount) == totalItemCount) {
                        performPaginate();
                    }
                }
            });
        }

        // Attach to action bar pull to refresh
        Activity act = getActivity();
        if (act instanceof DrawerActivity) {
            ((DrawerActivity) act).getPullToRefreshAttacher().setRefreshableView(getListView(), new PullToRefreshAttacher.OnRefreshListener() {
                @Override
                public void onRefreshStarted(View view) {
                    performRefresh();
                }
            });
        }
    }

    @Override
    public final void onCacheEmpty() {
        // Refresh the list if the column cache is empty
        performRefresh();
    }
}

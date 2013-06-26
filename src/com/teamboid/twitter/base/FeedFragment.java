package com.teamboid.twitter.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.devspark.appmsg.AppMsg;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Provides a standardized base for list fragments that retrieve and display a feed. Also handles
 * attaching to action bar refresh.
 *
 * @param <T> The class contained in the fragment's list adapter, usually Status or DirectMessage.
 * @author Aidan Follestad (afollestad)
 */
public abstract class FeedFragment<T> extends BoidListFragment {

    public abstract T[] refresh() throws Exception;

    private void performRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final T[] items = refresh();
                    if (getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getAdapter().add(items);
                            setListShown(true);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    if (getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppMsg.makeText(getActivity(), e.getMessage(), AppMsg.STYLE_ALERT).show();
                            setListShown(true);

                            Activity act = getActivity();
                            if (act instanceof DrawerActivity)
                                ((DrawerActivity) act).getPullToRefreshAttacher().setRefreshComplete();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity act = getActivity();
        if (act instanceof DrawerActivity) {
            ((DrawerActivity) act).getPullToRefreshAttacher().setRefreshableView(getListView(), new PullToRefreshAttacher.OnRefreshListener() {
                @Override
                public void onRefreshStarted(View view) {
                    performRefresh();
                }
            });
        }

        setListShown(false);
        performRefresh();
    }
}

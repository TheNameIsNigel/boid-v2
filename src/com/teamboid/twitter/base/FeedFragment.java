package com.teamboid.twitter.base;

import android.os.Bundle;
import android.view.View;
import com.devspark.appmsg.AppMsg;

/**
 * Provides a standardized base for list fragments that retrieve and display a feed.
 *
 * @param <T> The class contained in the fragment's list adapter, usually Status or DirectMessage.
 *
 * @author Aidan Follestad (afollestad)
 */
public abstract class FeedFragment<T> extends BoidListFragment {

    public abstract T[] refresh() throws Exception;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListShown(false);
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
                        }
                    });
                }
            }
        }).start();
    }
}

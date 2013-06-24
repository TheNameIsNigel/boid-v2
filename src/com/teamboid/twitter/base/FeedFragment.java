package com.teamboid.twitter.base;

import android.os.Bundle;
import android.view.View;
import com.devspark.appmsg.AppMsg;

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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getAdapter().add(items);
                            setListShown(true);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppMsg.makeText(getActivity(), e.getMessage(), AppMsg.STYLE_ALERT).show();
                        }
                    });
                }
            }
        }).start();
    }
}

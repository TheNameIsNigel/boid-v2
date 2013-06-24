package com.teamboid.twitter.base;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;

public abstract class BoidListFragment<T> extends ListFragment {

    public abstract int getTitle();

    public abstract int getEmptyText();

    public abstract BoidAdapter<T> getAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(getTitle());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(getAdapter());
        setEmptyText(getString(getEmptyText()));
    }
}

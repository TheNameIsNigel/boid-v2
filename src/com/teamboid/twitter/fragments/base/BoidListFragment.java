package com.teamboid.twitter.fragments.base;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.BoidAdapter;

/**
 * Provides a standardized base for all fragments that contain a list.
 *
 * @param <T> The class contained in the fragment's {@link com.teamboid.twitter.adapters.BoidAdapter}.
 * @author Aidan Follestad (afollestad)
 */
public abstract class BoidListFragment<T> extends CacheableFragment<T> {

    public BoidListFragment(boolean cachingEnabled) {
        super(cachingEnabled);
    }

    public final static int CACHE_LIMIT = 1000;


    public abstract int getEmptyText();

    public abstract BoidAdapter<T> initializeAdapter();

    public abstract void onItemClicked(int index, T item);

    public abstract boolean onItemLongClicked(int index, T item);


    public final void setListShown(boolean shown) {
        mListView.setVisibility(shown ? View.VISIBLE : View.GONE);
        if (!shown) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mListView.setEmptyView(mEmptyView);
            getAdapter().notifyDataSetChanged();
        }
        mProgressView.setVisibility(shown ? View.GONE : View.VISIBLE);
    }

    public final ListView getListView() {
        return mListView;
    }

    public final void runOnUiThread(Runnable runnable) {
        Activity act = getActivity();
        if (act == null)
            return;
        act.runOnUiThread(runnable);
    }


    private ListView mListView;
    private TextView mEmptyView;
    private ProgressBar mProgressView;
    private BoidAdapter<T> mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = initializeAdapter();
        setRetainInstance(true);
        getActivity().setTitle(getTitle());
    }

    @Override
    public void onResume() {
        // Prepare for cache to be read or full refresh
        setListShown(false);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.boid_list_fragment, null);
        mListView = (ListView) view.findViewById(R.id.list);
        mEmptyView = (TextView) view.findViewById(R.id.empty);
        mProgressView = (ProgressBar) view.findViewById(R.id.progress);

        mListView.setAdapter(getAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemClicked(i, mAdapter.getItem(i));
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return onItemLongClicked(i, mAdapter.getItem(i));
            }
        });
        mListView.setEmptyView(mEmptyView);
        mEmptyView.setText(getString(getEmptyText()));

        return view;
    }

    @Override
    public final T[] getCacheWriteables() {
        // Save the scroll position right before the fragment caches itself
        saveScrollPos();
        return mAdapter.toArray(CACHE_LIMIT);
    }

    @Override
    public final void onCacheRead(T[] contents) {
        mAdapter.set(contents);
        setListShown(true);
        // Restore the previously cached scroll position, if possible
        restoreScrollPos(0);
    }

    public final BoidAdapter<T> getAdapter() {
        return mAdapter;
    }

    public final void saveScrollPos() {
        int mSavedIndex = mListView.getFirstVisiblePosition();
        View v = mListView.getChildAt(0);
        int mSavedFromTop = (v == null) ? 0 : v.getTop();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putInt("saved_index", mSavedIndex).putInt("saved_top", mSavedFromTop).commit();
    }

    public final void restoreScrollPos(int addedCount) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int mSavedIndex = prefs.getInt("saved_index", -1);
        if (mSavedIndex == -1)
            return;
        else if (mSavedIndex > getAdapter().getCount() - 1) {
            // The saved scroll position is out of date with the cache
            prefs.edit().remove("saved_index").remove("saved_top").commit();
            return;
        }
        int mSavedFromTop = prefs.getInt("saved_top", 0);
        mListView.clearFocus();
        mListView.setSelectionFromTop(mSavedIndex + addedCount, mSavedFromTop);
        mListView.requestFocus();
    }
}
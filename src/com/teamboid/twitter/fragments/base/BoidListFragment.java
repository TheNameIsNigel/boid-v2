package com.teamboid.twitter.fragments.base;

import android.app.Activity;
import android.os.Bundle;
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
        return mAdapter.toArray();
    }

    @Override
    public final void onCacheRead(T[] contents) {
        mAdapter.set(contents);
        setListShown(true);
    }

    public final BoidAdapter<T> getAdapter() {
        return mAdapter;
    }
}
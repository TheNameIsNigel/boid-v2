package com.teamboid.twitter.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.teamboid.twitter.R;

public abstract class BoidListFragment<T> extends Fragment {

    public abstract int getTitle();

    public abstract int getEmptyText();

    public abstract BoidAdapter<T> getAdapter();

    public abstract void onItemClicked(int index);

    public final void setListShown(boolean shown) {
        //TODO
    }

    public final void setListAdapter(BoidAdapter<T> adapter) {
        mListView.setAdapter(adapter);
    }

    public final ListView getListView() {
        return mListView;
    }


    private ListView mListView;
    private TextView mEmptyView;

    public void ensureViews() {
        if (getView() == null)
            return;
        if (mListView == null)
            mListView = (ListView) getView().findViewById(R.id.list);
        if (mEmptyView == null)
            mEmptyView = (TextView) getView().findViewById(R.id.empty);
    }


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.boid_list_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureViews();
        mListView.setAdapter(getAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemClicked(i);
            }
        });
        mListView.setEmptyView(mEmptyView);
        mEmptyView.setText(getString(getEmptyText()));
    }
}

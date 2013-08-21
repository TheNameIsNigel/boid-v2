package com.teamboid.twitter.fragments;

import android.content.Intent;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.TrendAdapter;
import com.teamboid.twitter.fragments.base.BoidListFragment;
import com.teamboid.twitter.ui.SearchActivity;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;

/**
 * A feed fragment that displays Twitter trends.
 */
public class TrendsFragment extends BoidListFragment<Trend> {

    public TrendsFragment() {
        super("trends");
    }

    @Override
    public boolean getShouldShowLastUpdated() {
        return getLastUpdatedTime() != null;
    }

    @Override
    public int getEmptyText() {
        return R.string.no_trends;
    }

    @Override
    public SilkAdapter<Trend> initializeAdapter() {
        return new TrendAdapter(getActivity());
    }

    @Override
    public void onItemTapped(int index, Trend item, View view) {
        startActivity(new Intent(getActivity(), SearchActivity.class)
                .putExtra("query", item.getName()));
    }

    @Override
    public boolean onItemLongTapped(int index, Trend item, View view) {
        //TODO
        return false;
    }

    @Override
    public Trend[] refresh() throws TwitterException {
        // TODO implement location based trends
        Trends trends = BoidApp.get(getActivity()).getClient().getPlaceTrends(1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getAdapter().clear();
            }
        });
        return trends.getTrends();
    }

    @Override
    public String getTitle() {
        return getString(R.string.trends);
    }
}

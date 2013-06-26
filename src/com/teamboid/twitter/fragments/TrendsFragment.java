package com.teamboid.twitter.fragments;

import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.TrendAdapter;
import com.teamboid.twitter.base.BoidAdapter;
import com.teamboid.twitter.base.FeedFragment;
import twitter4j.Trend;

/**
 * A feed fragment that displays Twitter trends.
 */
public class TrendsFragment extends FeedFragment<Trend> {

    private TrendAdapter adapter;

    @Override
    public int getEmptyText() {
        return R.string.no_trends;
    }

    @Override
    public BoidAdapter<Trend> getAdapter() {
        if (adapter == null)
            adapter = new TrendAdapter(getActivity());
        return adapter;
    }

    @Override
    public void onItemClicked(int index) {
        //TODO
    }

    @Override
    public boolean onItemLongClicked(int index) {
        //TODO
        return false;
    }

    @Override
    public Trend[] refresh() throws Exception {
        // TODO implement location based trends
        return BoidApp.get(getActivity()).getClient().getPlaceTrends(1).getTrends();
    }

    @Override
    public Trend[] paginate() throws Exception {
        return null;
    }

    @Override
    public String getTitle() {
        return getString(R.string.trends);
    }
}

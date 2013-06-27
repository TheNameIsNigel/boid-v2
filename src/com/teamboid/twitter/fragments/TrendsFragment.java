package com.teamboid.twitter.fragments;

import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.BoidAdapter;
import com.teamboid.twitter.adapters.TrendAdapter;
import com.teamboid.twitter.fragments.base.FeedFragment;
import twitter4j.Trend;
import twitter4j.TwitterException;

/**
 * A feed fragment that displays Twitter trends.
 */
public class TrendsFragment extends FeedFragment<Trend> {

    public TrendsFragment() {
        super(false, true);
    }

    @Override
    public int getEmptyText() {
        return R.string.no_trends;
    }

    @Override
    public BoidAdapter<Trend> getAdapter() {
        return new TrendAdapter(getActivity());
    }

    @Override
    public void onItemClicked(int index, Trend item) {
        //TODO
    }

    @Override
    public boolean onItemLongClicked(int index, Trend item) {
        //TODO
        return false;
    }

    @Override
    public Trend[] refresh() throws TwitterException {
        // TODO implement location based trends
        return BoidApp.get(getActivity()).getClient().getPlaceTrends(1).getTrends();
    }

    @Override
    public Trend[] paginate() throws TwitterException {
        return null;
    }

    @Override
    public String getTitle() {
        return getString(R.string.trends);
    }
}

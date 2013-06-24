package com.teamboid.twitter.fragments;

import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.StatusAdapter;
import com.teamboid.twitter.base.BoidAdapter;
import com.teamboid.twitter.base.FeedFragment;
import twitter4j.Paging;
import twitter4j.Status;

public class TimelineFragment extends FeedFragment<Status> {

    private StatusAdapter adapter;

    @Override
    public int getEmptyText() {
        return R.string.no_tweets;
    }

    @Override
    public BoidAdapter<Status> getAdapter() {
        if (adapter == null)
            adapter = new StatusAdapter(getActivity());
        return adapter;
    }

    @Override
    public Status[] refresh() throws Exception {
        return BoidApp.get(getActivity()).getClient().getHomeTimeline(new Paging(100)).toArray(new Status[0]);
    }

    @Override
    public int getTitle() {
        return R.string.timeline;
    }
}

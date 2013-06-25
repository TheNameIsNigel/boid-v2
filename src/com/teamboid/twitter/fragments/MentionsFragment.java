package com.teamboid.twitter.fragments;

import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.StatusAdapter;
import com.teamboid.twitter.base.BoidAdapter;
import com.teamboid.twitter.base.FeedFragment;
import twitter4j.Status;

public class MentionsFragment extends FeedFragment<Status> {

    private StatusAdapter adapter;

    @Override
    public int getEmptyText() {
        return R.string.no_mentions;
    }

    @Override
    public BoidAdapter<Status> getAdapter() {
        if (adapter == null)
            adapter = new StatusAdapter(getActivity());
        return adapter;
    }

    @Override
    public void onItemClicked(int index) {
        //TODO
    }

    @Override
    public void nullifyAdapter() {
        adapter = null;
    }

    @Override
    public Status[] refresh() throws Exception {
        return BoidApp.get(getActivity()).getClient().getMentionsTimeline().toArray(new Status[0]);
    }

    @Override
    public int getTitle() {
        return R.string.mentions;
    }
}

package com.teamboid.twitter.fragments;

import android.content.Intent;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.ComposeActivity;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.StatusAdapter;
import com.teamboid.twitter.base.BoidAdapter;
import com.teamboid.twitter.base.FeedFragment;
import twitter4j.Status;

/**
 * A feed fragment that displays the current user's mentions.
 */
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
    public boolean onItemLongClicked(int index) {
        Status status = adapter.getItem(index);
        startActivity(new Intent(getActivity(), ComposeActivity.class)
                .putExtra("mention", status.getUser().getScreenName())
                .putExtra("reply_to", status.getId()));
        return true;
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

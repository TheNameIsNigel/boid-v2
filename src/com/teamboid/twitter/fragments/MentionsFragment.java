package com.teamboid.twitter.fragments;

import android.content.Intent;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.ComposeActivity;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.BoidAdapter;
import com.teamboid.twitter.adapters.StatusAdapter;
import com.teamboid.twitter.fragments.base.FeedFragment;
import com.teamboid.twitter.utilities.Utils;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * A feed fragment that displays the current user's mentions.
 */
public class MentionsFragment extends FeedFragment<Status> {

    public MentionsFragment() {
        super(true, true);
    }

    @Override
    public int getEmptyText() {
        return R.string.no_mentions;
    }

    @Override
    public BoidAdapter<Status> getAdapter() {
        return new StatusAdapter(getActivity());
    }

    @Override
    public void onItemClicked(int index, Status status) {
        //TODO
    }

    @Override
    public boolean onItemLongClicked(int index, Status status) {
        startActivity(new Intent(getActivity(), ComposeActivity.class)
                .putExtra("reply_to", Utils.serializeObject(status)));
        return true;
    }

    @Override
    public Status[] refresh() throws TwitterException {
        Paging paging = new Paging();
        paging.setCount(PAGE_LENGTH);
        if (getAdapter().getCount() > 0) {
            // Get tweets newer than the most recent tweet in the adapter
            paging.setSinceId(getAdapter().getItemId(0));
        }
        return BoidApp.get(getActivity()).getClient().getMentionsTimeline(paging).toArray(new Status[0]);
    }

    @Override
    public Status[] paginate() throws TwitterException {
        Paging paging = new Paging();
        paging.setCount(PAGE_LENGTH);
        BoidAdapter adapt = getAdapter();
        if (adapt.getCount() > 0) {
            // Get tweets older than the oldest tweet in the adapter
            paging.setMaxId(adapt.getItemId(adapt.getCount() - 1) - 1);
        }
        return BoidApp.get(getActivity()).getClient().getMentionsTimeline(paging).toArray(new Status[0]);
    }

    @Override
    public String getTitle() {
        return getString(R.string.mentions);
    }
}

package com.teamboid.twitter.fragments;

import android.content.Intent;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.StatusAdapter;
import com.teamboid.twitter.fragments.base.BoidListFragment;
import com.teamboid.twitter.ui.ComposeActivity;
import com.teamboid.twitter.ui.TweetViewerActivity;
import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A feed fragment that displays the current user's home timeline.
 */
public class TimelineFragment extends BoidListFragment<Status> {

    public TimelineFragment() {
        super("timeline");
    }

    @Override
    public int getEmptyText() {
        return R.string.no_tweets;
    }

    @Override
    public SilkAdapter<Status> initializeAdapter() {
        return new StatusAdapter(getActivity());
    }

    @Override
    public void onItemTapped(int index, Status status, View view) {
        startActivity(new Intent(getActivity(), TweetViewerActivity.class).putExtra("tweet", status));
    }

    @Override
    public boolean onItemLongTapped(int index, Status status, View view) {
        startActivity(new Intent(getActivity(), ComposeActivity.class).putExtra("reply_to", status));
        return true;
    }

//    @Override
//    public Status[] paginate() throws TwitterException {
//        Paging paging = new Paging();
//        paging.setCount(PAGE_LENGTH);
//        BoidAdapter adapt = getAdapter();
//        if (adapt.getCount() > 0) {
//            // Get tweets older than the oldest tweet in the adapter
//            paging.setMaxId(adapt.getItemId(adapt.getCount() - 1) - 1);
//        }
//        return BoidApp.get(getActivity()).getClient().getHomeTimeline(paging).toArray(new Status[0]);
//    }

    @Override
    public String getTitle() {
        return getString(R.string.timeline);
    }

    @Override
    protected List<Status> load(Twitter client, Paging paging) throws Exception {
        return client.getHomeTimeline(paging);
    }

    @Override
    protected long getItemId(Status item) {
        return item.getId();
    }

    @Override
    protected boolean isPaginationEnabled() {
        return true;
    }
}

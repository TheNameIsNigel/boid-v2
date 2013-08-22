package com.afollestad.twitter.fragments;

import android.content.Intent;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.StatusAdapter;
import com.afollestad.twitter.fragments.base.BoidListFragment;
import com.afollestad.twitter.ui.ComposeActivity;
import com.afollestad.twitter.ui.TweetViewerActivity;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;

import java.util.List;

/**
 * A feed fragment that displays tweet search results.
 */
public class SearchFragment extends BoidListFragment<Status> {

    public SearchFragment(String query, boolean cache) {
        super(cache ? query + "_search" : null);
        mQuery = query;
    }

    private String mQuery;

    @Override
    public int getEmptyText() {
        return R.string.no_tweets;
    }

    @Override
    public SilkAdapter<Status> initializeAdapter() {
        return new StatusAdapter(getActivity(), false);
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
        return mQuery;
    }

    @Override
    protected List<Status> load(Twitter client, Paging paging) throws Exception {
        Query q = new Query(mQuery);
        if (paging != null) {
            q.setCount(paging.getCount());
            q.setSinceId(paging.getSinceId());
            q.setMaxId(paging.getMaxId());
        }
        return client.search(q).getTweets();
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

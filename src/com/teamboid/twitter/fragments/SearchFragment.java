package com.teamboid.twitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.StatusAdapter;
import com.teamboid.twitter.fragments.base.BoidListFragment;
import com.teamboid.twitter.ui.ComposeActivity;
import com.teamboid.twitter.ui.TweetViewerActivity;
import twitter4j.*;

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

    @Override
    public Status[] refresh() throws TwitterException {
        Paging paging = new Paging();
        paging.setCount(getPageLength());
        if (getAdapter().getCount() > 0) {
            // Get tweets newer than the most recent tweet in the adapter
            paging.setSinceId(getAdapter().getItemId(0));
        }
        Query q = new Query(mQuery);
        q.setCount(200);
        QueryResult response = BoidApp.get(getActivity()).getClient().search(q);
        return response.getTweets().toArray(new Status[response.getTweets().size()]);
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
}

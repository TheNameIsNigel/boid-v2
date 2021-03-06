package com.afollestad.twitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.StatusAdapter;
import com.afollestad.twitter.columns.Column;
import com.afollestad.twitter.columns.Columns;
import com.afollestad.twitter.fragments.base.BoidListFragment;
import com.afollestad.twitter.ui.ComposeActivity;
import com.afollestad.twitter.ui.TweetViewerActivity;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.internal.json.StatusJSONImpl;

import java.util.List;

/**
 * A feed fragment that displays tweet search results.
 *
 * @author Aidan Follestad (afollestad)
 */
public class SearchFragment extends BoidListFragment<Status> {

    private String mQuery;

    @Override
    public String getCacheName() {
        return getCacheEnabled() ? new Column(Status.class, Column.SEARCH, mQuery).toString() : null;
    }

    @Override
    public Class getCacheClass() {
        return StatusJSONImpl.class;
    }

    protected boolean getCacheEnabled() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mQuery = getArguments().getString("query");
    }

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
        return false;
    }

    @Override
    public String getTitle() {
        return mQuery;
    }

    @Override
    protected boolean doesCacheExpire() {
        return false;
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
    protected boolean isPaginationEnabled() {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_search, menu);
        menu.findItem(R.id.tweetWithQuery).setTitle(getString(R.string.tweet_with_query).replace("{query}", mQuery));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pin:
                Columns.add(getActivity(), new Column(Status.class, Column.SEARCH, mQuery));
                getActivity().finish();
                return true;
            case R.id.tweetWithQuery:
                startActivity(new Intent(getActivity(), ComposeActivity.class)
                        .putExtra("content", mQuery + " "));
                return true;
            case R.id.save:
                //TODO
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
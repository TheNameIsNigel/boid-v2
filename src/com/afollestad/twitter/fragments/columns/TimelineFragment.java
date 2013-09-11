package com.afollestad.twitter.fragments.columns;

import android.content.Intent;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.StatusAdapter;
import com.afollestad.twitter.columns.Column;
import com.afollestad.twitter.fragments.base.BoidListFragment;
import com.afollestad.twitter.ui.TweetViewerActivity;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.internal.json.StatusJSONImpl;

import java.util.List;

/**
 * A feed fragment that displays the current user's home timeline.
 *
 * @author Aidan Follestad (afollestad)
 */
public class TimelineFragment extends BoidListFragment<Status> {

    @Override
    public String getCacheName() {
        return new Column(Status.class, Column.TIMELINE).toString();
    }

    @Override
    public Class getCacheClass() {
        return StatusJSONImpl.class;
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
        return false;
    }

    @Override
    public String getTitle() {
        return getString(R.string.timeline);
    }

    @Override
    protected boolean doesCacheExpire() {
        return true;
    }

    @Override
    protected List<Status> load(Twitter client, Paging paging) throws Exception {
        return client.getHomeTimeline(paging);
    }

    @Override
    protected boolean isPaginationEnabled() {
        return true;
    }
}

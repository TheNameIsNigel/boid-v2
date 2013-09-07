package com.afollestad.twitter.fragments.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.UserAdapter;
import com.afollestad.twitter.columns.Column;
import com.afollestad.twitter.columns.Columns;
import com.afollestad.twitter.fragments.base.BoidListFragment;
import com.afollestad.twitter.ui.ComposeActivity;
import com.afollestad.twitter.ui.ProfileActivity;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;

import java.util.List;

/**
 * A feed fragment that displays user search results.
 *
 * @author Aidan Follestad (afollestad)
 */
public class UserSearchFragment extends BoidListFragment<User> {

    private String mQuery;

    @Override
    public String getCacheName() {
        return getCacheEnabled() ? new Column(User.class, Column.SEARCH, mQuery).toString() : null;
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
    public SilkAdapter<User> initializeAdapter() {
        return new UserAdapter(getActivity());
    }

    @Override
    public void onItemTapped(int index, User user, View view) {
        startActivity(new Intent(getActivity(), ProfileActivity.class).putExtra("user", user));
    }

    @Override
    public boolean onItemLongTapped(int index, User user, View view) {
        return false;
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
    protected List<User> load(Twitter client, Paging paging) throws Exception {
        return client.searchUsers(mQuery, 0); //TODO pagination
    }

    @Override
    protected long getItemId(User item) {
        return item.getId();
    }

    @Override
    protected boolean isPaginationEnabled() {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pin:
                Columns.add(getActivity(), new Column(Status.class, Column.SEARCH, mQuery));
                getActivity().finish();
                return true;
            case R.id.compose:
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
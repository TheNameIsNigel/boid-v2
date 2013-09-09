package com.afollestad.twitter.fragments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.UserAdapter;
import com.afollestad.twitter.fragments.base.BoidListFragment;
import com.afollestad.twitter.ui.ProfileActivity;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.internal.json.UserJSONImpl;

import java.util.List;

/**
 * Displays a user's followers.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ProfileFollowersViewer extends BoidListFragment<User> {

    private User mUser;

    @Override
    public String getCacheName() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mUser = (User) getArguments().getSerializable("user");
    }

    @Override
    protected void onPostLoad(List<User> results, boolean paginated) {
        super.onPostLoad(results, paginated);
        if (results == null || results.size() == 0 || getAdapter().getCount() == mUser.getFollowersCount()) {
            getAdapter().add(new UserJSONImpl(true));
            getListView().smoothScrollToPosition(getAdapter().getCount());
        }
    }

    @Override
    public int getEmptyText() {
        return R.string.no_users;
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

    @Override
    public String getTitle() {
        return getString(R.string.followers);
    }

    @Override
    protected boolean isPageCursorMode() {
        return true;
    }

    @Override
    protected int getAddIndex() {
        return -1;
    }

    @Override
    protected boolean doesCacheExpire() {
        return false;
    }

    @Override
    protected List<User> load(Twitter client, Paging paging) throws Exception {
        PagableResponseList<User> results = client.getFollowersList(mUser.getId(), getCursor());
        setCursor(results.getNextCursor());
        return results;
    }

    @Override
    protected boolean isPaginationEnabled() {
        return true;
    }
}
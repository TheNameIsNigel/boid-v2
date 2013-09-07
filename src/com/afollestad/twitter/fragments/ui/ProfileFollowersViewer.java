package com.afollestad.twitter.fragments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.UserAdapter;
import com.afollestad.twitter.fragments.base.BoidListFragment;
import com.afollestad.twitter.ui.ProfileActivity;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.User;

import java.util.List;

/**
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
    protected List<User> load(Twitter client, Paging paging) throws Exception {
        //TODO pagination cursor?
        return client.getFollowersList(mUser.getId(), getCursor());
    }

    @Override
    protected boolean isPaginationEnabled() {
        return true;
    }
}
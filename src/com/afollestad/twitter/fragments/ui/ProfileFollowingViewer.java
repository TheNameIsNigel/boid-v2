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
public class ProfileFollowingViewer extends BoidListFragment<User> {

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
        return getString(R.string.following);
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
        return client.getFriendsList(mUser.getId(), getCursor());
    }

    @Override
    protected long getItemId(User item) {
        return item.getId();
    }

    @Override
    protected boolean isPaginationEnabled() {
        return true;
    }
}
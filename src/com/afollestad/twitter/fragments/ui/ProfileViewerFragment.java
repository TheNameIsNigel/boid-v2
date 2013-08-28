package com.afollestad.twitter.fragments.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.silk.fragments.SilkFeedFragment;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.ProfileAdapter;
import com.afollestad.twitter.ui.ComposeActivity;
import com.afollestad.twitter.ui.TweetViewerActivity;
import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ProfileViewerFragment extends SilkFeedFragment<Status> {

    private User mUser;
    private User mProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments().containsKey("screen_name")) loadUser();
        else mUser = (User) getArguments().getSerializable("user");
        mProfile = BoidApp.get(getActivity()).getProfile();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int res = R.menu.fragment_profile_viewer;
        if (mUser != null && mUser.getId() == mProfile.getId())
            res = R.menu.fragment_profile_viewer_me;
        inflater.inflate(res, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mention:
                startActivity(new Intent(getActivity(), ComposeActivity.class)
                        .putExtra("mention", mUser));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUser() {
        final String screenName = getArguments().getString("screen_name");
        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage(getString(R.string.please_wait));
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.show();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Twitter client = BoidApp.get(getActivity()).getClient();
                try {
                    mUser = client.showUser(screenName);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().getActionBar().setTitle("@" + mUser.getScreenName());
                            getActivity().invalidateOptionsMenu();
                            performRefresh(true);
                        }
                    });
                } catch (final TwitterException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                    });
                } finally {
                    mDialog.dismiss();
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    @Override
    protected List<Status> refresh() throws Exception {
        if (mUser == null) return new ArrayList<Status>();
        ((ProfileAdapter) getAdapter()).setUser(mUser);

        Twitter client = BoidApp.get(getActivity()).getClient();
        if (mProfile.getId() == mUser.getId()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ProfileAdapter) getAdapter()).setFollowing(ProfileAdapter.FollowingType.NONE);
                }
            });
        } else {
            try {
                final Relationship friendship = client.showFriendship(mProfile.getId(), mUser.getId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ProfileAdapter) getAdapter()).setFollowing(
                                friendship.isSourceFollowingTarget() ? ProfileAdapter.FollowingType.FOLLOWING : ProfileAdapter.FollowingType.UNFOLLOWING,
                                friendship.isTargetFollowingSource() ? ProfileAdapter.FollowingType.FOLLOWING : ProfileAdapter.FollowingType.UNFOLLOWING
                        );
                    }
                });
            } catch (final TwitterException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ProfileAdapter) getAdapter()).setFollowing(ProfileAdapter.FollowingType.UNKNOWN);
                        BoidApp.showAppMsgError(getActivity(), e);
                    }
                });
            }
        }

        Paging paging = new Paging();
        paging.setCount(200);
        return client.getUserTimeline(mUser.getId(), paging);
    }

    @Override
    protected void onError(Exception message) {
        BoidApp.showAppMsgError(getActivity(), message);
    }

    @Override
    protected int getEmptyText() {
        return R.string.no_tweets;
    }

    @Override
    protected SilkAdapter<Status> initializeAdapter() {
        return new ProfileAdapter(getActivity());
    }

    @Override
    protected void onItemTapped(int index, Status item, View view) {
        startActivity(new Intent(getActivity(), TweetViewerActivity.class).putExtra("tweet", item));
    }

    @Override
    protected boolean onItemLongTapped(int index, Status item, View view) {
        return false;
    }

    @Override
    protected void onVisibilityChange(boolean visible) {
    }

    @Override
    public String getTitle() {
        if (mUser == null) return getString(R.string.profile);
        return "@" + mUser.getScreenName();
    }
}
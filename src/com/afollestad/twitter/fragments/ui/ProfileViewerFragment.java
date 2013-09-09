package com.afollestad.twitter.fragments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.ProfileAdapter;
import com.afollestad.twitter.fragments.base.BoidListFragment;
import com.afollestad.twitter.ui.ComposeActivity;
import com.afollestad.twitter.ui.TweetViewerActivity;
import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The fragment that displays all information and tweets for a profile, contained in a {@link com.afollestad.twitter.ui.ProfileActivity} on phones.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ProfileViewerFragment extends BoidListFragment<Status> {

    private User mUser;
    private User mProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mUser = (User) getArguments().getSerializable("user");
        mProfile = BoidApp.get(getActivity()).getProfile();
    }

    @Override
    protected int getAddIndex() {
        return 2;
    }

    @Override
    protected boolean doesCacheExpire() {
        return false;
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

    @Override
    public String getCacheName() {
        return null;
    }

    @Override
    protected void onPostLoad(List<Status> results, boolean paginated) {
        super.onPostLoad(results, paginated);
        if (mProfile.getId() == mUser.getId()) {
            // Update profile cache
            BoidApp.get(getActivity()).storeProfile(mUser);
        }
    }

    @Override
    protected List<Status> load(Twitter client, Paging paging) throws Exception {
        if (mUser == null) {
            if (getArguments().containsKey("screen_name")) {
                // If a 'screen_name' intent extra was passed, the user must be manually loaded now
                mUser = client.showUser(getArguments().getString("screen_name"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getActionBar().setTitle("@" + mUser.getScreenName());
                        getActivity().invalidateOptionsMenu();
                    }
                });
            } else return new ArrayList<Status>();
        } else mUser = client.showUser(mUser.getId());

        ((ProfileAdapter) getAdapter()).setUser(mUser);
        if (mProfile.getId() == mUser.getId()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Indicates that you're viewing your own profile, so hide the follow button
                    ((ProfileAdapter) getAdapter()).setFollowing(ProfileAdapter.FollowingType.NONE);
                }
            });
        } else {
            try {
                final Relationship friendship = client.showFriendship(mProfile.getId(), mUser.getId());
                if (mUser.isProtected() && !friendship.isSourceFollowingTarget()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ProfileAdapter) getAdapter()).setFollowing(ProfileAdapter.FollowingType.UNFOLLOWING);
                        }
                    });
                    return new ArrayList<Status>();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ProfileAdapter) getAdapter()).setFollowing(
                                    friendship.isSourceFollowingTarget() ? ProfileAdapter.FollowingType.FOLLOWING : ProfileAdapter.FollowingType.UNFOLLOWING,
                                    friendship.isTargetFollowingSource() ? ProfileAdapter.FollowingType.FOLLOWING : ProfileAdapter.FollowingType.UNFOLLOWING
                            );
                        }
                    });
                }
            } catch (final TwitterException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ProfileAdapter) getAdapter()).setFollowing(ProfileAdapter.FollowingType.UNKNOWN);
                        BoidApp.showAppMsgError(getActivity(), e);
                    }
                });
                return new ArrayList<Status>();
            }
        }
        return client.getUserTimeline(mUser.getId(), paging);
    }

    @Override
    protected boolean isPaginationEnabled() {
        return true;
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
    public String getTitle() {
        if (mUser == null) return getString(R.string.profile);
        return "@" + mUser.getScreenName();
    }
}
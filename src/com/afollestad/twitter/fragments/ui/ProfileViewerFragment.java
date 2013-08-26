package com.afollestad.twitter.fragments.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.afollestad.silk.Silk;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.silk.fragments.SilkFeedFragment;
import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.silk.views.image.SilkImageView;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.ProfileAdapter;
import com.afollestad.twitter.ui.ComposeActivity;
import com.afollestad.twitter.ui.TweetViewerActivity;
import twitter4j.*;

import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ProfileViewerFragment extends SilkFeedFragment<Status> {

    private User mUser;
    private User mProfile;
    private boolean mFollowingThem;
    private boolean mFollowedBy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mUser = (User) getArguments().getSerializable("user");
        mProfile = BoidApp.get(getActivity()).getProfile();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int res = R.menu.fragment_profile_viewer;
        if (mUser.getId() == mProfile.getId())
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SilkImageManager loader = BoidApp.get(getActivity()).getImageLoader();
        ((SilkImageView) view.findViewById(R.id.profilePic)).setImageURL(loader, mUser.getBiggerProfileImageURL());
        ((SilkImageView) view.findViewById(R.id.headerImage)).setImageURL(loader,
                Silk.isTablet(getActivity()) ? mUser.getProfileBannerIPadRetinaURL() : mUser.getProfileBannerMobileRetinaURL());
        ((TextView) view.findViewById(R.id.username)).setText(mUser.getName());
        ((TextView) view.findViewById(R.id.description)).setText(mUser.getDescription());
        loadFollowButton((Button) view.findViewById(R.id.follow));
    }

    private void loadFollowButton(final Button button) {
        if (mProfile.getId() == mUser.getId()) {
            button.setVisibility(View.GONE);
            return;
        }
        button.setVisibility(View.VISIBLE);
        button.setText(R.string.loading);
        button.setEnabled(false);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Twitter client = BoidApp.get(getActivity()).getClient();
                try {
                    final Relationship friendship = client.showFriendship(mProfile.getId(), mUser.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFollowingThem = friendship.isSourceFollowingTarget();
                            mFollowedBy = friendship.isTargetFollowingSource();
                            invalidateFollowButton();
                        }
                    });
                } catch (final TwitterException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setText(R.string.error);
                            BoidApp.showAppMsgError(getActivity(), e);
                        }
                    });
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFollowAction(false);
            }
        });
    }

    private void performFollowAction(boolean confirmed) {
        final Button button = (Button) getView().findViewById(R.id.follow);
        if (!confirmed && mFollowingThem) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.confirm_unfollow_title)
                    .setMessage(getString(R.string.confirm_unfollow).replace("{screenname}", mUser.getScreenName()))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            performFollowAction(true);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            return;
        }

        button.setText(R.string.please_wait);
        button.setEnabled(false);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Twitter client = BoidApp.get(getActivity()).getClient();
                try {
                    if (mFollowingThem) {
                        client.destroyFriendship(mUser.getId());
                        mFollowingThem = false;
                    } else {
                        client.createFriendship(mUser.getId());
                        mFollowingThem = true;
                    }
                    if (mUser.isProtected())
                        mUser = client.showUser(mUser.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invalidateFollowButton();
                        }
                    });
                } catch (final TwitterException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BoidApp.showAppMsgError(getActivity(), e);
                        }
                    });
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    private void invalidateFollowButton() {
        if (getView() == null) return;
        Button button = (Button) getView().findViewById(R.id.follow);
        if (mUser.isFollowRequestSent()) {
            button.setText(R.string.request_sent);
            button.setEnabled(false);
            return;
        }
        button.setEnabled(true);
        if (mFollowingThem && mFollowedBy)
            button.setText(R.string.follows_you_unfollow);
        else if (mFollowingThem)
            button.setText(R.string.unfollow);
        else if (mFollowedBy)
            button.setText(R.string.follows_you_follow_back);
        else button.setText(R.string.follow);
    }

    @Override
    protected List<Status> refresh() throws Exception {
        Paging paging = new Paging();
        paging.setCount(200);
        return BoidApp.get(getActivity()).getClient().getUserTimeline(mUser.getId(), paging);
    }

    @Override
    protected void onError(Exception message) {
        BoidApp.showAppMsgError(getActivity(), message);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_profile_viewer;
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
        return "@" + mUser.getScreenName();
    }
}

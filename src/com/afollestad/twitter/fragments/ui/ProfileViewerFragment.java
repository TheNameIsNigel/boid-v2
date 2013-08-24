package com.afollestad.twitter.fragments.ui;

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
import com.afollestad.twitter.adapters.StatusAdapter;
import com.afollestad.twitter.ui.ComposeActivity;
import com.afollestad.twitter.ui.TweetViewerActivity;
import com.afollestad.twitter.utilities.Utils;
import com.devspark.appmsg.AppMsg;
import twitter4j.*;

import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ProfileViewerFragment extends SilkFeedFragment<Status> {

    private User mUser;
    private boolean mFollowingThem;
    private boolean mFollowedBy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mUser = (User) getArguments().getSerializable("user");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_profile_viewer, menu);
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
        button.setText(R.string.loading);
        button.setEnabled(false);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Twitter client = BoidApp.get(getActivity()).getClient();
                User me = BoidApp.get(getActivity()).getProfile();
                try {
                    final Relationship friendship = client.showFriendship(me.getId(), mUser.getId());
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
                            AppMsg.makeText(getActivity(), Utils.processTwitterException(getActivity(), e), AppMsg.STYLE_ALERT).show();
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
                performFollowAction();
            }
        });
    }

    private void performFollowAction() {
        final Button button = (Button) getView().findViewById(R.id.follow);
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
                            AppMsg.makeText(getActivity(), Utils.processTwitterException(getActivity(), e), AppMsg.STYLE_ALERT).show();
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
        AppMsg.makeText(getActivity(), message.getMessage(), AppMsg.STYLE_ALERT).show();
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
        return new StatusAdapter(getActivity());
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

package com.afollestad.twitter.fragments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.devspark.appmsg.AppMsg;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.User;

import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ProfileViewerFragment extends SilkFeedFragment<Status> {

    private User mUser;

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

package com.afollestad.twitter.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.afollestad.silk.Silk;
import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.silk.views.image.SilkImageView;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.internal.json.StatusJSONImpl;

import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ProfileAdapter extends StatusAdapter {

    public enum FollowingType {
        UNKNOWN,
        FOLLOWING,
        UNFOLLOWING,
        NONE
    }

    public ProfileAdapter(Activity context) {
        super(context, true);
        mActivity = context;
    }

    private Activity mActivity;
    private User mUser;
    private FollowingType outward = FollowingType.UNKNOWN;
    private FollowingType inward = FollowingType.UNKNOWN;

    private void addHeaderViews() {
        super.add(0, new StatusJSONImpl(true, false));
        if (outward != FollowingType.NONE && inward != FollowingType.NONE)
            super.add(1, new StatusJSONImpl(false, true));
    }

    public void setUser(User user) {
        mUser = user;
    }

    public void setFollowing(FollowingType outwardInward) {
        setFollowing(outwardInward, outwardInward);
    }

    public void setFollowing(FollowingType outward, FollowingType inward) {
        this.outward = outward;
        this.inward = inward;
    }

    @Override
    public void set(List<Status> toSet) {
        super.set(toSet);
        addHeaderViews();
    }

    @Override
    public void clear() {
        super.clear();
        addHeaderViews();
    }

    @Override
    public boolean isEnabled(int position) {
        Status item = getItem(position);
        return !item.isProfileHeader() && !item.isProfileFollowButton();
    }

    private void setupHeader(View view) {
        SilkImageManager loader = BoidApp.get(mActivity).getImageLoader();
        ((SilkImageView) view.findViewById(R.id.profilePic)).setImageURL(loader, mUser.getBiggerProfileImageURL());
        ((SilkImageView) view.findViewById(R.id.headerImage)).setImageURL(loader,
                Silk.isTablet(mActivity) ? mUser.getProfileBannerIPadRetinaURL() : mUser.getProfileBannerMobileRetinaURL());
        ((TextView) view.findViewById(R.id.username)).setText(mUser.getName());
        ((TextView) view.findViewById(R.id.description)).setText(mUser.getDescription());
    }

    private void invalidateFollowButton(final Button button) {
        if (mUser.isFollowRequestSent()) {
            button.setText(R.string.request_sent);
            button.setEnabled(false);
            return;
        }
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
        if (outward == FollowingType.NONE && inward == FollowingType.NONE) {
            button.setVisibility(View.GONE);
            return;
        } else if (outward == FollowingType.UNKNOWN || inward == FollowingType.UNKNOWN)
            button.setText(R.string.error);
        else if (outward == FollowingType.FOLLOWING && inward == FollowingType.FOLLOWING)
            button.setText(R.string.follows_you_unfollow);
        else if (outward == FollowingType.FOLLOWING)
            button.setText(R.string.unfollow);
        else if (inward == FollowingType.FOLLOWING)
            button.setText(R.string.follows_you_follow_back);
        else button.setText(R.string.follow);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFollowAction(button, false);
            }
        });
    }

    private void performFollowAction(final Button button, boolean confirmed) {
        if (!confirmed && outward == FollowingType.FOLLOWING) {
            new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.confirm_unfollow_title)
                    .setMessage(mActivity.getString(R.string.confirm_unfollow).replace("{screenname}", mUser.getScreenName()))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            performFollowAction(button, true);
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
                Twitter client = BoidApp.get(mActivity).getClient();
                try {
                    if (outward == FollowingType.FOLLOWING) {
                        client.destroyFriendship(mUser.getId());
                        outward = FollowingType.UNFOLLOWING;
                    } else {
                        client.createFriendship(mUser.getId());
                        outward = FollowingType.FOLLOWING;
                    }
                    if (mUser.isProtected())
                        mUser = client.showUser(mUser.getId());
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invalidateFollowButton(button);
                        }
                    });
                } catch (final TwitterException e) {
                    e.printStackTrace();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BoidApp.showAppMsgError(mActivity, e);
                        }
                    });
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    @Override
    public View onViewCreated(int index, View recycled, Status item) {
        if (item.isProfileHeader()) {
            setupHeader(recycled);
            return recycled;
        } else if (item.isProfileFollowButton()) {
            invalidateFollowButton((Button) recycled);
            return recycled;
        } else recycled = super.onViewCreated(index, recycled, item);
        int top;
        if (index == 2) {
            top = getContext().getResources().getDimensionPixelSize(R.dimen.status_item_padding_bigger);
        } else top = getContext().getResources().getDimensionPixelSize(R.dimen.status_item_padding);
        recycled.setPadding(recycled.getPaddingLeft(), top, recycled.getPaddingRight(), recycled.getPaddingBottom());
        return recycled;
    }

    @Override
    public int getLayout(int index, int type) {
        Status item = getItem(index);
        if (item.isProfileHeader()) return R.layout.list_item_profile_header;
        else if (item.isProfileFollowButton()) return R.layout.list_item_followbtn;
        return super.getLayout(index, type);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        Status item = getItem(position);
        if (item.isProfileHeader()) return 1;
        else if (item.isProfileFollowButton()) return 2;
        return super.getItemViewType(position);
    }
}
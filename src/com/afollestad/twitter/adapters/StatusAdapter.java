package com.afollestad.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.silk.utilities.TimeUtils;
import com.afollestad.silk.views.image.SilkImageView;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.ProfileActivity;
import com.afollestad.twitter.utilities.TweetUtils;
import com.afollestad.twitter.utilities.text.BoidSpan;
import com.afollestad.twitter.utilities.text.TextUtils;
import twitter4j.Status;
import twitter4j.User;

/**
 * A list adapter that displays statuses (tweets).
 *
 * @author Aidan Follestad (afollestad)
 */
public class StatusAdapter extends SilkAdapter<Status> implements View.OnClickListener {

    public StatusAdapter(Context context, boolean convertRetweets) {
        super(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mDisplayRealNames = prefs.getBoolean("display_realname", true);
        mImageLoader = BoidApp.get(context).getImageLoader();
        mConvertRetweets = convertRetweets;
    }

    public StatusAdapter(Context context) {
        this(context, true);
    }

    private final boolean mDisplayRealNames;
    private final SilkImageManager mImageLoader;
    private final boolean mConvertRetweets;

    @Override
    public int getLayout(int index, int type) {
        return R.layout.list_item_status;
    }

    @Override
    public View onViewCreated(int index, View recycled, Status item) {
        View retweetedBy = recycled.findViewById(R.id.retweetedBy);
        if (item.isRetweet() && mConvertRetweets) {
            retweetedBy.setVisibility(View.VISIBLE);
            String retweetedTxt = getContext().getString(R.string.retweeted_by).replace("{user}", item.getUser().getScreenName());
            SpannableString retweetedSpan = new SpannableString(retweetedTxt);
            retweetedSpan.setSpan(new BoidSpan(getContext(), null), retweetedTxt.indexOf("@"), retweetedTxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((TextView) recycled.findViewById(R.id.retweetedByText)).setText(retweetedSpan);
            item = item.getRetweetedStatus();
        } else {
            retweetedBy.setVisibility(View.GONE);
        }

        SilkImageView profilePic = (SilkImageView) recycled.findViewById(R.id.profilePic);
        profilePic.setTag(item.getUser());
        profilePic.setOnClickListener(this);
        if (getScrollState() == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            profilePic.setImageResource(R.drawable.ic_contact_picture);
        } else {
            profilePic.setImageURL(mImageLoader, item.getUser().getBiggerProfileImageURL());
        }

        ((TextView) recycled.findViewById(R.id.username)).setText(TweetUtils.getDisplayName(item.getUser(), mDisplayRealNames));
        TextUtils.linkifyText(getContext(), (TextView) recycled.findViewById(R.id.content), item, false, false);
        ((TextView) recycled.findViewById(R.id.timestamp)).setText(TimeUtils.toStringShort(item.getCreatedAt()));

        recycled.findViewById(R.id.favoritedIndicator).setVisibility(item.isFavorited() ? View.VISIBLE : View.INVISIBLE);

        SilkImageView media = (SilkImageView) recycled.findViewById(R.id.media);
        String mediaUrl = TweetUtils.getTweetMediaURL(item, false);
        if (mediaUrl != null) {
            media.setVisibility(View.VISIBLE);
            if (getScrollState() != AbsListView.OnScrollListener.SCROLL_STATE_FLING)
                media.setImageURL(mImageLoader, mediaUrl);
            else media.setImageBitmap(null);
        } else {
            media.setVisibility(View.GONE);
        }

        return recycled;
    }

    @Override
    public long getItemId(Status item) {
        return item.getId();
    }

    @Override
    public void onClick(View v) {
        getContext().startActivity(new Intent(getContext(), ProfileActivity.class)
                .putExtra("user", (User) v.getTag()));
    }
}
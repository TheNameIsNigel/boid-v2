package com.afollestad.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
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
        mDisplayInlineMedia = prefs.getBoolean("inline_media_toggle", true);
        mDisplayVia = prefs.getBoolean("inline_via_indicator", false);
        mImageLoader = BoidApp.get(context).getImageLoader();
        mConvertRetweets = convertRetweets;
    }

    public StatusAdapter(Context context) {
        this(context, true);
    }

    private final boolean mDisplayRealNames;
    private final boolean mDisplayInlineMedia;
    private final boolean mDisplayVia;
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
        View profilePicFrame = recycled.findViewById(R.id.profilePicFrame);
        profilePicFrame.setTag(item.getUser());
        profilePicFrame.setOnClickListener(this);
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
        if (mDisplayInlineMedia) {
            String mediaUrl = TweetUtils.getTweetMediaURL(item, false);
            if (mediaUrl != null) {
                media.setVisibility(View.VISIBLE);
                if (getScrollState() != AbsListView.OnScrollListener.SCROLL_STATE_FLING)
                    media.setImageURL(mImageLoader, mediaUrl);
                else media.setImageBitmap(null);
            } else {
                media.setVisibility(View.GONE);
            }
        } else {
            // Inline media is disabled
            media.setVisibility(View.GONE);
        }

        TextView via = (TextView) recycled.findViewById(R.id.via);
        if (mDisplayVia) {
            via.setVisibility(View.VISIBLE);
            via.setText("via " + Html.fromHtml(item.getSource()).toString());
        } else {
            via.setVisibility(View.GONE);
        }

        View inReplyToFrame = recycled.findViewById(R.id.inReplyToFrame);
        if (item.getInReplyToUserId() > 0) {
            inReplyToFrame.setVisibility(View.VISIBLE);
            TextView inReplyTo = (TextView) recycled.findViewById(R.id.inReplyTo);
            String inReplyToText = getContext().getString(R.string.in_reply_to_x).replace("{x}", item.getInReplyToScreenName());
            SpannableString inReplyToSpan = new SpannableString(inReplyToText);
            inReplyToSpan.setSpan(new BoidSpan(getContext(), null), inReplyToText.indexOf("@"), inReplyToText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            inReplyTo.setText(inReplyToSpan);
        } else {
            inReplyToFrame.setVisibility(View.GONE);
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
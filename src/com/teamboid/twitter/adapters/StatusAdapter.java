package com.teamboid.twitter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.utilities.TimeUtils;
import twitter4j.MediaEntity;
import twitter4j.Status;

/**
 * A list adapter that displays statuses (tweets).
 *
 * @author Aidan Follestad (afollestad)
 */
public class StatusAdapter extends BoidAdapter<Status> {

    public StatusAdapter(Context context) {
        super(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mDisplayRealNames = prefs.getBoolean("display_realname", true);
        mImageLoader = BoidApp.get(context).getImageLoader();
    }

    private boolean mDisplayRealNames;
    private ImageLoader mImageLoader;

    @Override
    public View fillView(int index, View view) {
        Status item = getItem(index);

        View retweetedBy = view.findViewById(R.id.retweetedBy);
        if (item.isRetweet()) {
            retweetedBy.setVisibility(View.VISIBLE);
            TextView retweetedByTxt = (TextView) view.findViewById(R.id.retweetedByText);
            retweetedByTxt.setText(getContext().getString(R.string.retweeted_by).replace("{user}", item.getUser().getScreenName()));
            item = item.getRetweetedStatus();
        } else {
            retweetedBy.setVisibility(View.GONE);
        }

        NetworkImageView profilePic = (NetworkImageView) view.findViewById(R.id.profilePic);
        profilePic.setErrorImageResId(R.drawable.ic_contact_picture);
        profilePic.setDefaultImageResId(R.drawable.ic_contact_picture);
        profilePic.setImageUrl(item.getUser().getProfileImageURL(), mImageLoader);

        ((TextView) view.findViewById(R.id.userName)).setText(getDisplayName(item.getUser(), mDisplayRealNames));
        ((TextView) view.findViewById(R.id.content)).setText(item.getText());
        ((TextView) view.findViewById(R.id.timestamp)).setText(TimeUtils.getFriendlyTime(item.getCreatedAt()));

        NetworkImageView media = (NetworkImageView) view.findViewById(R.id.media);
        MediaEntity[] mediaEnts = item.getMediaEntities();
        if (mediaEnts != null && mediaEnts.length > 0) {
            media.setVisibility(View.VISIBLE);
            media.setImageUrl(mediaEnts[0].getExpandedURL(), mImageLoader);
        } else {
            media.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public int getLayout(int pos) {
        return R.layout.list_item_status;
    }

    @Override
    public long getItemId(Status item) {
        return item.getId();
    }
}

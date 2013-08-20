package com.teamboid.twitter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.silk.views.image.SilkImageView;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.utilities.TimeUtils;
import com.teamboid.twitter.utilities.Utils;
import twitter4j.MediaEntity;
import twitter4j.Status;

import java.util.List;

/**
 * A list adapter that displays statuses (tweets).
 *
 * @author Aidan Follestad (afollestad)
 */
public class StatusAdapter extends SilkAdapter<Status> {

    public StatusAdapter(Context context) {
        super(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mDisplayRealNames = prefs.getBoolean("display_realname", true);
        mImageLoader = BoidApp.get(context).getImageLoader();
    }

    private boolean mDisplayRealNames;
    private SilkImageManager mImageLoader;

    @Override
    public void set(List<Status> toSet) {
        if (getCount() == 0) {
            super.set(toSet);
            return;
        }
        markChanged();
        int index = 0;
        for (Status item : toSet) {
            this.add(index, item);
            index++;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getLayout(int index, int type) {
        return R.layout.list_item_status;
    }

    @Override
    public View onViewCreated(int index, View recycled, Status item) {
        View retweetedBy = recycled.findViewById(R.id.retweetedBy);
        if (item.isRetweet()) {
            retweetedBy.setVisibility(View.VISIBLE);
            TextView retweetedByTxt = (TextView) recycled.findViewById(R.id.retweetedByText);
            retweetedByTxt.setText(getContext().getString(R.string.retweeted_by).replace("{user}", item.getUser().getScreenName()));
            item = item.getRetweetedStatus();
        } else {
            retweetedBy.setVisibility(View.GONE);
        }

        SilkImageView profilePic = (SilkImageView) recycled.findViewById(R.id.profilePic);
        profilePic.setImageURL(mImageLoader, item.getUser().getProfileImageURL());

        ((TextView) recycled.findViewById(R.id.userName)).setText(Utils.getDisplayName(item.getUser(), mDisplayRealNames));
        Utils.linkifyText((TextView) recycled.findViewById(R.id.content), item.getText());
        ((TextView) recycled.findViewById(R.id.timestamp)).setText(TimeUtils.getFriendlyTime(item.getCreatedAt()));

        SilkImageView media = (SilkImageView) recycled.findViewById(R.id.media);
        MediaEntity[] mediaEnts = item.getMediaEntities();
        if (mediaEnts != null && mediaEnts.length > 0) {
            media.setVisibility(View.VISIBLE);
            media.setImageURL(mImageLoader, mediaEnts[0].getExpandedURL());
        } else {
            media.setVisibility(View.GONE);
        }

        return recycled;
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getId();
    }
}

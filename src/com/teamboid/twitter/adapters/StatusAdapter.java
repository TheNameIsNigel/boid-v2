package com.teamboid.twitter.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.base.BoidAdapter;
import com.teamboid.twitter.utilities.TimeUtils;
import twitter4j.Status;

public class StatusAdapter extends BoidAdapter<Status> {

    public StatusAdapter(Context context) {
        super(context);
    }

    @Override
    public View fillView(int index, View view) {
        Status item = getItem(index);

//        TextView retweetedBy = (TextView)view.findViewById(R.id.retweetedBy);
//        if(item.isRetweet()) {
//            retweetedBy.setVisibility(View.VISIBLE);
//            retweetedBy.setText(getContext().getString(R.string.retweeted_by).replace("{user}", item.getUser().getScreenName()));
//            item = item.getRetweetedStatus();
//        } else {
//            retweetedBy.setVisibility(View.GONE);
//        }

        NetworkImageView profilePic = (NetworkImageView) view.findViewById(R.id.profilePic);
        profilePic.setErrorImageResId(R.drawable.ic_contact_picture);
        profilePic.setDefaultImageResId(R.drawable.ic_contact_picture);
        profilePic.setImageUrl(item.getUser().getProfileImageURL(), BoidApp.get(getContext()).getImageLoader());
        ((TextView) view.findViewById(R.id.userName)).setText(item.getUser().getName());
        ((TextView) view.findViewById(R.id.content)).setText(item.getText());
        ((TextView) view.findViewById(R.id.timestamp)).setText(TimeUtils.getFriendlyTime(item.getCreatedAt()));

        return view;
    }

    @Override
    public int getLayout() {
        return R.layout.list_item_status;
    }
}

package com.teamboid.twitter.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.base.BoidAdapter;
import com.teamboid.twitter.utilities.TimeUtils;
import twitter4j.DirectMessage;
import twitter4j.User;

/**
 * A list adapter that displays direct messages.
 *
 * @author Aidan Follestad (afollestad)
 */
public class MessageAdapter extends BoidAdapter<DirectMessage> {

    public MessageAdapter(Context context) {
        super(context);
        me = BoidApp.get(context).getProfile();
    }

    private User me;

    @Override
    public View fillView(int index, View view) {
        DirectMessage item = getItem(index);

        NetworkImageView profilePic = (NetworkImageView) view.findViewById(R.id.profilePic);
        profilePic.setErrorImageResId(R.drawable.ic_contact_picture);
        profilePic.setDefaultImageResId(R.drawable.ic_contact_picture);
        profilePic.setImageUrl(item.getSender().getProfileImageURL(), BoidApp.get(getContext()).getImageLoader());
        ((TextView) view.findViewById(R.id.content)).setText(item.getText());
        ((TextView) view.findViewById(R.id.timestamp)).setText(TimeUtils.getFriendlyTime(item.getCreatedAt()));

        return view;
    }

    @Override
    public int getLayout(int pos) {
        DirectMessage msg = getItem(pos);
        if (me.getId() == msg.getSenderId())
            return R.layout.list_item_message_sent;
        else
            return R.layout.list_item_message_recv;
    }

    @Override
    public long getItemId(DirectMessage item) {
        return item.getId();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        DirectMessage msg = getItem(position);
        return (me.getId() == msg.getSenderId()) ? 0 : 1;
    }
}

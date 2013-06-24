package com.teamboid.twitter.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.base.BoidAdapter;
import twitter4j.DirectMessage;

public class MessageAdapter extends BoidAdapter<DirectMessage> {

    public MessageAdapter(Context context) {
        super(context);
    }

    @Override
    public View fillView(int index, View view) {
        DirectMessage item = getItem(index);

        NetworkImageView profilePic = (NetworkImageView) view.findViewById(R.id.profilePic);
        profilePic.setErrorImageResId(R.drawable.ic_contact_picture);
        profilePic.setDefaultImageResId(R.drawable.ic_contact_picture);
        profilePic.setImageUrl(item.getSender().getProfileImageURL(), BoidApp.get(getContext()).getImageLoader());
        ((TextView) view.findViewById(R.id.userName)).setText(item.getSender().getName());
        ((TextView) view.findViewById(R.id.content)).setText(item.getText());

        return view;
    }

    @Override
    public int getLayout() {
        return R.layout.list_item_message;
    }
}

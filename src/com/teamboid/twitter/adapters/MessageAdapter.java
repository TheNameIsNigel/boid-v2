package com.teamboid.twitter.adapters;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.silk.views.image.SilkImageView;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import twitter4j.DirectMessage;
import twitter4j.User;

/**
 * A list adapter that displays direct messages.
 *
 * @author Aidan Follestad (afollestad)
 */
public class MessageAdapter extends SilkAdapter<DirectMessage> {

    public MessageAdapter(Context context) {
        super(context);
        me = BoidApp.get(context).getProfile();
        mImageLoader = BoidApp.get(context).getImageLoader();
    }

    private User me;
    private SilkImageManager mImageLoader;

    @Override
    public int getLayout(int index, int type) {
        DirectMessage msg = getItem(index);
        if (me.getId() == msg.getSenderId())
            return R.layout.list_item_message_sent;
        else return R.layout.list_item_message_recv;
    }

    @Override
    public View onViewCreated(int index, View view, DirectMessage item) {
        SilkImageView profilePic = (SilkImageView) view.findViewById(R.id.profilePic);
        if (getScrollState() == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            profilePic.setImageResource(R.drawable.ic_contact_picture);
        } else {
            profilePic.setFitView(false).setImageURL(mImageLoader, item.getSender().getProfileImageURL());
        }
        ((TextView) view.findViewById(R.id.content)).setText(item.getText());
        return view;
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

    @Override
    public long getItemId(int i) {
        return getItem(i).getId();
    }
}

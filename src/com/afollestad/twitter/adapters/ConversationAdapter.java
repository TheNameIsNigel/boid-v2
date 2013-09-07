package com.afollestad.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.silk.caching.SilkComparable;
import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.silk.utilities.TimeUtils;
import com.afollestad.silk.views.image.SilkImageView;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.ProfileActivity;
import com.afollestad.twitter.utilities.TweetUtils;
import com.afollestad.twitter.utilities.text.TextUtils;
import twitter4j.DirectMessage;
import twitter4j.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A list adapter that displays your direct messaging conversations.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ConversationAdapter extends SilkAdapter<ConversationAdapter.Conversation> implements View.OnClickListener {

    private final boolean mDisplayRealNames;
    private final SilkImageManager mImageLoader;

    public ConversationAdapter(Context context) {
        super(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mDisplayRealNames = prefs.getBoolean("display_realname", true);
        mImageLoader = BoidApp.get(context).getImageLoader();
    }

    /**
     * Represents a conversation, or a collection of messages sent and received from the same end user.
     */
    public static class Conversation implements SilkComparable<Conversation> {

        public Conversation(User endUser, DirectMessage msg) {
            this.endUser = endUser;
            this.messages = new ArrayList<DirectMessage>();
            this.messages.add(msg);
        }

        private final User endUser;
        private final List<DirectMessage> messages;

        public User getEndUser() {
            return endUser;
        }

        public boolean add(DirectMessage msg, User me) {
            boolean sent = (me.getId() == msg.getSenderId());
            if ((sent && endUser.getId() != msg.getRecipientId()) || (!sent && endUser.getId() != msg.getSenderId()))
                return false;
            this.messages.add(0, msg);
            return true;
        }

        public DirectMessage getRecentMessage() {
            return messages.get(messages.size() - 1);
        }

        public List<DirectMessage> getMessages() {
            return messages;
        }

        public void sort() {
            Collections.sort(this.messages, new MessageComparator());
        }

        @Override
        public Object getSilkId() {
            return getEndUser().getId();
        }

        @Override
        public boolean equalTo(Conversation other) {
            return getEndUser().getSilkId() == other.getEndUser().getSilkId();
        }
    }

    /**
     * Automatically sorts messages into conversations.
     */
    public static class ConversationOrganizer {

        public ConversationOrganizer(Context context) {
            this.items = new ArrayList<Conversation>();
            this.me = BoidApp.get(context).getProfile();
        }

        private final List<Conversation> items;
        private final User me;

        private void add(DirectMessage msg) {
            boolean found = false;
            for (int i = 0; i < items.size(); i++) {
                Conversation convo = items.get(i);
                if (convo.add(msg, me)) {
                    found = true;
                    items.set(i, convo);
                    break;
                }
            }
            if (!found) {
                boolean sent = (me.getId() == msg.getSenderId());
                Conversation toadd = new Conversation(sent ? msg.getRecipient() : msg.getSender(), msg);
                this.items.add(toadd);
            }
        }

        public void add(DirectMessage[] messages) {
            for (DirectMessage msg : messages)
                add(msg);
        }

        public void sortAll() {
            for (int i = 0; i < items.size(); i++)
                items.get(i).sort();
        }

        public List<Conversation> getConversations() {
            return items;
        }
    }

    public static class MessageComparator implements Comparator<DirectMessage> {
        @Override
        public int compare(DirectMessage one, DirectMessage two) {
            return one.getCreatedAt().compareTo(two.getCreatedAt());
        }
    }

    @Override
    public int getLayout(int index, int type) {
        return R.layout.list_item_status;
    }

    @Override
    public View onViewCreated(int index, View recycled, Conversation item) {
        DirectMessage message = item.getRecentMessage();
        SilkImageView profilePic = (SilkImageView) recycled.findViewById(R.id.profilePic);
        profilePic.setTag(item.getEndUser());
        profilePic.setOnClickListener(this);
        if (getScrollState() == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            profilePic.setImageResource(R.drawable.ic_contact_picture);
        } else {
            profilePic.setImageURL(mImageLoader, item.getEndUser().getProfileImageURL());
        }
        ((TextView) recycled.findViewById(R.id.username)).setText(TweetUtils.getDisplayName(item.getEndUser(), mDisplayRealNames));
        TextUtils.linkifyText(getContext(), (TextView) recycled.findViewById(R.id.content), message, false, false);
        ((TextView) recycled.findViewById(R.id.timestamp)).setText(TimeUtils.toStringShort(message.getCreatedAt()));
        return recycled;
    }

    @Override
    public long getItemId(Conversation item) {
        return item.getEndUser().getId();
    }

    @Override
    public void onClick(View v) {
        getContext().startActivity(new Intent(getContext(), ProfileActivity.class)
                .putExtra("user", (User) v.getTag()));
    }
}

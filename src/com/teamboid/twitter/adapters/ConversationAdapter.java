package com.teamboid.twitter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.utilities.TimeUtils;
import twitter4j.DirectMessage;
import twitter4j.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A list adapter that displays your direct messaging conversations.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ConversationAdapter extends BoidAdapter<ConversationAdapter.Conversation> {

    /**
     * Represents a conversation, or a collection of messages sent and received from the same end user.
     */
    public static class Conversation implements Serializable {

        public Conversation(User endUser, DirectMessage msg) {
            this.endUser = endUser;
            this.messages = new ArrayList<DirectMessage>();
            this.messages.add(msg);
        }

        private User endUser;
        private List<DirectMessage> messages;

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
    }

    /**
     * Automatically sorts messages into conversations.
     */
    public static class ConversationOrganizer {

        public ConversationOrganizer(Context context) {
            this.items = new ArrayList<Conversation>();
            this.me = BoidApp.get(context).getProfile();
        }

        private List<Conversation> items;
        private User me;

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

        public Conversation[] toArray() {
            return items.toArray(new Conversation[0]);
        }
    }

    public static class MessageComparator implements Comparator<DirectMessage> {
        @Override
        public int compare(DirectMessage one, DirectMessage two) {
            return one.getCreatedAt().compareTo(two.getCreatedAt());
        }
    }

    public ConversationAdapter(Context context) {
        super(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mDisplayRealNames = prefs.getBoolean("display_realname", true);
    }

    private boolean mDisplayRealNames;

    @Override
    public View fillView(int index, View view) {
        Conversation convo = getItem(index);
        DirectMessage message = convo.getRecentMessage();

        NetworkImageView profilePic = (NetworkImageView) view.findViewById(R.id.profilePic);
        profilePic.setErrorImageResId(R.drawable.ic_contact_picture);
        profilePic.setDefaultImageResId(R.drawable.ic_contact_picture);
        profilePic.setImageUrl(convo.getEndUser().getProfileImageURL(), BoidApp.get(getContext()).getImageLoader());
        ((TextView) view.findViewById(R.id.userName)).setText(getDisplayName(convo.getEndUser(), mDisplayRealNames));
        ((TextView) view.findViewById(R.id.content)).setText(message.getText());
        ((TextView) view.findViewById(R.id.timestamp)).setText(TimeUtils.getFriendlyTime(message.getCreatedAt()));

        return view;
    }

    @Override
    public int getLayout(int pos) {
        return R.layout.list_item_status;
    }

    @Override
    public long getItemId(Conversation item) {
        return item.getEndUser().getId();
    }
}

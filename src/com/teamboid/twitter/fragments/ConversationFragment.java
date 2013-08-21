package com.teamboid.twitter.fragments;

import android.content.Intent;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.ConversationAdapter;
import com.teamboid.twitter.fragments.base.BoidListFragment;
import com.teamboid.twitter.ui.ConversationActivity;
import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * A feed fragment that displays the current user's message conversations.
 */
public class ConversationFragment extends BoidListFragment<ConversationAdapter.Conversation> {

    public ConversationFragment() {
        super("conversations");
    }

    @Override
    public int getEmptyText() {
        return R.string.no_conversations;
    }

    @Override
    public SilkAdapter<ConversationAdapter.Conversation> initializeAdapter() {
        return new ConversationAdapter(getActivity());
    }

    @Override
    public void onItemTapped(int index, ConversationAdapter.Conversation convo, View view) {
        startActivity(new Intent(getActivity(), ConversationActivity.class).putExtra("conversation", convo));
    }

    @Override
    public boolean onItemLongTapped(int index, ConversationAdapter.Conversation convo, View view) {
        return false;
    }

    @Override
    public ConversationAdapter.Conversation[] refresh() throws TwitterException {
        Twitter cl = BoidApp.get(getActivity()).getClient();
        ConversationAdapter.ConversationOrganizer organizer = new ConversationAdapter.ConversationOrganizer(getActivity());
        ResponseList<DirectMessage> msges = cl.getDirectMessages();
        if (msges.size() > 0)
            organizer.add(msges.toArray(new DirectMessage[0]));
        msges = cl.getSentDirectMessages();
        if (msges.size() > 0)
            organizer.add(msges.toArray(new DirectMessage[0]));
        return organizer.toArray();
    }

    @Override
    public String getTitle() {
        return getString(R.string.messages);
    }
}

package com.afollestad.twitter.fragments.columns;

import android.content.Intent;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.ConversationAdapter;
import com.afollestad.twitter.columns.Column;
import com.afollestad.twitter.fragments.base.BoidListFragment;
import com.afollestad.twitter.ui.ConversationActivity;
import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;

import java.util.List;

/**
 * A feed fragment that displays the current user's message conversations.
 */
public class ConversationFragment extends BoidListFragment<ConversationAdapter.Conversation> {

    @Override
    public String getCacheName() {
        return new Column(ConversationAdapter.Conversation.class, Column.MESSAGES).toString();
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
    protected boolean doesCacheExpire() {
        return false;
    }

    @Override
    protected List<ConversationAdapter.Conversation> load(Twitter client, Paging paging) throws Exception {
        ConversationAdapter.ConversationOrganizer organizer = new ConversationAdapter.ConversationOrganizer(getActivity());
        ResponseList<DirectMessage> msges = client.getDirectMessages();
        if (msges.size() > 0)
            organizer.add(msges.toArray(new DirectMessage[msges.size()]));
        msges = client.getSentDirectMessages();
        if (msges.size() > 0)
            organizer.add(msges.toArray(new DirectMessage[msges.size()]));
        organizer.sortAll();
        return organizer.getConversations();
    }

    @Override
    protected boolean isPaginationEnabled() {
        return false;
    }

    @Override
    public String getTitle() {
        return getString(R.string.messages);
    }
}

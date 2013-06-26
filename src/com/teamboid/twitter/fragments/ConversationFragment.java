package com.teamboid.twitter.fragments;

import android.content.Intent;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.ConversationActivity;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.ConversationAdapter;
import com.teamboid.twitter.adapters.BoidAdapter;
import com.teamboid.twitter.fragments.base.FeedFragment;
import com.teamboid.twitter.utilities.Utils;
import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * A feed fragment that displays the current user's message conversations.
 */
public class ConversationFragment extends FeedFragment<ConversationAdapter.Conversation> {

    public ConversationFragment() {
        super(false, true);
    }

    private ConversationAdapter adapter;

    @Override
    public int getEmptyText() {
        return R.string.no_conversations;
    }

    @Override
    public BoidAdapter<ConversationAdapter.Conversation> getAdapter() {
        if (adapter == null)
            adapter = new ConversationAdapter(getActivity());
        return adapter;
    }

    @Override
    public void onItemClicked(int index) {
        ConversationAdapter.Conversation convo = adapter.getItem(index);
        startActivity(new Intent(getActivity(), ConversationActivity.class)
                .putExtra("conversation", Utils.serializeObject(convo)));
    }

    @Override
    public boolean onItemLongClicked(int index) {
        //TODO
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
    public ConversationAdapter.Conversation[] paginate() throws TwitterException {
        return null;
    }

    @Override
    public String getTitle() {
        return getString(R.string.messages);
    }
}

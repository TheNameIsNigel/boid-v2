package com.teamboid.twitter.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.ConversationAdapter;
import com.teamboid.twitter.adapters.MessageAdapter;
import com.teamboid.twitter.base.BoidAdapter;
import com.teamboid.twitter.base.FeedFragment;
import com.teamboid.twitter.utilities.Utils;
import twitter4j.DirectMessage;

/**
 * A feed fragment that displays direct messages from a conversation.
 */
public class MessagesFragment extends FeedFragment<DirectMessage> {

    private MessageAdapter adapter;
    private ConversationAdapter.Conversation mConvo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mConvo = (ConversationAdapter.Conversation) Utils.deserializeObject(getArguments().getString("conversation"));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setStackFromBottom(true);
        getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    @Override
    public int getEmptyText() {
        return R.string.no_messages;
    }

    @Override
    public BoidAdapter<DirectMessage> getAdapter() {
        if (adapter == null)
            adapter = new MessageAdapter(getActivity());
        return adapter;
    }

    @Override
    public void onItemClicked(int index) {
        //TODO
    }

    @Override
    public boolean onItemLongClicked(int index) {
        //TODO
        return false;
    }

    @Override
    public DirectMessage[] refresh() throws Exception {
        return mConvo.getMessages().toArray(new DirectMessage[0]);
    }

    @Override
    public String getTitle() {
        return mConvo.getEndUser().getName();
    }
}

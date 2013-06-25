package com.teamboid.twitter.fragments;

import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.MessageAdapter;
import com.teamboid.twitter.base.BoidAdapter;
import com.teamboid.twitter.base.FeedFragment;
import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;

public class MessagesFragment extends FeedFragment<DirectMessage> {

    private MessageAdapter adapter;

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
    public DirectMessage[] refresh() throws Exception {
        Twitter cl = BoidApp.get(getActivity()).getClient();
        ResponseList<DirectMessage> msges = cl.getDirectMessages();
        msges.addAll(cl.getSentDirectMessages());
        return msges.toArray(new DirectMessage[0]);
    }

    @Override
    public int getTitle() {
        return R.string.messages;
    }
}

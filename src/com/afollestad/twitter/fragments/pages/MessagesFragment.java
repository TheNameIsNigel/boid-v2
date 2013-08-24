package com.afollestad.twitter.fragments.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.ConversationAdapter;
import com.afollestad.twitter.adapters.MessageAdapter;
import twitter4j.DirectMessage;

/**
 * A feed fragment that displays direct messages from a conversation, and allows you to send messages.
 */
public class MessagesFragment extends Fragment {

    private ConversationAdapter.Conversation mConvo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mConvo = (ConversationAdapter.Conversation) getArguments().getSerializable("conversation");
        mConvo.sort();
        getActivity().setTitle(mConvo.getEndUser().getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.convo_viewer, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MessageAdapter adapter = new MessageAdapter(getActivity());
        adapter.add(mConvo.getMessages().toArray(new DirectMessage[mConvo.getMessages().size()]));
        ListView list = (ListView) view.findViewById(R.id.list);
        list.setEmptyView(view.findViewById(R.id.empty));
        list.setAdapter(adapter);
    }
}

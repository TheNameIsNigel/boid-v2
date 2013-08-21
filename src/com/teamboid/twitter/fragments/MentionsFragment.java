package com.teamboid.twitter.fragments;

import android.content.Intent;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.StatusAdapter;
import com.teamboid.twitter.fragments.base.BoidListFragment;
import com.teamboid.twitter.ui.ComposeActivity;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * A feed fragment that displays the current user's mentions.
 */
public class MentionsFragment extends BoidListFragment<Status> {

    public MentionsFragment() {
        super("mentions");
    }

    @Override
    public int getEmptyText() {
        return R.string.no_mentions;
    }

    @Override
    public SilkAdapter<Status> initializeAdapter() {
        return new StatusAdapter(getActivity());
    }

    @Override
    public void onItemTapped(int index, Status status, View view) {
        //TODO
    }

    @Override
    public boolean onItemLongTapped(int index, Status status, View view) {
        startActivity(new Intent(getActivity(), ComposeActivity.class).putExtra("reply_to", status));
        return true;
    }

    @Override
    public Status[] refresh() throws TwitterException {
        Paging paging = new Paging();
        paging.setCount(getPageLength());
        if (getAdapter().getCount() > 0) {
            // Get tweets newer than the most recent tweet in the adapter
            paging.setSinceId(getAdapter().getItemId(0));
        }
        ResponseList<Status> response = BoidApp.get(getActivity()).getClient().getMentionsTimeline(paging);
        return response.toArray(new Status[response.size()]);
    }

//    @Override
//    public Status[] paginate() throws TwitterException {
//        Paging paging = new Paging();
//        paging.setCount(PAGE_LENGTH);
//        BoidAdapter adapt = getAdapter();
//        if (adapt.getCount() > 0) {
//            // Get tweets older than the oldest tweet in the adapter
//            paging.setMaxId(adapt.getItemId(adapt.getCount() - 1) - 1);
//        }
//        return BoidApp.get(getActivity()).getClient().getMentionsTimeline(paging).toArray(new Status[0]);
//    }

    @Override
    public String getTitle() {
        return getString(R.string.mentions);
    }
}

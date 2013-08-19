package com.teamboid.twitter.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import com.teamboid.twitter.fragments.ConversationFragment;
import com.teamboid.twitter.fragments.MentionsFragment;
import com.teamboid.twitter.fragments.TimelineFragment;
import com.teamboid.twitter.fragments.TrendsFragment;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            default:
                return new TimelineFragment();
            case 1:
                return new MentionsFragment();
            case 2:
                return new ConversationFragment();
            case 3:
                return new TrendsFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}

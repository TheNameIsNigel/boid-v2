package com.afollestad.twitter.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import com.afollestad.twitter.fragments.*;

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
            case 4:
                return new SearchFragment("#Boid", true);
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}

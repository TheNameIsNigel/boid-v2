package com.afollestad.twitter.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import com.afollestad.twitter.R;
import com.afollestad.twitter.fragments.pages.SearchFragment;
import com.afollestad.twitter.fragments.pages.UserSearchFragment;

/**
 * @author Aidan Follestad (afollestad)
 */
public class SearchPagerAdapter extends FragmentStatePagerAdapter {

    public SearchPagerAdapter(Context context, Bundle args, FragmentManager fm) {
        super(fm);
        mContext = context;
        mArgs = args;
    }

    private final Context mContext;
    private final Bundle mArgs;

    @Override
    public Fragment getItem(int i) {
        Fragment frag;
        switch (i) {
            default:
                frag = new SearchFragment();
                break;
            case 1:
                frag = new UserSearchFragment();
                break;
        }
        frag.setArguments(mArgs);
        return frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            default:
                return mContext.getString(R.string.tweets);
            case 1:
                return mContext.getString(R.string.users);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
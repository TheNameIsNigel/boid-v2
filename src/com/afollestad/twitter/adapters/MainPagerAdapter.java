package com.afollestad.twitter.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import com.afollestad.twitter.fragments.pages.*;
import com.afollestad.twitter.utilities.Columns;

import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    public MainPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mCols = Columns.getAll(context);
    }

    private List<String> mCols;

    @Override
    public Fragment getItem(int i) {
        if (mCols.get(i).equals("timeline")) {
            return new TimelineFragment();
        } else if (mCols.get(i).equals("mentions")) {
            return new MentionsFragment();
        } else if (mCols.get(i).equals("messages")) {
            return new ConversationFragment();
        } else if (mCols.get(i).equals("trends")) {
            return new TrendsFragment();
        } else if (mCols.get(i).startsWith("[search]:")) {
            SavedSearchFragment frag = new SavedSearchFragment();
            Bundle args = new Bundle();
            args.putString("query", mCols.get(i).substring(9));
            frag.setArguments(args);
            return frag;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mCols.size();
    }
}

package com.afollestad.twitter.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import com.afollestad.twitter.columns.Column;
import com.afollestad.twitter.columns.Columns;
import com.afollestad.twitter.fragments.columns.*;

import java.util.List;

/**
 * The {@link com.afollestad.twitter.ui.MainActivity}'s column view pager adapter.
 *
 * @author Aidan Follestad (afollestad)
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    public MainPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mCols = Columns.getAll(context);
    }

    private final List<Column> mCols;

    @Override
    public Fragment getItem(int i) {
        switch (mCols.get(i).getId()) {
            case Column.TIMELINE:
                return new TimelineFragment();
            case Column.MENTIONS:
                return new MentionsFragment();
            case Column.MESSAGES:
                return new ConversationsFragment();
            case Column.TRENDS:
                return new TrendsFragment();
            case Column.SEARCH:
                SavedSearchFragment frag = new SavedSearchFragment();
                Bundle args = new Bundle();
                args.putString("query", mCols.get(i).getComponent());
                frag.setArguments(args);
                return frag;
            case Column.LIST:
                //TODO
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mCols.size();
    }
}

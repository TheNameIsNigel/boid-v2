package com.afollestad.twitter.adapters.emojis;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.twitter.R;
import com.afollestad.twitter.data.EmojiRecent;
import com.afollestad.twitter.views.EmojiKeyboardView;

import java.util.ArrayList;

public class EmojiPagerAdapter extends PagerAdapter {

    private String[] titles;
    private ViewPager pager;
    private ArrayList<View> pages;
    private int keyboardHeight;

    public EmojiPagerAdapter(Context context, ViewPager pager, ArrayList<EmojiRecent> recents, int keyboardHeight) {
        super();

        String[] titles = { context.getString(R.string.emoji_recent), context.getString(R.string.emoji_people),
                context.getString(R.string.emoji_things), context.getString(R.string.emoji_nature),
                context.getString(R.string.emoji_places), context.getString(R.string.emoji_symbols) };

        this.titles = titles;
        this.keyboardHeight = keyboardHeight;

        this.pager = pager;
        this.pages = new ArrayList<View>();

        pages.add(new EmojiKeyboardView(context, 0, recents).getView());
        pages.add(new EmojiKeyboardView(context, 1).getView());
        pages.add(new EmojiKeyboardView(context, 2).getView());
        pages.add(new EmojiKeyboardView(context, 3).getView());
        pages.add(new EmojiKeyboardView(context, 4).getView());
        pages.add(new EmojiKeyboardView(context, 5).getView());
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        pager.addView(pages.get(position), position, keyboardHeight);
        return pages.get(position);
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        pager.removeView(pages.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

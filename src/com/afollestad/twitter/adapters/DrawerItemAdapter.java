package com.afollestad.twitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.afollestad.twitter.R;
import twitter4j.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list adapter that displays items in the {@link com.afollestad.twitter.ui.MainActivity}'s navigation drawer.
 *
 * @author Aidan Follestad (afollestad)
 */
public class DrawerItemAdapter extends BaseAdapter {

    public DrawerItemAdapter(Context context, User profile) {
        this.context = context;
        items = new ArrayList<String>();
        if (profile != null)
            items.add("@" + profile.getScreenName());
        else items.add("Profile");
        Collections.addAll(items, context.getResources().getStringArray(R.array.main_drawer_items));
        items.add(context.getString(R.string.add));
    }

    private final Context context;
    private final List<String> items;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            if (getItemViewType(i) == 0)
                view = LayoutInflater.from(context).inflate(R.layout.list_item_drawer, null);
            else view = LayoutInflater.from(context).inflate(R.layout.list_item_drawer_add, null);
        }
        TextView content = (TextView) view.findViewById(R.id.title);
        content.setText(items.get(i));
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == (getCount() - 1)) return 1;
        return 0;
    }
}

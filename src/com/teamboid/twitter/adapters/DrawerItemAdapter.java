package com.teamboid.twitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.teamboid.twitter.R;

/**
 * A list adapter that displays items in the {@link com.teamboid.twitter.ui.MainActivity}'s navigation drawer.
 *
 * @author Aidan Follestad (afollestad)
 */
public class DrawerItemAdapter extends BaseAdapter {

    public DrawerItemAdapter(Context context) {
        this.context = context;
        items = context.getResources().getStringArray(R.array.main_drawer_items);
    }

    private Context context;
    private String[] items;

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.drawer_item, null);
        TextView content = (TextView) view.findViewById(R.id.title);
        content.setText(items[i]);
        return view;
    }
}

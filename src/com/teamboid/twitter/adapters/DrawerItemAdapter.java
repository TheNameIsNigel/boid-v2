package com.teamboid.twitter.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.teamboid.twitter.R;

/**
 * A list adapter that displays items in the {@link com.teamboid.twitter.MainActivity}'s navigation drawer.
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
        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageDrawable(getIcon(i));
        TextView content = (TextView) view.findViewById(R.id.content);
        content.setText(items[i]);
        return view;
    }

    private Drawable getIcon(int index) {
        int attribute = 0;
        switch (index) {
            default:
                attribute = R.attr.drawerHome;
                break;
            case 1:
                attribute = R.attr.drawerMentions;
                break;
            case 2:
                attribute = R.attr.drawerMessages;
                break;
            case 3:
                attribute = R.attr.drawerTrends;
                break;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attribute});
        return a.getDrawable(0);
    }
}

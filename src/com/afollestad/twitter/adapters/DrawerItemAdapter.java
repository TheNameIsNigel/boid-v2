package com.afollestad.twitter.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.R;
import com.afollestad.twitter.columns.Column;
import com.afollestad.twitter.columns.Columns;
import twitter4j.User;

/**
 * A list adapter that displays items in the {@link com.afollestad.twitter.ui.MainActivity}'s navigation drawer.
 *
 * @author Aidan Follestad (afollestad)
 */
public class DrawerItemAdapter extends SilkAdapter<Column> {

    public DrawerItemAdapter(Context context, User profile) {
        super(context);
        add(new Column(Column.PROFILE_BUTTON, "@" + profile.getScreenName()));
        add(Columns.getAll(context));
        add(new Column(Column.ADD_BUTTON, context.getString(R.string.add)));
    }

    @Override
    public int getLayout(int index, int type) {
        if (type == 1) return R.layout.list_item_drawer_add;
        return R.layout.list_item_drawer;
    }

    @Override
    public View onViewCreated(int index, View recycled, Column item) {
        TextView content = (TextView) recycled.findViewById(R.id.title);
        content.setText(item.getName(true));
        return recycled;
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

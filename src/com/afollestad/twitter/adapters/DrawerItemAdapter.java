package com.afollestad.twitter.adapters;

import android.content.Context;
import android.view.View;
import android.widget.Button;
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
        add(new Column(Button.class, Column.PROFILE_BUTTON, "@" + profile.getScreenName()));
        add(Columns.getAll(context));
    }

    @Override
    public int getLayout(int index, int type) {
        return R.layout.list_item_drawer;
    }

    @Override
    public View onViewCreated(int index, View recycled, Column item) {
        TextView content = (TextView) recycled.findViewById(R.id.title);
        content.setText(item.getName(true));
        return recycled;
    }
}

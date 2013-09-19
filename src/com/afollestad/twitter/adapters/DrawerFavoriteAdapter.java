package com.afollestad.twitter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import com.afollestad.silk.Silk;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.R;
import twitter4j.User;
import twitter4j.internal.json.UserJSONImpl;

import java.util.Map;

/**
 * @author Aidan Follestad (afollestad)
 */
public class DrawerFavoriteAdapter extends SilkAdapter<User> {

    public DrawerFavoriteAdapter(Context context) {
        super(context);
        SharedPreferences prefs = context.getSharedPreferences("favorite_users", Context.MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();
        for (String sn : map.keySet())
            add((User) Silk.deserializeObject((String) map.get(sn), UserJSONImpl.class));
    }

    @Override
    public int getLayout(int index, int type) {
        return R.layout.list_item_drawer;
    }

    @Override
    public View onViewCreated(int index, View recycled, User item) {
        TextView content = (TextView) recycled.findViewById(R.id.title);
        content.setText("@" + item.getScreenName());
        return recycled;
    }

    @Override
    public long getItemId(User item) {
        return item.getId();
    }
}

package com.teamboid.twitter.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.teamboid.twitter.R;
import twitter4j.Trend;

/**
 * A list adapter that displays trends.
 *
 * @author Aidan Follestad (afollestad)
 */
public class TrendAdapter extends BoidAdapter<Trend> {

    public TrendAdapter(Context context) {
        super(context);
    }

    @Override
    public View fillView(int index, View view) {
        Trend item = getItem(index);
        ((TextView) view).setText(item.getName());
        return view;
    }

    @Override
    public int getLayout(int pos) {
        return R.layout.list_item_trend;
    }

    @Override
    public long getItemId(Trend item) {
        return -1l;
    }
}

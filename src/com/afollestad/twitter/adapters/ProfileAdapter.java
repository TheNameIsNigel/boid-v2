package com.afollestad.twitter.adapters;

import android.content.Context;
import android.view.View;
import com.afollestad.twitter.R;
import twitter4j.Status;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ProfileAdapter extends StatusAdapter {

    public ProfileAdapter(Context context) {
        super(context, true);
    }

    @Override
    public View onViewCreated(int index, View recycled, Status item) {
        recycled = super.onViewCreated(index, recycled, item);
        int top;
        if (index == 0) {
            top = getContext().getResources().getDimensionPixelSize(R.dimen.status_item_padding_bigger);
        } else {
            top = getContext().getResources().getDimensionPixelSize(R.dimen.status_item_padding);
        }
        recycled.setPadding(recycled.getPaddingLeft(), top,
                recycled.getPaddingRight(), recycled.getPaddingBottom());
        return recycled;
    }
}

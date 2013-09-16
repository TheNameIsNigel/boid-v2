package com.afollestad.twitter.adapters.emojis;

import android.content.Context;
import android.widget.BaseAdapter;

abstract class BaseEmojiAdapter extends BaseAdapter {

    protected final Context context;

    public BaseEmojiAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}

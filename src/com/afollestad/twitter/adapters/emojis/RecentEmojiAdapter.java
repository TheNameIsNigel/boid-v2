package com.afollestad.twitter.adapters.emojis;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.afollestad.twitter.R;
import com.afollestad.twitter.data.EmojiRecent;
import com.afollestad.twitter.ui.ComposeActivity;

import java.util.ArrayList;

public class RecentEmojiAdapter extends BaseEmojiAdapter {

    private ArrayList<EmojiRecent> recents;

    public RecentEmojiAdapter(Context context, ArrayList<EmojiRecent> recents) {
        super(context);
        this.recents = recents;
    }

    @Override
    public int getCount() {
        try {
            return recents.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            int scale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
            imageView.setPadding(scale, (int)(scale*1.2), scale, (int)(scale * 1.2));
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(Integer.parseInt(recents.get(position).icon));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComposeActivity.insertEmoji(context, recents.get(position).text, Integer.parseInt(recents.get(position).icon));
            }
        });

        final RecentEmojiAdapter adapter = this;

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ComposeActivity.removeRecent(position);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        return imageView;
    }
}
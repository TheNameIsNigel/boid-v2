package com.afollestad.twitter.utilities.text;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.ProfileActivity;
import com.afollestad.twitter.ui.SearchActivity;
import com.afollestad.twitter.ui.theming.ThemedActivity;

/**
 * @author Aidan Follestad (afollestad)
 */
public class BoidSpan extends ClickableSpan {

    public BoidSpan(Context context, String value) {
        mContext = context;
        mValue = value;
        mThemeColor = ThemedActivity.getAccentColor(context);
        if (mThemeColor == -1 || mThemeColor == context.getResources().getColor(android.R.color.black) || mThemeColor == context.getResources().getColor(R.color.gray_ab))
            mThemeColor = context.getResources().getColor(android.R.color.holo_blue_dark);
    }

    private final Context mContext;
    private final String mValue;
    private int mThemeColor;

    @Override
    public void onClick(View widget) {
        if (mValue.startsWith("@")) {
            mContext.startActivity(new Intent(mContext, ProfileActivity.class).putExtra("screen_name", mValue.substring(1)));
        } else if (mValue.startsWith("#")) {
            mContext.startActivity(new Intent(mContext, SearchActivity.class).putExtra("query", mValue));
        } else if (mValue.startsWith("http") || mValue.startsWith("$")) {
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mValue));
            mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.open_with)));
        } else {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mValue, null));
            mContext.startActivity(intent);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setColor(mThemeColor);
    }
}
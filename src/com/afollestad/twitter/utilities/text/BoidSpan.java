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

/**
 * @author Aidan Follestad (afollestad)
 */
public class BoidSpan extends ClickableSpan {

    public BoidSpan(Context context, String value) {
        mContext = context;
        mValue = value;
    }

    private final Context mContext;
    private final String mValue;

    @Override
    public void onClick(View widget) {
        if (mValue.startsWith("@")) {
            mContext.startActivity(new Intent(mContext, ProfileActivity.class).putExtra("screen_name", mValue.substring(1)));
        } else if (mValue.startsWith("#")) {
            mContext.startActivity(new Intent(mContext, SearchActivity.class).putExtra("query", mValue));
        } else if (mValue.startsWith("http")) {
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
    }
}
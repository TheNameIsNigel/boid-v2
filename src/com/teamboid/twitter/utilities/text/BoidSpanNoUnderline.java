package com.teamboid.twitter.utilities.text;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;
import com.teamboid.twitter.R;
import com.teamboid.twitter.ui.SearchActivity;

/**
 * @author Aidan Follestad (afollestad)
 */
public class BoidSpanNoUnderline extends ClickableSpan {

    public BoidSpanNoUnderline(Context context, String value) {
        mContext = context;
        mValue = value;
    }

    private Context mContext;
    private String mValue;

    @Override
    public void onClick(View widget) {
        if (mValue.startsWith("@")) {
            Toast.makeText(mContext, mValue, Toast.LENGTH_LONG).show();
        } else if (mValue.startsWith("#")) {
            mContext.startActivity(new Intent(mContext, SearchActivity.class).putExtra("query", mValue));
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mValue));
            mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.open_with)));
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
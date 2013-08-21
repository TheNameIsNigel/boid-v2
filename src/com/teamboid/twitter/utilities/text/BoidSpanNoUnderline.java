package com.teamboid.twitter.utilities.text;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        Toast.makeText(mContext, mValue, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
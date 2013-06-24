package com.teamboid.twitter.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class RobotoThinEditText extends EditText {

    public RobotoThinEditText(Context context) {
        super(context);
        init();
    }

    public RobotoThinEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoThinEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Light.ttf");
        setTypeface(tf);
    }
}

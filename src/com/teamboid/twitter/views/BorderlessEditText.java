package com.teamboid.twitter.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import com.teamboid.twitter.R;

public class BorderlessEditText extends EditText {

    public BorderlessEditText(Context context) {
        super(context);
        init();
    }

    public BorderlessEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BorderlessEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Light.ttf");
        setTypeface(tf);
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        int padding = getResources().getDimensionPixelSize(R.dimen.borderless_padding);
        setPadding(padding, padding, padding, padding);
    }
}

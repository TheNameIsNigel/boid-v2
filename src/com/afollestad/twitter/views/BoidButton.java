package com.afollestad.twitter.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import com.afollestad.twitter.R;

/**
 * A customized holographic blue Button.
 *
 * @author Aidan Follestad (afollestad)
 */
public class BoidButton extends Button {

    public BoidButton(Context context) {
        super(context);
        init();
    }

    public BoidButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoidButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setTextColor(getResources().getColor(android.R.color.white));
        int padding = getResources().getDimensionPixelSize(R.dimen.borderless_padding);
        setPadding(padding, padding, padding, padding);
        TypedArray ta = getContext().obtainStyledAttributes(new int[]{R.attr.boid_button});
        setBackground(ta.getDrawable(0));
        ta.recycle();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        // Force textAllCaps
        super.setText(text.toString().toUpperCase(), type);
    }
}

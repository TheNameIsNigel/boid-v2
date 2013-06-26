package com.teamboid.twitter.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.teamboid.twitter.R;

/**
 * A shadow view that supports left/right/up/down using the 'Shadow' styleable in styles.xml.
 *
 * @author Aidan Follestad (afollestad)
 */
public class Shadow extends View {

    public Shadow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Shadow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Shadow);
        int dir = a.getInt(R.styleable.Shadow_direction, 0);
        switch (dir) {
            default:
                setBackgroundResource(R.drawable.shadow_left);
                break;
            case 1:
                setBackgroundResource(R.drawable.shadow_right);
                break;
            case 2:
                setBackgroundResource(R.drawable.shadow_up);
                break;
            case 3:
                setBackgroundResource(R.drawable.shadow_down);
                break;
        }
        a.recycle();
    }
}

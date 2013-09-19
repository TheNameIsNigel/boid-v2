package com.afollestad.twitter.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.widget.Button;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.theming.ThemedActivity;

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

    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private StateListDrawable createBackground() {
        int color = ThemedActivity.getAccentColor(getContext());
        if (color == -1 || color == getResources().getColor(android.R.color.black) || color == getResources().getColor(R.color.gray_ab))
            color = getResources().getColor(android.R.color.holo_blue_dark);
        int colorAlpha = adjustAlpha(color, 0.7f);
        StateListDrawable mIcon = new StateListDrawable();
        mIcon.addState(StateSet.WILD_CARD, new ColorDrawable(color));
        mIcon.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(colorAlpha));
        return mIcon;
    }

    private void init() {
        setTextColor(getResources().getColor(android.R.color.white));
        int padding = getResources().getDimensionPixelSize(R.dimen.borderless_padding);
        setPadding(padding, padding, padding, padding);
        setBackground(createBackground());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        // Force textAllCaps
        super.setText(text.toString().toUpperCase(), type);
    }
}

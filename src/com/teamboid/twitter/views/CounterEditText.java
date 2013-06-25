package com.teamboid.twitter.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import com.teamboid.twitter.R;

public class CounterEditText extends RobotoThinEditText {

    public CounterEditText(Context context) {
        super(context);
        initialize();
    }

    public CounterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.contentTextColor});
        contentTextColor = a.getColor(0, 0);
    }

    private TextView counterView;

    private int contentTextColor;

    public final static int CHARACTER_LIMIT = 140;

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        updateCounter();
    }

    public void setCounterView(TextView view) {
        this.counterView = view;
    }

    private void updateCounter() {
        if (counterView == null)
            return;
        int textLength = getText().toString().trim().length();
        boolean overLimit = false;
        if (textLength > CHARACTER_LIMIT) {
            overLimit = true;
            textLength = 0 - (textLength - CHARACTER_LIMIT);
        }
        counterView.setTextColor(overLimit ? getResources().getColor(R.color.bright_red) : contentTextColor);
        counterView.setText(textLength + "");
    }
}

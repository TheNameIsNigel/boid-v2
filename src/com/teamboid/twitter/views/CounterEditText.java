package com.teamboid.twitter.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.teamboid.twitter.R;

public class CounterEditText extends RobotoThinEditText {

    public CounterEditText(Context context) {
        super(context);
    }

    public CounterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private TextView counterView;

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
        counterView.setTextColor(overLimit ?
                getResources().getColor(R.color.bright_red) : getResources().getColor(R.color.white));
        counterView.setText(textLength + "");
    }
}

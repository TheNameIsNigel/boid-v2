package com.afollestad.twitter.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.silk.views.text.SilkEditText;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.utilities.text.TextUtils;

/**
 * An edit text connects to and displays its length on a {@link TextView} automatically. Shows a red negative value
 * if the character count goes over {@value #CHARACTER_LIMIT}.
 *
 * @author Aidan Follestad (afollestad)
 */
public class CounterEditText extends SilkEditText {

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
        a.recycle();
        a = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.errorTextColor});
        errorTextColor = a.getColor(0, 0);
        a.recycle();
    }

    private TextView counterView;

    private int errorTextColor;
    private int contentTextColor;
    private boolean hasMedia;
    private boolean mShownShortenedToast;

    private final static int CHARACTER_LIMIT = 140;

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        updateCounter();
    }

    public void setCounterView(TextView view) {
        this.counterView = view;
    }

    public void setHasMedia(boolean hasMedia) {
        this.hasMedia = hasMedia;
        updateCounter();
    }

    private void updateCounter() {
        if (counterView == null) return;
        int textLength = getText().toString().trim().length();

        BoidApp.Config config = BoidApp.get(getContext()).getConfig();
        if (hasMedia) {
            if (textLength > 0) {
                // Add 1 character for the space before the picture URL
                textLength += 1;
            }
            textLength += config.getCharsPerMedia();
        }
        int shortDiff = TextUtils.getShortenedUrlDifference(getText().toString(), config);
        if (shortDiff > 0) {
            if (!mShownShortenedToast) {
                Toast.makeText(getContext(), R.string.urls_shortened, Toast.LENGTH_SHORT).show();
                mShownShortenedToast = true;
            }
            textLength -= shortDiff;
        }

        boolean overLimit = false;
        if (textLength > CHARACTER_LIMIT) {
            overLimit = true;
            textLength = 0 - (textLength - CHARACTER_LIMIT);
        }

        counterView.setTextColor(overLimit ? errorTextColor : contentTextColor);
        counterView.setText(textLength + "");
    }
}

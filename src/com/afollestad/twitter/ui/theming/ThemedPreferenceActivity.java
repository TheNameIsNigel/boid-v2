package com.afollestad.twitter.ui.theming;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Same as {@link ThemedActivity}, but used by the {@link com.afollestad.twitter.ui.SettingsActivity}.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ThemedPreferenceActivity extends PreferenceActivity {

    private int mTheme;
    private int mAbColor;
    private boolean mUseColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = ThemedActivity.getBoidTheme(this);
        setTheme(mTheme);
        mAbColor = ThemedActivity.getAccentColor(this);
        if (mAbColor != -1)
            getActionBar().setBackgroundDrawable(new ColorDrawable(mAbColor));
        mUseColor = ThemedActivity.shouldUseThemeColor(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ThemedActivity.shouldRecreate(this, mTheme, mAbColor, mUseColor))
            recreate();
    }
}
package com.afollestad.twitter.ui.theming;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Same as {@link ThemedActivity}, but used by the {@link com.afollestad.twitter.ui.SettingsActivity}.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ThemedPreferenceActivity extends PreferenceActivity {

    private int mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = ThemedActivity.getBoidTheme(this);
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ThemedActivity.shouldRecreate(this, mTheme))
            recreate();
    }
}
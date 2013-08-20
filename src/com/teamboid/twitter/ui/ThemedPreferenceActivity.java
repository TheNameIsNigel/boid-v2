package com.teamboid.twitter.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.teamboid.twitter.R;

/**
 * Same as {@link ThemedActivity}, but used by the {@link SettingsActivity}.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ThemedPreferenceActivity extends PreferenceActivity {

    private int mTheme;
    private boolean mDisplayRealNames;

    public final boolean shouldRecreate() {
        int currentTheme = getBoidTheme();
        boolean displayRealNames = shouldDisplayRealNames();
        return currentTheme != mTheme || displayRealNames != mDisplayRealNames;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = getBoidTheme();
        mDisplayRealNames = shouldDisplayRealNames();
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRecreate())
            recreate();
    }

    public final boolean shouldDisplayRealNames() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("display_realname", true);
    }

    public final int getBoidTheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = Integer.parseInt(prefs.getString("boid_theme", "0"));
        switch (theme) {
            default:
                return R.style.Theme_Boidlight;
            case 1:
                return R.style.Theme_Boid;
        }
    }
}
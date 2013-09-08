package com.afollestad.twitter.ui.theming;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.afollestad.twitter.R;

/**
 * Provides a standardized base for all activities that automatically theme themselves based on
 * the application's theme setting; this is basically every activity in the app.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ThemedActivity extends Activity {

    private int mTheme;
    private boolean mDisplayRealNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = getBoidTheme(this);
        mDisplayRealNames = shouldDisplayRealNames(this);
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRecreate(this, mTheme, mDisplayRealNames)) recreate();
    }

    public static boolean shouldRecreate(Context context, int mTheme, boolean mDisplayRealNames) {
        int currentTheme = getBoidTheme(context);
        boolean displayRealNames = shouldDisplayRealNames(context);
        return currentTheme != mTheme || displayRealNames != mDisplayRealNames;
    }

    public static boolean shouldDisplayRealNames(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("display_realname", true);
    }

    public static int getBoidTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int theme = Integer.parseInt(prefs.getString("boid_theme", "0"));
        switch (theme) {
            default:
                return R.style.Theme_Boid;
            case 1:
                return R.style.Theme_Boidlight;
            case 2:
                return R.style.Theme_Boidblack;
            case 3:
                return R.style.Theme_Boidgray;
        }
    }
}
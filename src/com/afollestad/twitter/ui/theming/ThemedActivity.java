package com.afollestad.twitter.ui.theming;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
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
    private int mAbColor;
    private boolean mUseColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = getBoidTheme(this);
        setTheme(mTheme);
        mAbColor = getAccentColor(this);
        if (mAbColor != -1)
            getActionBar().setBackgroundDrawable(new ColorDrawable(mAbColor));
        mUseColor = shouldUseThemeColor(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRecreate(this, mTheme, mAbColor, mUseColor)) recreate();
    }

    public static boolean shouldUseThemeColor(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("use_theme_color", false);
    }

    public static boolean shouldRecreate(Context context, int mTheme, int mAbColor, boolean mUseColor) {
        int currentTheme = getBoidTheme(context);
        int currentColor = getAccentColor(context);
        boolean useColor = shouldUseThemeColor(context);
        return currentTheme != mTheme || currentColor != mAbColor || useColor != mUseColor;
    }

    public static int getBoidTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int theme = Integer.parseInt(prefs.getString("boid_theme", "0"));
        switch (theme) {
            default:
                return R.style.Theme_Boidgray;
            case 1:
                return R.style.Theme_Boid;
            case 2:
                return R.style.Theme_Boidblack;
            case 3:
                return R.style.Theme_Boidlight;
            case 4:
                return R.style.Theme_Boidlight_DarkAB;
        }
    }

    public static int getAccentColor(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean("use_theme_color", false)) return -1;
        return prefs.getInt("theme_color", -1);
    }
}
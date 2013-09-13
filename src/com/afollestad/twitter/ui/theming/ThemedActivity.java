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
    private boolean mInlineMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = getBoidTheme(this);
        mDisplayRealNames = shouldDisplayRealNames(this);
        mInlineMedia = shouldDisplayInlineMedia(this);
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRecreate(this, mTheme, mDisplayRealNames, mInlineMedia)) recreate();
    }

    public static boolean shouldRecreate(Context context, int mTheme, boolean mDisplayRealNames, boolean mInlineMedia) {
        int currentTheme = getBoidTheme(context);
        boolean displayRealNames = shouldDisplayRealNames(context);
        boolean displayInlineMeida = shouldDisplayInlineMedia(context);
        return currentTheme != mTheme || displayRealNames != mDisplayRealNames || displayInlineMeida != mInlineMedia;
    }

    public static boolean shouldDisplayRealNames(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("display_realname", true);
    }

    public static boolean shouldDisplayInlineMedia(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("inline_media_toggle", true);
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
}
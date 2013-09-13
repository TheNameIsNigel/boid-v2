package com.afollestad.twitter.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.afollestad.twitter.R;

/**
 * The 'tweet viewer' preference category.
 *
 * @author Aidan Follestad (afollestad)
 */
public class FeedsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_feeds);
    }
}

package com.afollestad.twitter.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.afollestad.twitter.R;

/**
 * The 'composer' preference category.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ComposerFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_composer);
    }
}

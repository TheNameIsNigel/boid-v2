package com.teamboid.twitter.preferences;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.teamboid.twitter.R;

/**
 * The 'look and feel' preference category.
 *
 * @author Aidan Follestad (afollestad)
 */
public class LookAndFeelFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_look_and_feel);

        ListPreference theme = (ListPreference) findPreference("boid_theme");
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                getActivity().recreate();
                return true;
            }
        });
    }
}

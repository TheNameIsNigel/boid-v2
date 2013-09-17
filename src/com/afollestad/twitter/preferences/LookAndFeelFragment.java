package com.afollestad.twitter.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.afollestad.twitter.R;

/**
 * The 'look and feel' preference category.
 *
 * @author Aidan Follestad (afollestad)
 */
public class LookAndFeelFragment extends PreferenceFragment {

    private String getThemeName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int theme = Integer.parseInt(prefs.getString("boid_theme", "0"));
        if (theme == 0) return getString(R.string.base_theme_desc);
        return getResources().getStringArray(R.array.theme_names)[theme];
    }

    private String getThemeColorName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int index = Integer.parseInt(prefs.getString("theme_color", "0"));
        if (index == 0) return getString(R.string.theme_color_desc);
        return getResources().getStringArray(R.array.color_entries)[index];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_look_and_feel);

        ListPreference theme = (ListPreference) findPreference("boid_theme");
        theme.setSummary(getThemeName());
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(getThemeName());
                getActivity().recreate();
                return true;
            }
        });

        ListPreference color = (ListPreference) findPreference("theme_color");
        color.setSummary(getThemeColorName());
        color.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(getThemeColorName());
                getActivity().recreate();
                return true;
            }
        });
    }
}

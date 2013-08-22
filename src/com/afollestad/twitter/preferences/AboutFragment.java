package com.afollestad.twitter.preferences;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.afollestad.twitter.R;

/**
 * The 'about' preference category.
 *
 * @author Aidan Follestad (afollestad)
 */
public class AboutFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);

        Preference version = findPreference("app_version");
        Preference build = findPreference("app_build");

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setSummary(pInfo.versionName);
            build.setSummary("" + pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}

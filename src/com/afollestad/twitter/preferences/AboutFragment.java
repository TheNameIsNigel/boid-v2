package com.afollestad.twitter.preferences;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.ProfileActivity;

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

        findPreference("boidapp").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), ProfileActivity.class)
                        .putExtra("screen_name", "boidapp"));
                return true;
            }
        });
    }
}

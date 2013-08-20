package com.teamboid.twitter.ui;

import android.os.Bundle;
import android.view.MenuItem;
import com.teamboid.twitter.R;

import java.util.List;

/**
 * The settings UI, accessed via the {@link MainActivity} overflow menu.
 *
 * @author Aidan Follestad (afollestad)
 */
public class SettingsActivity extends ThemedPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // go to previous preference fragment
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
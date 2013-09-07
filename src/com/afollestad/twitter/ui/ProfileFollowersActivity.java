package com.afollestad.twitter.ui;

import android.os.Bundle;
import android.view.MenuItem;
import com.afollestad.twitter.R;
import com.afollestad.twitter.fragments.ui.ProfileFollowersViewer;
import com.afollestad.twitter.ui.theming.ThemedActivity;
import com.afollestad.twitter.ui.theming.ThemedPtrActivity;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ProfileFollowersActivity extends ThemedPtrActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle args = new Bundle();
        args.putAll(getIntent().getExtras());
        ProfileFollowersViewer frag = new ProfileFollowersViewer();
        frag.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, frag).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

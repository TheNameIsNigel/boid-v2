package com.afollestad.twitter.ui;

import android.os.Bundle;
import android.view.MenuItem;
import com.afollestad.twitter.R;
import com.afollestad.twitter.fragments.ui.TweetViewerFragment;

/**
 * The tweet viewer UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class TweetViewerActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        TweetViewerFragment frag = new TweetViewerFragment();
        frag.setArguments(getIntent().getExtras());
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
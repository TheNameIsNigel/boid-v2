package com.afollestad.twitter.ui;

import android.os.Bundle;
import android.view.MenuItem;
import com.afollestad.twitter.R;
import com.afollestad.twitter.fragments.columns.MessagingFragment;
import com.afollestad.twitter.ui.theming.ThemedActivity;

/**
 * The direct message conversation viewer UI, just displays a {@link com.afollestad.twitter.fragments.columns.MessagingFragment} on phones.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ConversationActivity extends ThemedActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle args = new Bundle();
        args.putAll(getIntent().getExtras());
        MessagingFragment frag = new MessagingFragment();
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

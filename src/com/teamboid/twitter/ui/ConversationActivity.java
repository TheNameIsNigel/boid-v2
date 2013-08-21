package com.teamboid.twitter.ui;

import android.os.Bundle;
import android.view.MenuItem;
import com.teamboid.twitter.R;
import com.teamboid.twitter.fragments.MessagesFragment;

/**
 * The direct message conversation viewer UI, just displays a {@link MessagesFragment} on phones.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ConversationActivity extends ThemedActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle args = new Bundle();
        args.putAll(getIntent().getExtras());
        MessagesFragment frag = new MessagesFragment();
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

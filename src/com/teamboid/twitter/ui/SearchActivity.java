package com.teamboid.twitter.ui;

import android.os.Bundle;
import android.view.MenuItem;
import com.teamboid.twitter.R;
import com.teamboid.twitter.fragments.SearchFragment;

/**
 * @author Aidan Follestad (afollestad)
 */
public class SearchActivity extends ThemedActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        SearchFragment frag = new SearchFragment(getIntent().getStringExtra("query"));
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

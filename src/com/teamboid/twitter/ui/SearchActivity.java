package com.teamboid.twitter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.teamboid.twitter.R;
import com.teamboid.twitter.fragments.SearchFragment;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * @author Aidan Follestad (afollestad)
 */
public class SearchActivity extends ThemedActivity {

    private PullToRefreshAttacher mPullToRefreshAttacher;

    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

        SearchFragment frag = new SearchFragment(getIntent().getStringExtra("query"));
        getFragmentManager().beginTransaction().replace(R.id.content_frame, frag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.compose:
                startActivity(new Intent(this, ComposeActivity.class)
                        .putExtra("content", getIntent().getStringExtra("query")));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

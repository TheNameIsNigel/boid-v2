package com.afollestad.twitter.ui;

import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import com.afollestad.twitter.R;
import com.afollestad.twitter.SearchSuggestionsProvider;
import com.afollestad.twitter.adapters.SearchPagerAdapter;
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
        setContentView(R.layout.activity_search);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(new SearchPagerAdapter(this, getIntent().getExtras(), getFragmentManager()));

        String query = getIntent().getStringExtra("query");
        if (query.startsWith("@"))
            mPager.setCurrentItem(1);
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
        suggestions.saveRecentQuery(query, null);
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

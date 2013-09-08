package com.afollestad.twitter.fragments.columns;

import android.content.Intent;
import android.view.View;
import com.afollestad.silk.adapters.SilkAdapter;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.TrendAdapter;
import com.afollestad.twitter.columns.Column;
import com.afollestad.twitter.fragments.base.BoidListFragment;
import com.afollestad.twitter.ui.SearchActivity;
import twitter4j.Paging;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;

import java.util.Arrays;
import java.util.List;

/**
 * A feed fragment that displays Twitter trends.
 */
public class TrendsFragment extends BoidListFragment<Trend> {

    @Override
    public String getCacheName() {
        return Column.TRENDS + "";
    }

    @Override
    public int getEmptyText() {
        return R.string.no_trends;
    }

    @Override
    public SilkAdapter<Trend> initializeAdapter() {
        return new TrendAdapter(getActivity());
    }

    @Override
    public void onItemTapped(int index, Trend item, View view) {
        startActivity(new Intent(getActivity(), SearchActivity.class)
                .putExtra("query", item.getName()));
    }

    @Override
    public boolean onItemLongTapped(int index, Trend item, View view) {
        return false;
    }

    @Override
    protected List<Trend> load(Twitter client, Paging paging) throws Exception {
        Trends trends = BoidApp.get(getActivity()).getClient().getPlaceTrends(1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getAdapter().clear();
            }
        });
        return Arrays.asList(trends.getTrends());
    }

    @Override
    protected boolean isPaginationEnabled() {
        return false;
    }

    @Override
    public String getTitle() {
        return getString(R.string.trends);
    }
}

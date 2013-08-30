package com.afollestad.twitter.ui;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.DrawerItemAdapter;
import com.afollestad.twitter.adapters.MainPagerAdapter;
import com.afollestad.twitter.columns.Columns;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * The activity_main app UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends ThemedDrawerActivity {

    private ViewPager mPager;
    private ListView drawerList;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private int mLastChecked = 1;

    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }

    @Override
    public int getDrawerShadowRes() {
        return R.drawable.drawer_shadow;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    @Override
    public int getOpenedTextRes() {
        return R.string.app_name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

        mPager = (ViewPager) findViewById(R.id.pager);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        mPager.setOffscreenPageLimit(5);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                drawerList.setItemChecked(position + 1, true);
                invalidateOptionsMenu();
            }
        });
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDrawerItemClicked(position);
            }
        });

        if (BoidApp.get(this).hasAccount()) {
            // Restore the last viewed fragment
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int index = prefs.getInt("recent_fragment_main", 1);
            drawerList.setItemChecked(index + 1, true);
            onDrawerItemClicked(index);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateColumns();
    }

    private void invalidateColumns() {
        int page = mPager.getCurrentItem();
        mPager.setAdapter(new MainPagerAdapter(this, getFragmentManager()));
        drawerList.setAdapter(new DrawerItemAdapter(this, BoidApp.get(this).getProfile()));
        mPager.setCurrentItem(page);
    }

    private void onDrawerItemClicked(int index) {
        getDrawerLayout().closeDrawers();
        if (index == 0) {
            drawerList.setItemChecked(mLastChecked, true);
            startActivity(new Intent(this, ProfileActivity.class)
                    .putExtra("user", BoidApp.get(this).getProfile()));
            return;
        } else if (index == drawerList.getCount() - 1) {
            drawerList.setItemChecked(mLastChecked, true);
            Columns.showAddDialog(this, new Columns.ColumnAddListener() {
                @Override
                public void onAdded(int newIndex) {
                    invalidateColumns();
                    mPager.setCurrentItem(newIndex);
                }
            });
            return;
        }
        mLastChecked = index;
        mPager.setCurrentItem(index - 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt("recent_fragment_main", mPager.getCurrentItem() + 1).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        menu.findItem(R.id.compose_dm).setVisible(mPager.getCurrentItem() == 2);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.compose:
                startActivity(new Intent(this, ComposeActivity.class));
                return true;
            case R.id.compose_dm:
                //TODO
                Toast.makeText(getApplicationContext(), "TODO", Toast.LENGTH_LONG).show();
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.logout:
                confirmLogout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.confirm_logout)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        BoidApp.get(MainActivity.this).logout();
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}

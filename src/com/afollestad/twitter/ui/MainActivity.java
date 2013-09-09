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
import android.view.Gravity;
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
import com.afollestad.twitter.ui.theming.ThemedDrawerActivity;

/**
 * The main app UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends ThemedDrawerActivity {

    private ViewPager mPager;
    private ListView drawerList;
    private int mLastChecked = 1;
    private int mLastPageCount;

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
        return R.string.columns;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if (!BoidApp.get(this).hasAccount()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Check for new columns
        int oldAccount = mLastPageCount;
        invalidateColumns();
        int newCount = mLastPageCount;
        if (oldAccount > 0 && newCount != oldAccount) {
            // If columns have been added or removed, move to the newer page or last old page
            mPager.setCurrentItem(newCount - 1);
            prefs.edit().remove("recent_fragment_main").commit();
        } else {
            // Restore the last viewed fragment page
            int index = prefs.getInt("recent_fragment_main", -1);
            if (index > -1) {
                mPager.setCurrentItem(index);
                prefs.edit().remove("recent_fragment_main").commit();
            }
        }
    }

    private void invalidateColumns() {
        int page = mPager.getCurrentItem();
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this, getFragmentManager());
        mLastPageCount = pagerAdapter.getCount();
        mPager.setAdapter(pagerAdapter);
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
        }
        mLastChecked = index;
        mPager.setCurrentItem(index - 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt("recent_fragment_main", mPager.getCurrentItem()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean drawerOpen = getDrawerLayout().isDrawerOpen(Gravity.START);
        if (drawerOpen) {
            getMenuInflater().inflate(R.menu.activity_main_draweropen, menu);
        } else {
            getMenuInflater().inflate(R.menu.activity_main, menu);
            menu.findItem(R.id.compose_dm).setVisible(mPager.getCurrentItem() == 2);
            final MenuItem search = menu.findItem(R.id.search);
            search.setVisible(!drawerOpen);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
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
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_columns:
                //TODO
                Toast.makeText(getApplicationContext(), "TODO", Toast.LENGTH_LONG).show();
                return true;
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

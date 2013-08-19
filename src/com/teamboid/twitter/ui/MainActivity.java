package com.teamboid.twitter.ui;

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
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.DrawerItemAdapter;
import com.teamboid.twitter.adapters.MainPagerAdapter;

/**
 * The main app UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends ThemedDrawerActivity {

    private ViewPager mPager;
    private ListView drawerList;

    @Override
    public int getDrawerIndicatorRes() {
        return R.drawable.ic_drawer;
    }

    @Override
    public int getDrawerShadowRes() {
        return R.drawable.drawer_shadow;
    }

    @Override
    public int getLayout() {
        return R.layout.main;
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

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MainPagerAdapter(getFragmentManager()));
        mPager.setOffscreenPageLimit(5);

        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerList.setAdapter(new DrawerItemAdapter(this));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDrawerItemClicked(position);
            }
        });

        if (BoidApp.get(this).hasAccount()) {
            // Restore the last viewed fragment
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int index = prefs.getInt("recent_fragment_main", 0);
            onDrawerItemClicked(index);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void onDrawerItemClicked(int index) {
        getDrawerLayout().closeDrawers();
        if (index == 0) {
            //Profile viewer
            //TODO
            return;
        }
        drawerList.setItemChecked(index, true);
        int previous = mPager.getCurrentItem();
        mPager.setCurrentItem(index - 1);
        if ((index - 1) != previous) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putInt("recent_fragment_main", index).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.compose:
                startActivity(new Intent(this, ComposeActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

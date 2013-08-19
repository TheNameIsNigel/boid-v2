package com.teamboid.twitter.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.adapters.DrawerItemAdapter;
import com.teamboid.twitter.fragments.ConversationFragment;
import com.teamboid.twitter.fragments.MentionsFragment;
import com.teamboid.twitter.fragments.TimelineFragment;
import com.teamboid.twitter.fragments.TrendsFragment;

/**
 * The main app UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends ThemedDrawerActivity {

    private int mPreviousFragment = -1;

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
        if (BoidApp.get(this).hasAccount()) {
            // Restore the last viewed fragment
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int index = prefs.getInt("recent_fragment_main", 0);
            onDrawerItemClicked(index);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerList.setAdapter(new DrawerItemAdapter(this));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDrawerItemClicked(position);
            }
        });
    }

    private void onDrawerItemClicked(int index) {
        getDrawerLayout().closeDrawers();
        switch (index) {
            case 0:  // Profile
                //TODO
                break;
            case 1:  // Timeline
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new TimelineFragment()).commit();
                break;
            case 2:  // Mentions
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new MentionsFragment()).commit();
                break;
            case 3:  // Messages
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new ConversationFragment()).commit();
                break;
            case 4:  // Trends
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new TrendsFragment()).commit();
                break;
        }
        if (index != mPreviousFragment) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putInt("recent_fragment_main", index).commit();
            mPreviousFragment = index;
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

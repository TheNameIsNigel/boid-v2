package com.teamboid.twitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.teamboid.twitter.adapters.DrawerItemAdapter;
import com.teamboid.twitter.base.DrawerActivity;
import com.teamboid.twitter.fragments.ConversationFragment;
import com.teamboid.twitter.fragments.MentionsFragment;
import com.teamboid.twitter.fragments.TimelineFragment;
import com.teamboid.twitter.fragments.TrendsFragment;
import twitter4j.User;

/**
 * The main app UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends DrawerActivity {

    private int mPreviousFragment = -1;

    @Override
    public int getLayout() {
        return R.layout.main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BoidApp.get(this).hasAccount()) {
            // Restore the last viewed fragment
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int index = prefs.getInt("recent_fragment_main", 0);
            onDrawerItemClicked(index);
            // Setup views in the navigation drawer
            setupDrawer();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onDrawerItemClicked(int index) {
        switch (index) {
            default:  // Timeline
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new TimelineFragment()).commit();
                break;
            case 1:  // Mentions
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new MentionsFragment()).commit();
                break;
            case 2:  // Messages
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new ConversationFragment()).commit();
                break;
            case 3:  // Trends
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
    public BaseAdapter getDrawerListAdapter() {
        return new DrawerItemAdapter(this);
    }

    private void setupDrawer() {
        User profile = BoidApp.get(this).getProfile();
        NetworkImageView profilePic = (NetworkImageView) findViewById(R.id.accountPicture);
        profilePic.setErrorImageResId(R.drawable.ic_contact_picture);
        profilePic.setDefaultImageResId(R.drawable.ic_contact_picture);
        profilePic.setImageUrl(profile.getProfileImageURL(), BoidApp.get(this).getImageLoader());
        ((TextView) findViewById(R.id.accountName)).setText(profile.getName());
        ((TextView) findViewById(R.id.accountScreenname)).setText("@" + profile.getScreenName());

        findViewById(R.id.accountDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO open profile viewer
                toggleDrawer();
            }
        });
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

package com.teamboid.twitter;

import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.teamboid.twitter.base.DrawerActivity;
import com.teamboid.twitter.fragments.MentionsFragment;
import com.teamboid.twitter.fragments.MessagesFragment;
import com.teamboid.twitter.fragments.TimelineFragment;
import twitter4j.User;

public class MainActivity extends DrawerActivity {

    @Override
    public int getLayout() {
        return R.layout.main;
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
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new MessagesFragment()).commit();
                break;
        }
    }

    @Override
    public ArrayAdapter getDrawerListAdapter() {
        return new ArrayAdapter<String>(this, R.layout.drawer_item, getResources().getStringArray(R.array.main_drawer_items));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BoidApp.get(this).hasAccount()) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new TimelineFragment()).commit();
            setupDrawer();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
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
}

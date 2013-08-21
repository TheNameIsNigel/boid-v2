package com.teamboid.twitter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.afollestad.silk.views.image.SilkImageView;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.utilities.text.TextUtils;
import twitter4j.Status;
import twitter4j.User;

/**
 * The tweet composition UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class TweetViewerActivity extends ThemedActivity {

    private Status mTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_viewer);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        processIntent();
    }

    private void processIntent() {
        mTweet = (Status) getIntent().getSerializableExtra("tweet");
        if (mTweet.isRetweet())
            mTweet = mTweet.getRetweetedStatus();
        SilkImageView profilePic = (SilkImageView) findViewById(R.id.profilePic);
        profilePic.setFitView(false).setImageURL(BoidApp.get(this).getImageLoader(), mTweet.getUser().getProfileImageURL());
        ((TextView) findViewById(R.id.fullname)).setText(mTweet.getUser().getName());
        ((TextView) findViewById(R.id.username)).setText("@" + mTweet.getUser().getScreenName());
        TextView content = (TextView) findViewById(R.id.content);
        TextUtils.linkifyText(this, content, mTweet, true, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tweet_viewer, menu);
        User me = BoidApp.get(this).getProfile();
        menu.findItem(R.id.delete).setVisible(me.getId() == mTweet.getUser().getId());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.reply:
                startActivity(new Intent(this, ComposeActivity.class).putExtra("reply_to", mTweet));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

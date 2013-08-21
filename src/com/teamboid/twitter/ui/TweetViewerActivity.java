package com.teamboid.twitter.ui;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        ((TextView) findViewById(R.id.source)).setText("via " + Html.fromHtml(mTweet.getSource()).toString());
        TextView content = (TextView) findViewById(R.id.content);
        TextUtils.linkifyText(this, content, mTweet, true, true);

        SilkImageView media = (SilkImageView) findViewById(R.id.media);
        if (mTweet.getMediaEntities() != null && mTweet.getMediaEntities().length > 0) {
            media.setVisibility(View.VISIBLE);
            media.setImageURL(BoidApp.get(this).getImageLoader(), mTweet.getMediaEntities()[0].getMediaURL());
        } else media.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tweet_viewer, menu);
        User me = BoidApp.get(this).getProfile();
        menu.findItem(R.id.delete).setVisible(me.getId() == mTweet.getUser().getId());
        MenuItem favorite = menu.findItem(R.id.favorite);
        int favIcon;
        if (mTweet.isFavorited()) {
            favorite.setTitle(R.string.unfavorite);
            favIcon = R.attr.favoritedIcon;
        } else {
            favorite.setTitle(R.string.favorite);
            favIcon = R.attr.unfavoritedIcon;
        }
        TypedArray ta = obtainStyledAttributes(new int[]{favIcon});
        favIcon = ta.getResourceId(0, 0);
        ta.recycle();
        favorite.setIcon(favIcon);
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
            case R.id.retweet:
                return true;
            case R.id.favorite:
                return true;
            case R.id.share:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

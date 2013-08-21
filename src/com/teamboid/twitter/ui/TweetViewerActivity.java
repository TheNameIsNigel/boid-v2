package com.teamboid.twitter.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.afollestad.silk.views.image.SilkImageView;
import com.devspark.appmsg.AppMsg;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.R;
import com.teamboid.twitter.utilities.text.TextUtils;
import twitter4j.Status;
import twitter4j.Twitter;
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
                showRetweetDialog();
                return true;
            case R.id.favorite:
                toggleFavorite();
                return true;
            case R.id.share:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleFavorite() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Twitter cl = BoidApp.get(TweetViewerActivity.this).getClient();
                    if (mTweet.isFavorited()) {
                        cl.destroyFavorite(mTweet.getId());
                        mTweet.setIsFavorited(false);
                    } else {
                        cl.createFavorite(mTweet.getId());
                        mTweet.setIsFavorited(true);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invalidateOptionsMenu();
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppMsg.makeText(TweetViewerActivity.this, e.getMessage(), AppMsg.STYLE_ALERT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    private void showRetweetDialog() {
        new AlertDialog.Builder(this).setItems(R.array.retweet_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    default:
                        performRetweet();
                    case 1:
                        startActivity(new Intent(TweetViewerActivity.this, ComposeActivity.class)
                                .putExtra("content", "\"@" + mTweet.getUser().getScreenName() + ": " + mTweet.getText() + "\" "));
                        break;
                }
            }
        }).show();
    }

    private void performRetweet() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Twitter cl = BoidApp.get(TweetViewerActivity.this).getClient();
                    cl.retweetStatus(mTweet.getId());
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppMsg.makeText(TweetViewerActivity.this, e.getMessage(), AppMsg.STYLE_ALERT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }
}
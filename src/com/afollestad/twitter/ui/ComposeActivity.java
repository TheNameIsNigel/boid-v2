package com.afollestad.twitter.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.afollestad.silk.views.image.SilkImageView;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.services.ComposerService;
import com.afollestad.twitter.ui.theming.ThemedActivity;
import com.afollestad.twitter.utilities.TweetUtils;
import com.afollestad.twitter.utilities.text.TextUtils;
import com.afollestad.twitter.views.CounterEditText;
import twitter4j.Status;
import twitter4j.User;

/**
 * The tweet composition UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ComposeActivity extends ThemedActivity {

    private Status mReplyTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer);
        setupInput();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        processIntent();
    }

    private void processIntent() {
        EditText input = (EditText) findViewById(R.id.input);
        Intent i = getIntent();
        input.setText("");
        if (i.hasExtra("mention")) {
            User mention = (User) i.getSerializableExtra("mention");
            input.append("@" + mention.getScreenName() + " ");
        }
        if (i.hasExtra("content"))
            input.append(i.getStringExtra("content"));
        if (i.hasExtra("reply_to")) {
            mReplyTo = (Status) i.getSerializableExtra("reply_to");
            if (mReplyTo.isRetweet())
                mReplyTo = mReplyTo.getRetweetedStatus();
            input.append(TweetUtils.getReplyAll(BoidApp.get(this).getProfile(), mReplyTo));
            setTitle(R.string.reply);
        } else {
            setTitle(R.string.compose);
        }
        setupInReplyTo();
    }

    private void setupInReplyTo() {
        View frame = findViewById(R.id.inReplyToFrame);
        View label = findViewById(R.id.inReplyToLabel);
        if (mReplyTo != null) {
            frame.setVisibility(View.VISIBLE);
            label.setVisibility(View.VISIBLE);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            boolean mDisplayRealNames = prefs.getBoolean("display_realname", true);
            SilkImageView profilePic = (SilkImageView) frame.findViewById(R.id.replyProfilePic);
            profilePic.setImageURL(BoidApp.get(this).getImageLoader(), mReplyTo.getUser().getBiggerProfileImageURL());
            ((TextView) frame.findViewById(R.id.replyUsername)).setText(TweetUtils.getDisplayName(mReplyTo.getUser(), mDisplayRealNames));
            TextUtils.linkifyText(this, (TextView) frame.findViewById(R.id.replyContent), mReplyTo, false, false);
        } else {
            frame.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
        }
    }

    private void setupInput() {
        final CounterEditText input = (CounterEditText) findViewById(R.id.input);
        input.setCounterView((TextView) findViewById(R.id.counter));
    }

    private void send(MenuItem item) {
        final EditText input = (EditText) findViewById(R.id.input);
        item.setEnabled(false);
        input.setEnabled(false);
        startService(new Intent(this, ComposerService.class)
                .putExtra("content", input.getText().toString().trim())
                .putExtra("reply_to", mReplyTo));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_composer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.send:
                send(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

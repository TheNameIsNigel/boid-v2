package com.teamboid.twitter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import com.teamboid.twitter.R;
import com.teamboid.twitter.services.ComposerService;
import com.teamboid.twitter.utilities.Utils;
import com.teamboid.twitter.views.CounterEditText;
import twitter4j.Status;

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
        if (i.hasExtra("mention"))
            input.append("@" + i.getStringExtra("mention") + " ");
        if (i.hasExtra("content"))
            input.append(i.getStringExtra("content"));
        if (i.hasExtra("reply_to")) {
            mReplyTo = (Status) Utils.deserializeObject(i.getStringExtra("reply_to"));
            if (mReplyTo.isRetweet())
                mReplyTo = mReplyTo.getRetweetedStatus();
            input.append("@" + mReplyTo.getUser().getScreenName() + " ");
            setTitle(R.string.reply);
        } else {
            setTitle(R.string.compose);
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

package com.teamboid.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import com.teamboid.twitter.base.ThemedActivity;
import com.teamboid.twitter.services.ComposerService;
import com.teamboid.twitter.views.CounterEditText;

/**
 * The tweet composition UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ComposeActivity extends ThemedActivity {

    private long mReplyTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.composer);
        setupInput();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        processIntent();
    }

    private void processIntent() {
        EditText input = (EditText) findViewById(R.id.input);
        Intent i = getIntent();
        input.setText("");
        if (i.hasExtra("mention"))
            input.append("@" + i.getStringExtra("mention"));
        if (i.hasExtra("content"))
            input.append(i.getStringExtra("content"));
        if (i.hasExtra("reply_to"))
            mReplyTo = i.getLongExtra("reply_to", 0l);
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
        getMenuInflater().inflate(R.menu.composer, menu);
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

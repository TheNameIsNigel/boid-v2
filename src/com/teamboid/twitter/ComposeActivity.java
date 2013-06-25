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

public class ComposeActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.composer);
        setupInput();
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
                .putExtra("content", input.getText().toString().trim()));
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

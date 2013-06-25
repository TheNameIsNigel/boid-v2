package com.teamboid.twitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import com.teamboid.twitter.services.ComposerService;

public class ComposeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.composer);
        setupInput();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupInput() {
        EditText input = (EditText) findViewById(R.id.input);
        final TextView counter = (TextView) findViewById(R.id.counter);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int before, int after, int count) {
                counter.setText(count + "");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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
                item.setEnabled(false);
                startService(new Intent(this, ComposerService.class)
                        .putExtra("content", ((EditText) findViewById(R.id.input)).getText().toString()));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

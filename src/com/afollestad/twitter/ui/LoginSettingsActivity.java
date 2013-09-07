package com.afollestad.twitter.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.theming.ThemedActivity;

/**
 * The login settings UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class LoginSettingsActivity extends ThemedActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        setContentView(R.layout.activity_loginsettings);

        final EditText key = (EditText) findViewById(R.id.consumerKey);
        key.setText(BoidApp.get(this).getConsumerKey());
        key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BoidApp.get(LoginSettingsActivity.this).setConsumerKey(key.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final EditText secret = (EditText) findViewById(R.id.consumerSecret);
        secret.setText(BoidApp.get(this).getConsumerSecret());
        secret.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BoidApp.get(LoginSettingsActivity.this).setConsumerSecret(secret.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
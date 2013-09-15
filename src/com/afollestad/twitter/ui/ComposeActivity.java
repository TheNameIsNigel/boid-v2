package com.afollestad.twitter.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.silk.Silk;
import com.afollestad.silk.views.image.SilkImageView;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.services.ComposerService;
import com.afollestad.twitter.ui.theming.ThemedLocationActivity;
import com.afollestad.twitter.utilities.TweetUtils;
import com.afollestad.twitter.utilities.Utils;
import com.afollestad.twitter.utilities.text.TextUtils;
import com.afollestad.twitter.views.CounterEditText;
import twitter4j.Status;
import twitter4j.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The tweet composition UI.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ComposeActivity extends ThemedLocationActivity {

    private Status mReplyTo;
    private boolean mAttachLocation;
    private String mCurrentCapturePath;

    private final static int CAPTURE_RESULT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer);
        setupInput();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        processIntent();
    }

    @Override
    public void onLocationUpdate(Location location) {
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
        Bundle extras = new Bundle();
        extras.putString("content", input.getText().toString().trim());
        extras.putSerializable("reply_to", mReplyTo);
        if (mAttachLocation)
            extras.putParcelable("location", getCurrentLocation());
        if (mCurrentCapturePath != null)
            extras.putString("media", mCurrentCapturePath);
        startService(new Intent(this, ComposerService.class).putExtras(extras));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_composer, menu);
        menu.findItem(R.id.locate).setIcon(mAttachLocation ? R.drawable.ic_location_unattach : Utils.resolveThemeAttr(this, R.attr.attachLocation));
        MenuItem camera = menu.findItem(R.id.camera);
        camera.setVisible(Silk.isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE));
        camera.setIcon(mCurrentCapturePath != null ? R.drawable.ic_camera_unattach : Utils.resolveThemeAttr(this, R.attr.attachCamera));
        return super.onCreateOptionsMenu(menu);
    }

    private void capture() {
        if (mCurrentCapturePath != null) {
            mCurrentCapturePath = null;
            invalidateOptionsMenu();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "capture_" + timeStamp + "_";
        File image;
        try {
            image = File.createTempFile(imageFileName, ".jpg", getExternalCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentCapturePath = image.getAbsolutePath();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        startActivityForResult(takePictureIntent, CAPTURE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_RESULT) {
            if (resultCode == RESULT_CANCELED)
                mCurrentCapturePath = null;
            invalidateOptionsMenu();
        }
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
            case R.id.locate:
                mAttachLocation = !mAttachLocation;
                invalidateOptionsMenu();
                return true;
            case R.id.camera:
                capture();
                return true;
            case R.id.gallery:
                //TODO
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

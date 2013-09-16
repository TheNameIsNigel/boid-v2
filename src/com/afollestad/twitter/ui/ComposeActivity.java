package com.afollestad.twitter.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.afollestad.silk.views.image.SilkImageView;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import com.afollestad.twitter.adapters.emojis.EmojiPagerAdapter;
import com.afollestad.twitter.data.EmojiDataSource;
import com.afollestad.twitter.data.EmojiRecent;
import com.afollestad.twitter.services.ComposerService;
import com.afollestad.twitter.ui.theming.ThemedLocationActivity;
import com.afollestad.twitter.utilities.TweetUtils;
import com.afollestad.twitter.utilities.Utils;
import com.afollestad.twitter.utilities.text.EmojiConverter;
import com.afollestad.twitter.utilities.text.TextUtils;
import com.afollestad.twitter.views.CounterEditText;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import twitter4j.Status;
import twitter4j.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private String mCurrentGalleryPath;

    private boolean isEmojiShowing;
    private static EmojiDataSource dataSource;
    private static ArrayList<EmojiRecent> recents;
    private static CounterEditText input;
    private static EmojiPagerAdapter emojiAdapter;

    private final static int CAPTURE_RESULT = 100;
    private final static int GALLERY_RESULT = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAttachLocation = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("always_attach_location", false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer);
        setupInput();
        setUpEmojiKeyboard();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        processIntent();
    }

    private void processIntent() {
        EditText input = (EditText) findViewById(R.id.input);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Intent i = getIntent();
        input.setText("");
        if (i.hasExtra("mention")) {
            User mention = (User) i.getSerializableExtra("mention");
            input.append("@" + mention.getScreenName() + " ");
        }
        if (i.hasExtra("content")) {
            input.append(i.getStringExtra("content"));
        }
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

        // Sharing from external apps
        if (Intent.ACTION_SEND.equals(i.getAction()) && i.getType() != null) {
            if (!BoidApp.get(this).hasAccount()) {
                Toast.makeText(this, R.string.add_account_try_again, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            if ("text/plain".equals(i.getType())) {
                input.append(i.getStringExtra(Intent.EXTRA_TEXT));
            } else if (i.getType().startsWith("image/")) {
                loadGalleryImage((Uri) i.getParcelableExtra(Intent.EXTRA_STREAM));
                invalidateAttachment();
            }
        }
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

    private void setUpEmojiKeyboard() {
        isEmojiShowing = false;
        input = (CounterEditText) findViewById(R.id.input);
        dataSource = new EmojiDataSource(this);
        dataSource.open();
        recents = (ArrayList<EmojiRecent>) dataSource.getAllRecents();
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int keyboardHeight = (int) (size.y / 3.0);
        ViewPager vp = (ViewPager) findViewById(R.id.emojiKeyboardPager);
        vp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, keyboardHeight));
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.emojiTabs);
        tabs.setIndicatorColor(getResources().getColor(android.R.color.holo_blue_dark));
        emojiAdapter = new EmojiPagerAdapter(this, vp, recents, keyboardHeight);
        vp.setAdapter(emojiAdapter);
        tabs.setViewPager(vp);
        vp.setCurrentItem(1);
        ImageButton delete = (ImageButton) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeText();
            }
        });
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
        else if (mCurrentGalleryPath != null)
            extras.putString("media", mCurrentGalleryPath);
        startService(new Intent(this, ComposerService.class).putExtras(extras));
        finish();
    }

    private boolean invalidateTweetButton() {
        EditText input = (EditText) findViewById(R.id.input);
        return input.getText().toString().trim().length() <= 140 && (!input.getText().toString().trim().isEmpty() || mCurrentCapturePath != null || mCurrentGalleryPath != null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_composer, menu);
        // Location attachment action
        MenuItem locate = menu.findItem(R.id.locate);
        locate.setIcon(mAttachLocation ? R.drawable.ic_location_unattach : Utils.resolveThemeAttr(this, R.attr.attachLocation));
        locate.setTitle(mAttachLocation ? R.string.unattach_location : R.string.attach_location);
        // Media attachment action
        MenuItem media = menu.findItem(R.id.media);
        media.setIcon(mCurrentCapturePath != null || mCurrentGalleryPath != null ?
                R.drawable.ic_gallery_unattach : Utils.resolveThemeAttr(this, R.attr.attachMedia));
        media.setTitle(mCurrentCapturePath != null || mCurrentGalleryPath != null ?
                R.string.unattach_media : R.string.attach_media);
        // Other actions
        MenuItem emoji = menu.findItem(R.id.emoji);
        emoji.setIcon(isEmojiShowing ? R.drawable.ic_emoji_keyboard_showing : Utils.resolveThemeAttr(this, R.attr.emojiKeyboard));
        menu.findItem(R.id.send).setEnabled(invalidateTweetButton());
        return super.onCreateOptionsMenu(menu);
    }

    private File createTempImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "capture_" + timeStamp + "_";
        File image;
        try {
            image = File.createTempFile(imageFileName, ".jpg", getExternalCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return image;
    }

    private void capture() {
        File image = createTempImageFile();
        mCurrentCapturePath = image.getAbsolutePath();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        startActivityForResult(takePictureIntent, CAPTURE_RESULT);
    }

    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK).setType("image/*");
        startActivityForResult(intent, GALLERY_RESULT);
    }

    private void insertEmojiKeyboard() {
        if (isEmojiShowing) {
            isEmojiShowing = false;
            findViewById(R.id.emojiKeyboard).setVisibility(View.GONE);
            findViewById(R.id.input).requestFocus();
            findViewById(R.id.input).requestFocusFromTouch();
        } else {
            isEmojiShowing = true;
            View keyboard = findViewById(R.id.emojiKeyboard);
            keyboard.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(keyboard.getWindowToken(), 0);
            findViewById(R.id.input).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isEmojiShowing) {
                        insertEmojiKeyboard();
                    }
                }
            });
        }
        invalidateOptionsMenu();
    }

    private void loadGalleryImage(Uri contentUri) {
        if (contentUri.toString().startsWith("content://com.google.android.gallery3d.provider/picasa/")) {
            try {
                InputStream picasaInput = getContentResolver().openInputStream(contentUri);
                File image = createTempImageFile();
                Utils.copy(picasaInput, new FileOutputStream(image));
                mCurrentGalleryPath = image.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                mCurrentGalleryPath = null;
            }
            invalidateAttachment();
            return;
        }
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (!cursor.moveToFirst()) return;
        mCurrentGalleryPath = cursor.getString(0);
        invalidateAttachment();
    }

    public static void insertEmoji(Context context, String emoji, int icon) {
        input.setEnabled(false); // prevent modification temporarily
        int beforeSelectionStart = input.getSelectionStart();
        int beforeLength = input.getText().toString().length();
        CharSequence before = input.getText().subSequence(0, beforeSelectionStart);
        CharSequence after = input.getText().subSequence(input.getSelectionEnd(), beforeLength);
        input.setText(android.text.TextUtils.concat(before, EmojiConverter.getSmiledText(context, emoji), after));
        input.setEnabled(true);
        input.setSelection(beforeSelectionStart + (input.getText().toString().length() - beforeLength));
        for (EmojiRecent recent1 : recents) {
            if (recent1.text.equals(emoji)) {
                dataSource.updateRecent(icon + "");
                recent1.count++;
                return;
            }
        }
        EmojiRecent recent = dataSource.createRecent(emoji, icon + "");
        if (recent != null) recents.add(recent);
    }

    private void removeText() {
        String currentText = input.getText().toString();
        if (currentText.length() > 0 && input.getSelectionStart() > 0) {
            //TODO are all emojis 2 characters long?
            input.setEnabled(false);
            input.setText(EmojiConverter.getSmiledText(this,
                    new StringBuilder(input.getText().toString()).deleteCharAt(input.getSelectionStart() - 2).toString()));
            input.setEnabled(true);
            input.setSelection(currentText.length() - 1);
        }
    }

    public static void removeRecent(int position) {
        dataSource.deleteRecent(recents.get(position).id);
        recents.remove(position);
        emojiAdapter.notifyDataSetChanged();
    }

    private void invalidateAttachment() {
        ((CounterEditText) findViewById(R.id.input)).setHasMedia(mCurrentCapturePath != null || mCurrentGalleryPath != null);
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_RESULT) {
            if (resultCode == RESULT_CANCELED)
                mCurrentCapturePath = null;
            invalidateAttachment();
        } else if (requestCode == GALLERY_RESULT) {
            if (resultCode == RESULT_CANCELED)
                mCurrentGalleryPath = null;
            else loadGalleryImage(data.getData());
        }
    }

    private void attachMedia() {
        if (mCurrentCapturePath != null || mCurrentGalleryPath != null) {
            mCurrentCapturePath = null;
            mCurrentGalleryPath = null;
            invalidateAttachment();
            return;
        }
        new AlertDialog.Builder(this).setTitle(R.string.attach_media)
                .setItems(R.array.media_attach_types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            default:
                                capture();
                                break;
                            case 1:
                                selectGallery();
                                break;
                        }
                    }
                }).show();
    }

    private void attachLocation() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.contains("always_attach_location")) {
            new AlertDialog.Builder(this).setTitle(R.string.always_attach_location)
                    .setMessage(R.string.always_attach_location_prompt)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            prefs.edit().putBoolean("always_attach_location", true).commit();
                            mAttachLocation = !mAttachLocation;
                            invalidateOptionsMenu();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            prefs.edit().putBoolean("always_attach_location", false).commit();
                            mAttachLocation = !mAttachLocation;
                            invalidateOptionsMenu();
                        }
                    }).show();
            return;
        }
        mAttachLocation = !mAttachLocation;
        invalidateOptionsMenu();
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
                attachLocation();
                return true;
            case R.id.media:
                attachMedia();
                return true;
            case R.id.emoji:
                insertEmojiKeyboard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isEmojiShowing) {
            insertEmojiKeyboard();
        } else {
            super.onBackPressed();
        }
    }
}
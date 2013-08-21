package com.teamboid.twitter.services;

import android.app.IntentService;
import android.content.Intent;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.notifications.ComposerNotify;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Handles tweet composition, sends tweets in the background and displays notifications while sending or if the app
 * fails to send a tweet.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ComposerService extends IntentService {

    public ComposerService() {
        super("BoidComposerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        send(intent);
    }

    private void send(Intent intent) {
        final String tag = ComposerNotify.show(this, intent.getExtras());
        Twitter cl = BoidApp.get(this).getClient();
        StatusUpdate update = new StatusUpdate(intent.getStringExtra("content"));

        if (intent.hasExtra("reply_to")) {
            Status replyTo = (Status) intent.getSerializableExtra("reply_to");
            update.setInReplyToStatusId(replyTo.getId());
        }

        try {
            cl.updateStatus(update);
            ComposerNotify.destroy(this, tag);
        } catch (TwitterException e) {
            e.printStackTrace();
            ComposerNotify.showError(this, intent.getExtras(), tag);
        }
    }
}

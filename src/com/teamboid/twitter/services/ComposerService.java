package com.teamboid.twitter.services;

import android.app.IntentService;
import android.content.Intent;
import com.teamboid.twitter.BoidApp;
import com.teamboid.twitter.notifications.ComposerNotify;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class ComposerService extends IntentService {

    public ComposerService() {
        super("BoidComposerService");
    }

    public final static String SEND_ACTION = "com.teamboid.twitter.SEND_TWEET";

    @Override
    protected void onHandleIntent(Intent intent) {
        send(intent);
    }

    private void send(Intent intent) {
        final String tag = ComposerNotify.show(this, intent.getExtras());
        Twitter cl = BoidApp.get(this).getClient();
        StatusUpdate update = new StatusUpdate(intent.getStringExtra("content"));

        try {
            cl.updateStatus(update);
            ComposerNotify.destroy(this, tag);
        } catch (TwitterException e) {
            e.printStackTrace();
            ComposerNotify.showError(this, intent.getExtras(), tag);
        }
    }
}

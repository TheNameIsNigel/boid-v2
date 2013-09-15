package com.afollestad.twitter.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.notifications.ComposerNotify;
import twitter4j.*;

import java.io.File;
import java.util.List;

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
            if (replyTo != null) update.setInReplyToStatusId(replyTo.getId());
        }

        if (intent.hasExtra("location")) {
            Location location = intent.getParcelableExtra("location");
            GeoLocation geoLoc = new GeoLocation(location.getLatitude(), location.getLongitude());
            update.setLocation(geoLoc);

            try {
                List<Place> places = cl.reverseGeoCode(new GeoQuery(geoLoc).granularity("poi"));
                update.setPlaceId(places.get(0).getId());
            } catch (TwitterException e) {
                e.printStackTrace();
                ComposerNotify.showError(this, intent.getExtras(), tag);
                return;
            }
        }

        if (intent.hasExtra("media"))
            update.setMedia(new File(intent.getStringExtra("media")));

        try {
            cl.updateStatus(update);
            ComposerNotify.destroy(this, tag);
        } catch (TwitterException e) {
            e.printStackTrace();
            ComposerNotify.showError(this, intent.getExtras(), tag);
        }
    }
}

package com.afollestad.twitter.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.ComposeActivity;
import com.afollestad.twitter.utilities.Utils;
import twitter4j.TwitterException;

/**
 * Convenience methods for displaying 'sending' or 'failed to send' notifications for tweets. Used by the
 * {@link com.afollestad.twitter.services.ComposerService}.
 *
 * @author Aidan Follestad (afollestad)
 */
public class ComposerNotify {

    private final static int ID = 100;

    public static String show(Context context, Bundle args) {
        String tag = System.currentTimeMillis() + "";
        Notification.Builder builder = new Notification.Builder(context);
        builder.setOngoing(true).setContentTitle(context.getString(R.string.posting_tweet))
                .setContentText(args.getString("content")).setSmallIcon(R.drawable.ic_status)
                .setTicker(context.getString(R.string.posting_tweet));

        Notification noti = builder.getNotification();
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(tag, ID, noti);

        return tag;
    }

    public static void showError(Context context, Bundle args, TwitterException error, String tag) {
        Notification.Builder builder = new Notification.Builder(context);
        Intent onTap = new Intent(context, ComposeActivity.class).putExtras(args);
        builder.setAutoCancel(true)
                .setContentTitle(context.getString(R.string.send_error))
                .setContentText(Utils.processTwitterException(context, error))
                .setSmallIcon(R.drawable.ic_status)
                .setTicker(context.getString(R.string.send_error))
                .setContentIntent(PendingIntent.getActivity(context, ID, onTap, PendingIntent.FLAG_CANCEL_CURRENT));

        Notification noti = builder.getNotification();
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(tag, ID, noti);
    }

    public static void destroy(Context context, String tag) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(tag, ID);
    }
}

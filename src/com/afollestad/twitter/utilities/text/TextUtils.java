package com.afollestad.twitter.utilities.text;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.widget.TextView;
import com.afollestad.twitter.BoidApp;
import com.afollestad.twitter.R;
import twitter4j.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aidan Follestad (afollestad)
 */
public class TextUtils {

    public static String expandURLs(String tweet, boolean longer, URLEntity[] urls, MediaEntity[] media) {
        if (urls != null) {
            for (URLEntity url : urls) {
                String toReplace = url.getDisplayURL();
                if (longer) toReplace = url.getExpandedURL();
                tweet = tweet.replace(url.getURL(), toReplace);
            }
        }
        if (media != null) {
            for (MediaEntity pic : media) {
                String toReplace = pic.getDisplayURL();
                if (longer) toReplace = pic.getExpandedURL();
                tweet = tweet.replace(pic.getURL(), toReplace);
            }
        }
        return tweet;
    }

    public static int getShortenedUrlDifference(String text, BoidApp.Config config) {
        int totalDiff = 0;
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            Log.d("getShortenedUrlDifference", url);
            int sl = url.startsWith("https://") ? config.getShortUrlHttpsLength() : config.getShortUrlLength();
            if (sl == url.length()) {
                Log.d("getShortenedUrlDifference", "URL length is equal to that of the short URL length");
                continue;
            }
            Log.d("getShortenedUrlDifference", "Normal length: " + url.length());
            Log.d("getShortenedUrlDifference", "Short length: " + sl);
            totalDiff += (url.length() - sl);
        }
        if (totalDiff == 0) return 0;
        Log.d("getShortenedUrlDifference", "Total diff: " + totalDiff);
        return totalDiff;
    }

    public static void linkifyText(Context context, TextView textView, String tweet, boolean clickable, boolean expandUrls, URLEntity[] urls, MediaEntity[] media) {
        tweet = expandURLs(tweet, expandUrls, urls, media);
        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return match.group();
            }
        };

        if (!hasEmoji(tweet)) {
            textView.setText(tweet);
        } else {
            // TODO optimize inserting
            // might not be the most efficient way to do this, not sure how to make it better though... :/ does create a little lag when it hits an emoji
            textView.setText(EmojiConverter.getSmiledText(context, tweet, textView.getTextSize()));
            textView.setGravity(Gravity.CENTER_VERTICAL);
        }

        textView.setLinksClickable(clickable);

        Linkify.addLinks(context, textView, Pattern.compile("@([A-Za-z0-9_-]+)"), null, filter);
        Linkify.addLinks(context, textView, Pattern.compile("#([A-Za-z0-9_-]+)"), null, filter);
        Linkify.addLinks(context, textView, Pattern.compile("^\\$([A-Za-z0-9_-]+)"), null, filter);
        Linkify.addLinks(context, textView, Patterns.WEB_URL, null, filter);
        Linkify.addLinks(context, textView, Patterns.EMAIL_ADDRESS, null, filter);
    }

    public static void linkifyText(Context context, TextView textView, Status tweet, boolean clickable, boolean expandUrls) {
        linkifyText(context, textView, tweet.getText(), clickable, expandUrls, tweet.getURLEntities(), tweet.getMediaEntities());
    }

    public static void linkifyText(Context context, TextView textView, User user, boolean clickable, boolean expandUrls) {
        linkifyText(context, textView, user.getDescription(), clickable, expandUrls, user.getDescriptionURLEntities(), null);
    }

    public static void linkifyText(Context context, TextView textView, DirectMessage msg, boolean clickable, boolean expandUrls) {
        linkifyText(context, textView, msg.getText(), clickable, expandUrls, msg.getURLEntities(), msg.getMediaEntities());
    }

    private static Pattern pattern;
    public static boolean hasEmoji(String text) {
        if (pattern == null) {
            pattern = Pattern.compile("\u00a9|\u00ae|[\u203c-\u3299]|[\uD83C\uDC04-\uD83C\uDFf0]|[\uD83D\uDC00-\uD83D\uDEc5]");
        }

        return pattern.matcher(text).find();
    }
}

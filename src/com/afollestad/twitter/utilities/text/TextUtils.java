package com.afollestad.twitter.utilities.text;

import android.content.Context;
import android.util.Patterns;
import android.widget.TextView;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;

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

    public static void linkifyText(Context context, TextView textView, String tweet, boolean clickable, boolean expandUrls, URLEntity[] urls, MediaEntity[] media) {
        tweet = expandURLs(tweet, expandUrls, urls, media);
        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return match.group();
            }
        };

        textView.setText(tweet);
        textView.setLinksClickable(clickable);

        Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)");
        Linkify.addLinks(context, textView, mentionPattern, null, filter);

        Pattern hashtagPattern = Pattern.compile("#([A-Za-z0-9_-]+)");
        Linkify.addLinks(context, textView, hashtagPattern, null, filter);

        Pattern urlPattern = Patterns.WEB_URL;
        Linkify.addLinks(context, textView, urlPattern, null, filter);
    }

    public static void linkifyText(Context context, TextView textView, Status tweet, boolean clickable, boolean expandUrls) {
        linkifyText(context, textView, tweet.getText(), clickable, expandUrls, tweet.getURLEntities(), tweet.getMediaEntities());
    }
}

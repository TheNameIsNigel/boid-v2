package com.teamboid.twitter.utilities.text;

import android.util.Patterns;
import android.widget.TextView;
import twitter4j.Status;
import twitter4j.URLEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aidan Follestad (afollestad)
 */
public class TextUtils {

    public static void linkifyText(TextView textView, String tweet, boolean clickable, boolean expandUrls, URLEntity[] urls) {
        for (URLEntity url : urls) {
            String toReplace = url.getDisplayURL();
            if (expandUrls) toReplace = url.getExpandedURL();
            tweet = tweet.replace(url.getURL(), toReplace);
        }

        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return match.group();
            }
        };

        textView.setText(tweet);
        textView.setLinksClickable(clickable);

        Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)");
        String mentionScheme = "http://www.twitter.com/";
        Linkify.addLinks(textView, mentionPattern, mentionScheme, null, filter);

        Pattern hashtagPattern = Pattern.compile("#([A-Za-z0-9_-]+)");
        String hashtagScheme = "http://www.twitter.com/search/";
        Linkify.addLinks(textView, hashtagPattern, hashtagScheme, null, filter);

        Pattern urlPattern = Patterns.WEB_URL;
        Linkify.addLinks(textView, urlPattern, null, null, filter);
    }

    public static void linkifyText(TextView textView, Status tweet, boolean clickable, boolean expandUrls) {
        linkifyText(textView, tweet.getText(), clickable, expandUrls, tweet.getURLEntities());
    }
}

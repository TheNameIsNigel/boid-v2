package com.afollestad.twitter.utilities;

import twitter4j.*;

import java.util.Locale;

/**
 * @author Aidan Follestad (afollestad)
 */
public class TweetUtils {

    public static String getDisplayName(User user, boolean realName) {
        if (realName) {
            String toreturn = user.getName();
            if (!toreturn.trim().isEmpty())
                return toreturn;
        }
        return "@" + user.getScreenName();
    }

    public static String getReplyAll(User me, Status tweet) {
        String toReturn = "@" + tweet.getUser().getScreenName() + " ";
        UserMentionEntity[] mentions = tweet.getUserMentionEntities();
        if (mentions != null) {
            for (UserMentionEntity mention : mentions) {
                if (mention.getId() == tweet.getUser().getId()) continue;
                else if (mention.getId() == me.getId()) continue;
                toReturn += "@" + mention.getScreenName() + " ";
            }
        }
        return toReturn;
    }

    public static String getTweetMediaURL(Status tweet, boolean larger) {
        return getMediaURL(tweet.getMediaEntities(), tweet.getURLEntities(), larger);
    }

    public static String getMediaURL(MediaEntity[] media, URLEntity[] urls, boolean larger) {
        if (media != null && media.length > 0) {
            return media[0].getMediaURL();
        } else if (urls != null && urls.length > 0) {
            for (URLEntity url : urls) {
                String du = url.getDisplayURL().toLowerCase(Locale.getDefault());
                if (du.startsWith("instagram.com")) {
                    String mu = url.getExpandedURL();
                    if (!mu.endsWith("/")) mu += "/";
                    return mu + "media/?size=" + (larger ? "l" : "m");
                } else if (du.startsWith("twitpic.com")) {
                    String mu = url.getExpandedURL();
                    if (mu.endsWith("/")) mu = mu.substring(0, mu.length() - 1);
                    mu = mu.substring(mu.lastIndexOf('/') + 1);
                    return "http://twitpic.com/show/full/" + mu;
                } else if (du.startsWith("yfrog")) {
                    return "http://" + url.getDisplayURL() + (larger ? ":medium" : ":iphone");
                }
            }
        }
        return null;
    }
}
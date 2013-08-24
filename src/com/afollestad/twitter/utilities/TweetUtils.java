package com.afollestad.twitter.utilities;

import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

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
                else if(mention.getId() == me.getId()) continue;
                toReturn += "@" + mention.getScreenName() + " ";
            }
        }
        return toReturn;
    }

    public static String getTweetMediaURL(Status tweet, boolean larger) {
        if (tweet.getMediaEntities() != null && tweet.getMediaEntities().length > 0) {
            return tweet.getMediaEntities()[0].getMediaURL();
        } else if (tweet.getURLEntities() != null && tweet.getURLEntities().length > 0) {
            URLEntity[] urls = tweet.getURLEntities();
            for (URLEntity url : urls) {
                if (url.getDisplayURL().startsWith("instagram.com")) {
                    String mu = url.getExpandedURL();
                    if (!mu.endsWith("/")) mu += "/";
                    return mu + "media/?size=" + (larger ? "l" : "m");
                } else if (url.getDisplayURL().startsWith("twitpic.com")) {
                    String mu = url.getExpandedURL();
                    if (mu.endsWith("/")) mu = mu.substring(0, mu.length() - 1);
                    mu = mu.substring(mu.lastIndexOf('/') + 1);
                    return "http://twitpic.com/show/full/" + mu;
                } else if (url.getDisplayURL().startsWith("yfrog")) {
                    return "http://" + url.getDisplayURL() + (larger ? ":medium" : ":iphone");
                }
            }
        }
        return null;
    }
}
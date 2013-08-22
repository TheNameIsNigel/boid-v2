package com.afollestad.twitter.utilities;

import android.util.Base64;
import twitter4j.Status;
import twitter4j.User;
import twitter4j.UserMentionEntity;

import java.io.*;

/**
 * Various utility methods.
 *
 * @author Aidan Follestad (afollestad)
 */
public class Utils {

    public static Object deserializeObject(String input) {
        try {
            byte[] data = Base64.decode(input, Base64.DEFAULT);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String serializeObject(Serializable tweet) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(tweet);
            oos.close();
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDisplayName(User user, boolean realName) {
        if (realName) {
            String toreturn = user.getName();
            if (!toreturn.trim().isEmpty())
                return toreturn;
        }
        return "@" + user.getScreenName();
    }

    public static String getReplyAll(Status tweet) {
        String toReturn = "@" + tweet.getUser().getScreenName() + " ";
        UserMentionEntity[] mentions = tweet.getUserMentionEntities();
        if (mentions != null) {
            for (UserMentionEntity mention : mentions) {
                if (mention.getId() == tweet.getUser().getId()) continue;
                toReturn += "@" + mention.getScreenName() + " ";
            }
        }
        return toReturn;
    }
}

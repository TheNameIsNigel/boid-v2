package com.afollestad.twitter.utilities;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;
import com.afollestad.twitter.R;
import twitter4j.TwitterException;

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

    public static String processTwitterException(Context context, TwitterException te) {
        String msg;
        if (te.exceededRateLimitation())
            msg = context.getString(R.string.rate_limit_error);
        else if (te.isCausedByNetworkIssue())
            msg = context.getString(R.string.network_error);
        else if (te.isErrorMessageAvailable())
            msg = te.getErrorMessage();
        else msg = te.getMessage();
        return msg;
    }
}

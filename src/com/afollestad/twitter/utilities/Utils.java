package com.afollestad.twitter.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import com.afollestad.twitter.R;
import twitter4j.TwitterException;

/**
 * Various utility methods.
 *
 * @author Aidan Follestad (afollestad)
 */
public class Utils {

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

    public static int resolveThemeAttr(Context context, int id) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{id});
        int resolved = ta.getResourceId(0, 0);
        ta.recycle();
        return resolved;
    }
}

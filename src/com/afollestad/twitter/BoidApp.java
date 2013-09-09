package com.afollestad.twitter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.afollestad.silk.Silk;
import com.afollestad.silk.images.SilkImageManager;
import com.afollestad.twitter.columns.Columns;
import com.afollestad.twitter.utilities.Utils;
import com.devspark.appmsg.AppMsg;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

/**
 * Variables and methods kept in memory throughout the life of the app.
 *
 * @author Aidan Follestad (afollestad)
 */
public class BoidApp extends Application {

    private Twitter client;
    private SilkImageManager mImageLoader;

    public final static String DEFAULT_CONSUMER_KEY = "5LvP1d0cOmkQleJlbKICtg";
    public final static String DEFAULT_CONSUMER_SECRET = "j44kDQMIDuZZEvvCHy046HSurt8avLuGeip2QnOpHKI";
    public final static String CALLBACK_URL = "boid://auth";

    public static String showAppMsgError(Activity activity, Exception e) {
        String msg = e.getMessage();
        if (e instanceof TwitterException)
            msg = Utils.processTwitterException(activity, (TwitterException) e);
        AppMsg.makeText(activity, msg, new AppMsg.Style(AppMsg.LENGTH_LONG, R.color.app_msg_red), R.layout.app_msg_themed).show();
        return msg;
    }

    public static void showAppMsg(Activity activity, String msg) {
        AppMsg.makeText(activity, msg, new AppMsg.Style(AppMsg.LENGTH_LONG, R.color.app_msg_blue)).show();
    }

    public SilkImageManager getImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new SilkImageManager(getApplicationContext())
                    .setFallbackImage(R.drawable.ic_contact_picture);
        }
        return mImageLoader;
    }

    public static BoidApp get(Context context) {
        return (BoidApp) context.getApplicationContext();
    }

    public void shutdownClient() {
        if (client == null) return;
        client.shutdown();
        client = null;
    }

    public Twitter getClient() {
        if (client == null) {
            client = new TwitterFactory().getInstance();
            client.setOAuthConsumer(getConsumerKey(), getConsumerSecret());
            AccessToken token = getToken();
            if (token != null)
                client.setOAuthAccessToken(token);
        }
        return client;
    }

    public void setConsumerKey(String key) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (key == null || key.trim().isEmpty())
            prefs.remove("consumer_key");
        else prefs.putString("consumer_key", key);
        prefs.commit();
    }

    public void setConsumerSecret(String secret) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (secret == null || secret.trim().isEmpty())
            prefs.remove("consumer_secret");
        else prefs.putString("consumer_secret", secret);
        prefs.commit();
    }

    public String getConsumerKey() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("consumer_key", DEFAULT_CONSUMER_KEY);
    }

    public String getConsumerSecret() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("consumer_secret", DEFAULT_CONSUMER_SECRET);
    }

    public BoidApp storeToken(AccessToken token) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("token", token.getToken()).putString("token_secret", token.getTokenSecret()).commit();
        return this;
    }

    public BoidApp storeProfile(User profile) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("profile", Silk.serializeObject(profile)).commit();
        return this;
    }

    public void clearAccount() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().remove("token").remove("token_secret").remove("profile").commit();
    }

    public User getProfile() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.contains("profile"))
            return null;
        return (User) Silk.deserializeObject(prefs.getString("profile", null));
    }

    public AccessToken getToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.contains("token"))
            return null;
        return new AccessToken(prefs.getString("token", null), prefs.getString("token_secret", null));
    }

    public boolean hasAccount() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.contains("token");
    }

    public void logout() {
        // Remove the stored authentication token and saved column positions
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().remove("token").commit();
        getSharedPreferences("[column-positions]", MODE_PRIVATE).edit().clear().commit();
        // Clear persisted preferences and stuff for Silk.
        Silk.clearPersistence(this);
        Columns.clear(this);
    }
}
package com.teamboid.twitter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.teamboid.twitter.images.ImageCacheManager;
import com.teamboid.twitter.utilities.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

/**
 * Variables and methods kept in memory throughout the life of the app.
 *
 * @author Aidan Follestad (afollestad)
 */
public class BoidApp extends Application {

    private ImageLoader mImageLoader;
    private Twitter client;

    private static Bitmap.CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;

    public final static String CONSUMER_KEY = "5LvP1d0cOmkQleJlbKICtg";
    public final static String CONSUMER_SECRET = "j44kDQMIDuZZEvvCHy046HSurt8avLuGeip2QnOpHKI";
    public final static String CALLBACK_URL = "boid://auth";

    @Override
    public void onCreate() {
        super.onCreate();
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
        int DISK_IMAGECACHE_QUALITY = 100;
        mImageLoader = new ImageLoader(mRequestQueue, new ImageCacheManager(this,
                getPackageCodePath(), DISK_IMAGECACHE_SIZE, DISK_IMAGECACHE_COMPRESS_FORMAT,
                DISK_IMAGECACHE_QUALITY, ImageCacheManager.CacheType.DISK));
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public static BoidApp get(Context context) {
        return (BoidApp) context.getApplicationContext();
    }

    public Twitter getClient() {
        if (client == null) {
            client = TwitterFactory.getSingleton();
            client.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            AccessToken token = getToken();
            if (token != null)
                client.setOAuthAccessToken(token);
        }
        return client;
    }

    public BoidApp storeToken(AccessToken token) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("token", token.getToken()).putString("token_secret", token.getTokenSecret()).commit();
        return this;
    }

    public BoidApp storeProfile(User profile) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("profile", Utils.serializeObject(profile)).commit();
        return this;
    }

    public void clearAccount() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().remove("token").remove("token_secret").remove("profile").commit();
        client = TwitterFactory.getSingleton();
        client.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    }

    public User getProfile() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.contains("profile"))
            return null;
        return (User) Utils.deserializeObject(prefs.getString("profile", null));
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
}
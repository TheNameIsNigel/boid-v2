package com.afollestad.twitter.readability;

import android.os.Handler;
import com.afollestad.silk.http.SilkHttpClient;
import com.afollestad.silk.http.SilkHttpResponse;

import java.net.URLEncoder;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Readability {

    public final static String TOKEN = "7855c885b4bfe7ada90401d87fd3298d8bfdc0b2";

    public static Response load(Handler handler, String url) throws Exception {
        SilkHttpResponse response = new SilkHttpClient(handler).get("https://readability.com/api/content/v1/parser?url=" +
                URLEncoder.encode(url, "UTF-8") + "&token=" + TOKEN);
        Response readable = new Response(response.getContentJSON());
        if (!readable.invalidate()) return null;
        return readable;
    }
}

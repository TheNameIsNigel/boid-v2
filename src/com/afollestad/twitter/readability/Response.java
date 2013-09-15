package com.afollestad.twitter.readability;

import android.text.Html;
import android.text.Spanned;
import org.json.JSONObject;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Response {

    public Response(JSONObject json) throws Exception {
        domain = json.getString("domain");
        url = json.getString("url");
        shortUrl = json.getString("short_url");
        author = json.getString("author");
        excerpt = Html.fromHtml(json.getString("excerpt")).toString();
        direction = json.getString("direction");
        wordCount = json.getInt("word_count");
        totalPages = json.getInt("total_pages");
        content = Html.fromHtml("content");
        leadImageUrl = json.getString("lead_image_url");
        title = json.getString("title");
        renderedPages = json.getInt("rendered_pages");
    }

    //    {
//        "domain": "www.androidheadlines.com",
//            "next_page_id": null,
//            "url": "http://www.androidheadlines.com/2013/09/motorola-nexus-5-look-like.html",
//            "short_url": "http://rdd.me/niq98nb2",
//            "author": "Alexander Maxham |",
//            "excerpt": "It&#x2019;s Nexus time. That means were basically only talking Nexus for the next few weeks. I&#x2019;m sure a few of you won&#x2019;t mind. The Nexus 5 is all but confirmed to be made by LG and&hellip;",
//            "direction": "ltr",
//            "word_count": 340,
//            "total_pages": 0,
//            "content": "<div><div class=\"entry\"><p><img class=\"aligncenter size-full wp-image-282815\" alt=\"motorola-nexus-concept-407x575\" src=\"http://www.androidheadlines.com/wp-content/uploads/2013/09/motorola-nexus-concept-407x575.png\" width=\"407\"></p><p>It&#x2019;s Nexus time. That means were basically only talking Nexus for the next few weeks. I&#x2019;m sure a few of you won&#x2019;t mind. The Nexus 5 is all but <a href=\"http://www.androidheadlines.com/2013/09/nexus-5-preview-will-nexus-5-look-like.html\">confirmed to be made by LG</a> and being announced next month by Google with the full details of Android 4.4 &#x2013; Kit Kat. However, that doesn&#x2019;t mean that we won&#x2019;t see another Nexus made by another competitor.</p><p>A well-sourced friend, Taylor Wimberly who used to run Android and Me, informed many of us that Motorola is working on a Nexus device and that it is not the Moto X. Naturally that got a lot of us excited. I know I&#x2019;d love to have a Motorola Nexus or a Nexus Maxx. This Nexus by Motorola is supposedly arriving in Q4 of this year, which would make sense since that&#x2019;s normally when Nexus devices come out.</p><p>Additionally, we heard from another well-sourced friend of ours, evleaks who is very seldom wrong, that the Nexus 5 is going to be made by Motorola. Which makes us really wonder if we&#x2019;ll be seeing multiple Nexus phones come next month.</p><p>On Thursday, a new concept for the Motorola Nexus 5 has popped up over at <a href=\"http://www.concept-phones.com/motorola/motorola-nexus-concept-features-edge-edge-display-beefy-battery/\"><em>Concept-Phones</em></a>. It doesn&#x2019;t show much. In fact it looks just like any other Motorola device. Basically,a black slab with a big bezel-less display, speaker grill and a front-facing camera that looks to be very big. Along with the Motorola logo. While it looks nice, it&#x2019;s also the specs and price that we need to find out before we can really draw any conclusions.</p><p>I&#x2019;d love to have a Motorola Nexus, especially if it has a lot of the features of the Moto X, as well as a huge battery. Throw in a 3000mAh battery or larger and I&#x2019;ll be happy. Motorola is known for their battery life, so that could very well happen. How many of you would love to see a Motorola Nexus 5 come October? Let us know in the comments below what you think of this render by Victor Monteiro.</p> <p class=\"cats\"><strong>Category</strong>: <a href=\"http://www.androidheadlines.com/category/news\" title=\"View all posts in Android News\" rel=\"category tag\">Android News</a></p></div></div>",
//            "date_published": null,
//            "dek": null,
//            "lead_image_url": "http://www.androidheadlines.com/wp-content/uploads/2013/09/motorola-nexus-concept-407x575.png",
//            "title": "Could a Motorola Nexus 5 Look Like This?",
//            "rendered_pages": 1
//    }

    private String domain;
    private String url;
    private String shortUrl;
    private String author;
    private String excerpt;
    private String direction;
    private int wordCount;
    private int totalPages;
    private Spanned content;
    private String leadImageUrl;
    private String title;
    private int renderedPages;

    public String getDomain() {
        return domain;
    }

    public String getUrl() {
        return url;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getDirection() {
        return direction;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public Spanned getContent() {
        return content;
    }

    public String getLeadImageUrl() {
        return leadImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getRenderedPages() {
        return renderedPages;
    }

    public boolean invalidate() {
        return getContent() != null && !getContent().toString().trim().isEmpty() && !getExcerpt().trim().isEmpty();
    }
}

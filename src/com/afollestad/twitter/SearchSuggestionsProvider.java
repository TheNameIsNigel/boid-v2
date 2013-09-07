package com.afollestad.twitter;

import android.content.SearchRecentSuggestionsProvider;

/**
 * @author Aidan Follestad (afollestad)
 */
public class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.afollestad.twitter.SearchSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}

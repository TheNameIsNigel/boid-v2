package com.afollestad.twitter.fragments.pages;

/**
 * A {@link SearchFragment} with the cache enabled.
 *
 * @author Aidan Follestad (afollestad)
 */
public class SavedSearchFragment extends SearchFragment {

    @Override
    protected boolean getCacheEnabled() {
        return true;
    }
}

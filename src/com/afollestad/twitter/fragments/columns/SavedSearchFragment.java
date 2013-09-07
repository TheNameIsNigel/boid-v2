package com.afollestad.twitter.fragments.columns;

import com.afollestad.twitter.fragments.SearchFragment;

/**
 * A {@link com.afollestad.twitter.fragments.SearchFragment} with the cache enabled.
 *
 * @author Aidan Follestad (afollestad)
 */
public class SavedSearchFragment extends SearchFragment {

    @Override
    protected boolean getCacheEnabled() {
        return true;
    }
}

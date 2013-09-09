package com.afollestad.twitter.fragments.columns;

import android.view.Menu;
import android.view.MenuInflater;
import com.afollestad.twitter.R;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.pin).setVisible(false);
    }
}

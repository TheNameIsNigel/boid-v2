package com.teamboid.twitter.fragments.base;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Provides a standardized base for list fragments that cache their contents to the disk to be loaded offline later.
 *
 * @param <T> The class contained in the fragment's list adapter, usually Status or DirectMessage.
 * @author Aidan Follestad (afollestad)
 */
public abstract class CacheableListFragment<T> extends Fragment {

    public CacheableListFragment(boolean enableCaching) {
        mCacheEnabled = enableCaching;
    }

    private boolean mCacheEnabled = true;

    /**
     * Gets the content that will be written to the cache.
     */
    public abstract T[] getCacheWriteables();

    /**
     * Called during Fragment initialization if the cache is empty.
     */
    public abstract void onCacheEmpty();

    /**
     * Called when the cache has been read and contents are ready to be written to an adapter.
     */
    public abstract void onCacheRead(T[] contents);

    /**
     * Gets the title of the fragment, used both in action bar titles and for the cache file name.
     */
    public abstract String getTitle();


    @Override
    public void onResume() {
        super.onResume();
        if (mCacheEnabled)
            readCache();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCacheEnabled)
            writeCache();
    }

    private void readCache() {
        final String title = getTitle().toLowerCase() + ".boid-cache";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream fileInputStream = getActivity().openFileInput(title);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    final T[] cache = (T[]) objectInputStream.readObject();
                    if (cache != null) {
                        Log.d("CacheableListFragment", "Read " + cache.length + " items from " + title);
                        onCacheRead(cache);
                        return;
                    }
                    objectInputStream.close();
                } catch (Exception e) {
                }
                Log.d("CacheableListFragment", title + " is empty or could not be read from...");
                onCacheEmpty();
            }
        }).start();
    }

    private void writeCache() {
        final T[] towrite = getCacheWriteables();
        final String title = getTitle().toLowerCase() + ".boid-cache";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream fileOutputStream = getActivity().openFileOutput(title, Context.MODE_PRIVATE);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(towrite);
                    Log.d("CacheableListFragment", "Wrote " + towrite.length + " items to " + title);
                    objectOutputStream.close();
                } catch (Exception e) {
                    Log.d("CacheableListFragment", "Failed to write to " + title + "...");
                }
            }
        }).start();
    }
}
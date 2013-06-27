package com.teamboid.twitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import twitter4j.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides a standardized base for most list adapters that are used in the app. Doesn't allow duplicate IDs.
 *
 * @param <T> The class contained in the adapter, usually Status or DirectMessage.
 * @author Aidan Follestad (afollestad)
 */
public abstract class BoidAdapter<T> extends BaseAdapter {

    public BoidAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<T>();
    }

    private Context context;
    protected List<T> items;

    public abstract View fillView(int index, View view);

    public abstract int getLayout(int pos);

    public abstract long getItemId(T item);

    public final Context getContext() {
        return context;
    }

    public final String getDisplayName(User user, boolean realName) {
        if (realName) {
            String toreturn = user.getName();
            if (!toreturn.trim().isEmpty())
                return toreturn;
        }
        return "@" + user.getScreenName();
    }

    /**
     * Adds an array of objects to the adapter. Returns number of items added.
     *
     * @param toadd the array of objects to add.
     * @param end   whether or not the items will be appended to the end of the list. False puts them at the beginning.
     */
    public final int add(T[] toadd, boolean end) {
        if (toadd == null)
            return 0;
        int count = 0;
        for (T item : toadd) {
            if (contains(item)) {
                if (!end) {
                    // While inserting into the beginning, assume we've reached the end of tweets that aren't already in the list
                    break;
                } else continue;
            }
            if (!end) {
                items.add(count, item);
            } else {
                items.add(item);
            }
            count++;
        }
        return count;
    }

    /**
     * Sets the contents of the adapter, and claers out any old items.
     *
     * @param items The items to fill the adapter with after clearing it.
     */

    public final void set(T[] items) {
        this.items.clear();
        for (T item : items)
            this.items.add(item);
        notifyDataSetChanged();
    }

    public final void clear() {
        this.items.clear();
    }

    /**
     * Checks whether or not the adapter contains an item of type T; searches based on IDs.
     */
    public final boolean contains(T item) {
        long itemId = getItemId(item);
        if (itemId == -1l)
            return false;
        for (int i = 0; i < this.items.size(); i++) {
            long curId = getItemId(this.items.get(i));
            if (curId == itemId)
                return true;
        }
        return false;
    }

    @Override
    public final int getCount() {
        return items.size();
    }

    @Override
    public final T getItem(int i) {
        return items.get(i);
    }

    @Override
    public final long getItemId(int i) {
        return getItemId(getItem(i));
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = LayoutInflater.from(context).inflate(getLayout(i), null);
        return fillView(i, view);
    }

    public T[] toArray(int limit) {
        T[] toreturn = (T[]) items.toArray();
        if (limit > 0 && toreturn.length > limit) {
            toreturn = Arrays.copyOfRange(toreturn, 0, limit);
        }
        return toreturn;
    }
}

package com.teamboid.twitter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a standardized base for most list adapters that are used in the app.
 *
 * @param <T> The class contained in the adapter, usually Status or DirectMessage.
 *
 * @author Aidan Follestad (afollestad)
 */
public abstract class BoidAdapter<T> extends BaseAdapter {

    public BoidAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<T>();
    }

    private Context context;
    private List<T> items;

    public abstract View fillView(int index, View view);

    public abstract int getLayout();

    public final Context getContext() {
        return context;
    }

    public final void add(T toadd) {
        if (toadd == null)
            return;
        items.add(toadd);
        notifyDataSetChanged();
    }

    public final void add(T[] toadd) {
        if (toadd == null)
            return;
        for (T item : toadd)
            items.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = LayoutInflater.from(context).inflate(getLayout(), null);
        return fillView(i, view);
    }
}

package com.afollestad.twitter.columns;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.afollestad.twitter.adapters.ConversationAdapter;
import twitter4j.Status;
import twitter4j.Trend;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for retrieval and modification of columns in the {@link com.afollestad.twitter.ui.MainActivity}.
 *
 * @author Aidan Follestad (afollestad)
 */
public class Columns {

    public interface ColumnAddListener {
        public void onAdded(int newIndex);
    }

    public static List<Column> getAll(Context context, Class<?> ofType) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String saved = prefs.getString("[columns]", null);
        if (saved == null) {
            List<Column> cols = new ArrayList<Column>();
            cols.add(new Column(Status.class, Column.TIMELINE));
            cols.add(new Column(Status.class, Column.MENTIONS));
            cols.add(new Column(ConversationAdapter.Conversation.class, Column.MESSAGES));
            cols.add(new Column(Trend.class, Column.TRENDS));
            setAll(context, cols);
            return cols;
        }
        String[] cols = saved.split(",");
        List<Column> toReturn = new ArrayList<Column>();
        for (String col : cols) {
            Column toAdd = new Column(col);
            if (ofType != null && !toAdd.getType().getName().equals(ofType.getName())) continue;
            toReturn.add(toAdd);
        }
        return toReturn;
    }

    public static List<Column> getAll(Context context) {
        return getAll(context, null);
    }

    public static void setAll(Context context, List<Column> cols) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String toSave = "";
        for (int i = 0; i < cols.size(); i++) {
            if (i > 0) toSave += ",";
            toSave += cols.get(i).toString();
        }
        prefs.edit().putString("[columns]", toSave).commit();
    }

    public static int add(Context context, Column col) {
        List<Column> cols = getAll(context);
        cols.add(col);
        setAll(context, cols);
        return cols.size() - 1;
    }

    public static void remove(Context context, int index) {
        List<Column> cols = getAll(context);
        cols.remove(index);
        setAll(context, cols);
    }

    public static void clear(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().remove("[columns]").commit();
    }
}
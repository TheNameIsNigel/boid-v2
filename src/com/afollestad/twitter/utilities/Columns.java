package com.afollestad.twitter.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Columns {

    public static List<String> getAll(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String saved = prefs.getString("[columns]", null);
        if (saved == null) {
            List<String> cols = new ArrayList<String>();
            Collections.addAll(cols, "timeline", "mentions", "messages", "trends");
            setAll(context, cols);
            return cols;
        }
        String[] cols = saved.split(",");
        List<String> toReturn = new ArrayList<String>();
        for (String col : cols) toReturn.add(col.replace("&#44;", ","));
        return toReturn;
    }

    public static void setAll(Context context, List<String> cols) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String toSave = "";
        for (int i = 0; i < cols.size(); i++) {
            if (i > 0) toSave += ",";
            toSave += cols.get(i).replace(",", "&#44;");
        }
        prefs.edit().putString("[columns]", toSave).commit();
    }

    public static void add(Context context, String col) {
        List<String> cols = getAll(context);
        cols.add(col);
        setAll(context, cols);
    }

    public static void remove(Context context, int index) {
        List<String> cols = getAll(context);
        cols.remove(index);
        setAll(context, cols);
    }
}
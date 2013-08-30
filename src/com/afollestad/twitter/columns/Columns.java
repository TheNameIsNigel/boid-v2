package com.afollestad.twitter.columns;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.afollestad.twitter.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Columns {

    public interface ColumnAddListener {
        public void onAdded(int newIndex);
    }

    public static List<Column> getAll(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String saved = prefs.getString("[columns]", null);
        if (saved == null) {
            List<Column> cols = new ArrayList<Column>();
            cols.add(new Column(Column.TIMELINE));
            cols.add(new Column(Column.MENTIONS));
            cols.add(new Column(Column.MESSAGES));
            cols.add(new Column(Column.TRENDS));
            setAll(context, cols);
            return cols;
        }
        String[] cols = saved.split(",");
        List<Column> toReturn = new ArrayList<Column>();
        for (String col : cols) {
            toReturn.add(new Column(col));
        }
        return toReturn;
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

    public static void showAddDialog(final Activity context, final ColumnAddListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.add_column)
                .setItems(R.array.column_types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TODO search and list dialogs?
                        int index = add(context, new Column(which));
                        if (listener != null) listener.onAdded(index);
                    }
                }).show();
    }
}
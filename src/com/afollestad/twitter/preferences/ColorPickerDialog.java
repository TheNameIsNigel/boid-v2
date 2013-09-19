package com.afollestad.twitter.preferences;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import com.afollestad.twitter.R;
import com.afollestad.twitter.ui.theming.ThemedDialog;
import com.larswerkman.colorpicker.ColorPicker;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ColorPickerDialog extends ThemedDialog implements com.larswerkman.colorpicker.ColorPicker.OnColorChangedListener {

    private ColorPicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_color_picker);

        picker = (ColorPicker) findViewById(R.id.picker);
        picker.setOnColorChangedListener(this);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(ColorPickerDialog.this).edit()
                        .putInt("theme_color", picker.getColor()).commit();
                setResult(RESULT_OK);
                finish();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int color = PreferenceManager.getDefaultSharedPreferences(ColorPickerDialog.this).getInt("theme_color", -1);
        if (color != -1) {
            picker.setColor(color);
            picker.setOldCenterColor(color);
            picker.setNewCenterColor(color);
        }
    }

    @Override
    public void onColorChanged(int color) {
        picker.setOldCenterColor(color);
    }

    public void onPresetClicked(View view) {
        int color = Color.parseColor((String) view.getTag());
        picker.setOldCenterColor(color);
        picker.setNewCenterColor(color);
    }
}
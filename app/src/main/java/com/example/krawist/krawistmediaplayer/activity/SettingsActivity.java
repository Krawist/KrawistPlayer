package com.example.krawist.krawistmediaplayer.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView colorinHex;
    TextView colorPreview;
    SharedPreferences sharedPreferences;

/*    TextView colorPreview = dialog.findViewById(R.id.textview_color_preview);

    final SeekBar redSeekBar = dialog.findViewById(R.id.seekbar_red_color);

    final SeekBar greenSeekBar = dialog.findViewById(R.id.seekbar_green_color);

    final SeekBar blueSeekBar = dialog.findViewById(R.id.seekbar_blue_color);

    EditText colorinHex = dialog.findViewById(R.id.edittext_color_hexadecimal);

    Button cancelButton = dialog.findViewById(R.id.button_color_dialog_cancel);

    Button validateButton = dialog.findViewById(R.id.button_color_dialog_validate);*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initialiseElements();

        configureToolbar();

        addAction();
    }


    private void initialiseElements(){
        toolbar = findViewById(R.id.toolbar);
        colorPreview = findViewById(R.id.textview_color_preview);
        colorinHex = findViewById(R.id.textview_selected_color_code);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    private void configureToolbar(){

        ((TextView )findViewById(R.id.toolbar_label)).setText(R.string.action_settings);

        findViewById(R.id.imagebutton_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.super.onBackPressed();
            }
        });

    }

    private void addAction(){

        LinearLayout layout = findViewById(R.id.layout_theme_color);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog colorChangingDialog = new Dialog(SettingsActivity.this);
                colorChangingDialog.setContentView(R.layout.layout_dialog_color_changing);
                colorChangingDialog.setCanceledOnTouchOutside(false);

                configureDialog(colorChangingDialog);

                colorChangingDialog.show();

            }
        });

        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals(getString(R.string.theme_color_preference_key))){
                    updateColor();
                }
            }
        });

    }

    private void updateColor(){
        String color = sharedPreferences.getString(getString(R.string.theme_color_preference_key),getString(R.string.theme_color_default_value));
        toolbar.setBackgroundColor(Color.parseColor(color));
        colorPreview.setBackgroundColor(Color.parseColor(color));
        colorinHex.setText(color);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    protected void onResume() {
        updateColor();
        super.onResume();
    }

    private void configureDialog(final Dialog dialog){
        final int MAX_VALUE = 255;
        final int MIN_VALUE = 0;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String currentColor = sharedPreferences.getString(getString(R.string.theme_color_preference_key),getString(R.string.theme_color_default_value));


        final TextView colorPreview = dialog.findViewById(R.id.textview_color_preview);

        final SeekBar redSeekBar = dialog.findViewById(R.id.seekbar_red_color);
        redSeekBar.setMax(MAX_VALUE);

        final SeekBar greenSeekBar = dialog.findViewById(R.id.seekbar_green_color);
        greenSeekBar.setMax(MAX_VALUE);

        final SeekBar blueSeekBar = dialog.findViewById(R.id.seekbar_blue_color);
        blueSeekBar.setMax(MAX_VALUE);

        final EditText colorinHex = dialog.findViewById(R.id.edittext_color_hexadecimal);

        Button cancelButton = dialog.findViewById(R.id.button_color_dialog_cancel);

        Button validateButton = dialog.findViewById(R.id.button_color_dialog_validate);


        colorPreview.setBackgroundColor(Color.parseColor(currentColor));
        colorinHex.setText(currentColor);
        redSeekBar.setProgress(Color.red(Color.parseColor(currentColor)));
        greenSeekBar.setProgress(Color.green(Color.parseColor(currentColor)));
        blueSeekBar.setProgress(Color.blue(Color.parseColor(currentColor)));


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveColor(redSeekBar.getProgress(),greenSeekBar.getProgress(),blueSeekBar.getProgress());
                dialog.dismiss();
            }
        });

        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                redSeekBar.setProgress(progress);
                updateUI(redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress(),
                        colorPreview,
                        colorinHex);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                greenSeekBar.setProgress(progress);
                updateUI(redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress(),
                        colorPreview,colorinHex);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                blueSeekBar.setProgress(progress);
                updateUI(redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress(),
                        colorPreview,colorinHex);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void updateUI(int r, int g, int b, TextView colorPreview, EditText colorinHex){

        colorPreview.setBackgroundColor(Color.parseColor(convertRGBToHexadecimal(r,g,b)));
        colorinHex.setText(convertRGBToHexadecimal(r,g,b));

       Log.e("Setting",convertRGBToHexadecimal(r,g,b));

    }

    private String convertRGBToHexadecimal(int r, int g, int b){

        String hex="#";
        String redHex;
        String greenHex;
        String blueHex;

        redHex =  hexValueOf(r);

        greenHex = hexValueOf(g);
        blueHex =  hexValueOf(b);

        hex = hex + redHex + greenHex + blueHex;

        return hex;
    }

    private String  hexValueOf(int number){
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while(builder.length()<2){
            builder.append("0");
        }

        return builder.toString().toUpperCase();
    }

    private void saveColor(int r, int g, int b){

        sharedPreferences.edit().putString(getString(R.string.theme_color_preference_key),
                convertRGBToHexadecimal(r,g,b)).apply();
        updateColor();
    }

}

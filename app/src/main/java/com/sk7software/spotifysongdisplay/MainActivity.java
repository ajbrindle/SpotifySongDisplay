package com.sk7software.spotifysongdisplay;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise context for preferences
        PreferencesUtil.init(getApplicationContext());
        int textSize = PreferencesUtil.getInstance().getIntPreference(PreferencesUtil.PREFERENCE_TEXT_SIZE);
        if (textSize == 0) {
            textSize = 32;
        }

        Toolbar tb = (Toolbar)findViewById(R.id.toolbar);
        tb.setTitle("Track Display");

        Switch swiShowSong = (Switch)findViewById(R.id.swiShowSong);
        boolean isChecked = PreferencesUtil.getInstance().getBooleanPreference(PreferencesUtil.PREFERNECE_SHOW_TRACK);
        swiShowSong.setChecked(isChecked);
        updateTrackReceiverService(isChecked);

        Switch swiScreenOn = (Switch)findViewById(R.id.swiScreenOn);
        isChecked = PreferencesUtil.getInstance().getBooleanPreference(PreferencesUtil.PREFERNECE_SCREEN_ON);
        swiScreenOn.setChecked(isChecked);

        // Spinner for number of tracks to show in listening history
        Spinner spiPosition = (Spinner)findViewById(R.id.spiPosition);
        ArrayAdapter<CharSequence> positionAdapter = ArrayAdapter.createFromResource(this,
                R.array.positions, android.R.layout.simple_spinner_item);
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiPosition.setAdapter(positionAdapter);

        int selectedPosition = PreferencesUtil.getInstance().getIntPreference(PreferencesUtil.PREFERENCE_DISPLAY_POSITION);
        spiPosition.setSelection(selectedPosition);
        spiPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PreferencesUtil.getInstance().addPreference(PreferencesUtil.PREFERENCE_DISPLAY_POSITION, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SeekBar seekTextSize = (SeekBar)findViewById(R.id.seekTextSize);
        seekTextSize.setProgress(textSize);
        seekTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PreferencesUtil.getInstance().addPreference(PreferencesUtil.PREFERENCE_TEXT_SIZE, progress);
                TextView txtDemo = (TextView)findViewById(R.id.txtDemo);
                txtDemo.setTextSize(progress);
                progressChangedValue = progress;
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        TextView txtDemo = (TextView)findViewById(R.id.txtDemo);
        txtDemo.setTextSize(textSize);
        txtDemo.setTypeface(Typeface.DEFAULT_BOLD);

        swiShowSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtil.getInstance().addPreference(PreferencesUtil.PREFERNECE_SHOW_TRACK, isChecked);
                updateTrackReceiverService(isChecked);
            }
        });

        swiScreenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtil.getInstance().addPreference(PreferencesUtil.PREFERNECE_SCREEN_ON, isChecked);
            }
        });

        Button btnActivate = (Button)findViewById(R.id.btnActivate);
        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }
    }

    private void updateTrackReceiverService(boolean start) {
        Intent i = new Intent(getApplicationContext(), TrackBroadcastReceiver.class);

        if (start) {
            startService(i);
            Log.d(TAG, "Track display service started");
            Toast.makeText(getApplicationContext(), "Track display service started", Toast.LENGTH_SHORT);
        } else {
            stopService(i);
            Log.d(TAG, "Track display service stopped");
            Toast.makeText(getApplicationContext(), "Track display service stopped", Toast.LENGTH_SHORT);
        }
    }
}

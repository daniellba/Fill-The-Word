package com.danielbenami_tomermaalumi.filltheword;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener
{
    private MediaPlayer mediaPlayer;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btnBrowse;
    private TextView txvExplain;
    private String explainText, path;
    private boolean musicIsOn;
    private int radioId;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Hide the Activity Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide the Activity Action Bar
        getSupportActionBar().hide();

        radioGroup = findViewById(R.id.radioGroup);
        btnBrowse = findViewById(R.id.btnBrowse);
        txvExplain = findViewById(R.id.txvExplain);

        // event listeners
        btnBrowse.setOnClickListener(this);
        explainText = "Goal: to fill the word matching the photo, by dragging each letter to the empty box." +
                "\n\nGarbege can: clear text.\nV: enter your result.\nHint: hear the word." +
                "\n\nWhen time's up: move back to first stage.";
        txvExplain.setText(explainText);
    }

    public void onClick(View v)
    {
        Log.d("debug", "onClick");

        switch (v.getId())
        {
            case R.id.btnBrowse:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
                    }
                    else
                    {
                        Intent intent = new Intent(SettingsActivity.this, SongListActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
        }
    }

    public void checkButton(View v)
    {
        radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
    }

    // for play music & sound files
    private void playMusic()
    {
        loadData();
        Uri uri = Uri.parse(path);
        Log.e("$$$Path", path);

        if (path.isEmpty())
        {
            mediaPlayer = MediaPlayer.create(this, R.raw.theme);
            mediaPlayer.setLooping(true);
        }

        else
        {
            mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.setLooping(true);
        }
    }

    //saving data to SharedPreferences
    public void saveData()
    {
        SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("radioButton", radioId);
        editor.apply();
    }

    //loading data from SharedPreferences
    public void loadData()
    {
        SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        this.musicIsOn = sp.getBoolean("switchMusic", false);
        this.path = sp.getString("musicPath", "");
        this.radioId = sp.getInt("radioButton", 2131165232);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("debug", "MainActivity onStart()");

        // start the music
        loadData();
        if (musicIsOn)
        {
            playMusic();
            mediaPlayer.start();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("debug", "SettingsActivity onStop()");

        saveData();
        if (musicIsOn)
        {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d("debug", "SettingsActivity onDestroy()");

        saveData();
        finish();
    }
}

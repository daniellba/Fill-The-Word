package com.danielbenami_tomermaalumi.filltheword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button btnPlay, btnSettings;
    private Switch switchMusic;
    private MediaPlayer mediaPlayer;
    private String path;
    private boolean musicIsOn;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the Activity Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnPlay = findViewById(R.id.btnPlay);
        btnSettings = findViewById(R.id.btnSettings);
        switchMusic = findViewById(R.id.switchMusic);

        loadData();
        switchMusic.setChecked(musicIsOn);
        playMusic();

        // event listeners
        btnPlay.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        switchMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    musicIsOn = true;
                    playMusic();
                    mediaPlayer.start();
                    saveData();
                }
                else
                {
                    musicIsOn = false;
                    mediaPlayer.pause();
                    saveData();
                }
            }
        });
    }

    public void onClick(View v)
    {
        Log.d("debug", "onClick");

        switch (v.getId())
        {
            case R.id.btnPlay:
                Intent intentGame = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intentGame);
                break;

            case R.id.btnSettings:
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
        }
    }

    //3 dots menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);


        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.ic_main_icon);


        MenuItem menuAbout = menu.add("About");
        MenuItem menuExit = menu.add("Exit");

        menuAbout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showAboutDialog();
                return true;
            }
        });

        menuExit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showExitDialog();
                return true;
            }
        });
        return true;
    }

    private void showAboutDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("About App");
        alertDialog.setMessage("Welcome to our final Project\n\nBy Daniel Ben Ami & Tomer Maalumi (c)");
        alertDialog.show();
    }

    private void showExitDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you really want to exit?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(MainActivity.this, "Bye Bye!", Toast.LENGTH_SHORT).show();
                finish();  // destroy this activity
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        alertDialog.show();
    }

    // for play music & sound files
    private void playMusic()
    {
        loadData();
        Uri uri = Uri.parse(path);
        Log.e("### Path ###", path);

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

        editor.putBoolean("switchMusic", switchMusic.isChecked());
        editor.apply();
    }

    //loading data from SharedPreferences
    public void loadData()
    {
        SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        this.musicIsOn = sp.getBoolean("switchMusic", false);
        this.path = sp.getString("musicPath", "");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("debug", "MainActivity onStart()");

        loadData();
        if (musicIsOn)
        {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("debug", "MainActivity onStop()");

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
        Log.d("debug", "MainActivity onDestroy()");

        saveData();
        finish();
    }
}
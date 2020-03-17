package com.danielbenami_tomermaalumi.filltheword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SongListActivity extends AppCompatActivity
{
    private ListView songsListView;
    private ArrayList<Song> songsList;
    private SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songsListView = findViewById(R.id.listViewID);
        songsList = new ArrayList<>();
        songAdapter = new SongAdapter(this, songsList);
        songsListView.setAdapter(songAdapter);
        setSong();

        //in the function below, we "listen" to the contact's entity, if pressed, we move to dialer
        songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.e("###", songsList.get(position).getPath().toString());

                SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("musicPath", songsList.get(position).getPath().toString());
                editor.apply();
                Toast.makeText(SongListActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setSong()
    {
        Song tempSong;
        ContentResolver resolver = getContentResolver();

        String[] projection = new String[] {
                BaseColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATA
        };
        String sortOrder = MediaStore.MediaColumns.DISPLAY_NAME + " DESC";

        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        );

        if ((cursor != null) && (cursor.moveToFirst()))
        {
            do
            {
                long id = cursor.getLong(0); //Using the field order to obtain the information
                String name = cursor.getString(1);
                Uri path = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE));
                //Log.i("###", "Audio File: ID = " + id + " Name = " + name + " Size = " + size + " Path: " + path);

                tempSong = new Song(id, name, path, size);
                songsList.add(tempSong);

                songAdapter = new SongAdapter(this, songsList);
                songsListView.setAdapter(songAdapter);
            }while(cursor.moveToNext());
        }
    }
}

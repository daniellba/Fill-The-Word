package com.danielbenami_tomermaalumi.filltheword;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<Song>
{
    public SongAdapter(Context context, ArrayList<Song> list)
    {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_row, parent, false);

        Song currentContact = getItem(position);

        TextView name = convertView.findViewById(R.id.nameID);
        name.setText(currentContact.getName());

        return convertView;
    }

}

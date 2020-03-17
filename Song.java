package com.danielbenami_tomermaalumi.filltheword;

import android.net.Uri;

public class Song
{
    private long id, size;
    private String name;
    private Uri path;

    public Song(long id, String name, Uri path, long size)
    {
        this.id = id;
        this.name = name;
        this.path = path;
        this.size = size;
    }

    public long getID(){return this.id;}
    public String getName(){return this.name;}
    public Uri getPath(){return this.path;}
    public long getSize(){return this.size;}

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", size=" + size +
                ", name='" + name + '\'' +
                ", path=" + path +
                '}';
    }
}

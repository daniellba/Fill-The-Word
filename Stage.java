package com.danielbenami_tomermaalumi.filltheword;

public class Stage
{
    private int id, length;
    private String name;
    private byte[] image;

    public Stage(int id, String name, byte[] image)
    {
        this.id = id;
        this.name = name;
        this.image = image;
        this.length = name.length();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}

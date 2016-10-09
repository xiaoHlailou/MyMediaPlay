package com.example.mymediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by hjz on 2016/10/5.
 */
public class AudioItem implements Serializable {

    private String title;
    private String artist;
    private String path;

    public static AudioItem fromCursor(Cursor cursor) {
        AudioItem item=new AudioItem();
        item.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        item.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        item.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        return item;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String duration) {
        this.artist = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

package com.example.mymediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by hjz on 2016/10/2.
 */
public class VideoItem implements Serializable{
//    MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION,
//    MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATA

    private String title;
    private long duration;
    private long size;
    private String path;

    /** 把cursor对象转换为javabean */
    public static VideoItem fromCursor(Cursor cursor) {
        VideoItem item=new VideoItem();
        item.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        item.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        item.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
        item.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));

        return item;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}

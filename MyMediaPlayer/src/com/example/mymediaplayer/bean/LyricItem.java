package com.example.mymediaplayer.bean;

/**
 * Created by hjz on 2016/10/10.
 */
public class LyricItem {

    private long startShowTime;
    private String text;

    public long getStartShowTime() {
        return startShowTime;
    }

    public void setStartShowTime(long startShowTime) {
        this.startShowTime = startShowTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LyricItem(long startShowTime, String text) {
        this.startShowTime = startShowTime;
        this.text = text;
    }

    @Override
    public String toString() {
        return "LyricItem{" +
                "startShowTime=" + startShowTime +
                ", text='" + text + '\'' +
                '}';
    }
}

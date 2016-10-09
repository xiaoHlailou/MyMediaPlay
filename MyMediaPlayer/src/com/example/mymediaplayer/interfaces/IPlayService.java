package com.example.mymediaplayer.interfaces;

import com.example.mymediaplayer.bean.AudioItem;

/**
 * Created by hjz on 2016/10/7.
 */
public interface IPlayService {

    /**
     * 打开音频
     */
    void openAudio();

    /**
     * 是否正在播放
     */
    boolean isPlaying();

    /**
     * 播放音频
     */
    void start();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 播放上一首
     */
    void pre();

    /**
     * 播放下一首
     */
    void next();

    /**
     * 跳转
     * @param position
     */
    void seekTo(int position);

    /**
     * 获取音频当前的播放位置
     */
    int getCurrentPosition();

    /**
     * 获取音频的总时长
     */
    int getDuration();

    /**
     * 切换播放模式
     * @return  返回切换后的播放模式
     */
    int switchPlayMode();

    /**
     * 返回当前播放模式
     * @return
     */
    int getCurrentPlayMode();

    AudioItem getCurrentAudioItem();
}

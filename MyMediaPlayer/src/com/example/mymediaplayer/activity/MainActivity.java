package com.example.mymediaplayer.activity;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mymediaplayer.R;
import com.example.mymediaplayer.adapter.MainAdapter;
import com.example.mymediaplayer.fragment.AudioListFragment;
import com.example.mymediaplayer.fragment.VideoListFragment;
import com.example.mymediaplayer.utils.Utils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {


    private TextView tv_audio;
    private TextView tv_video;
    private View view_indicator;
    private ViewPager mViewpager;
    private ArrayList<Fragment> fragments;
    private int pageSize;
    private int indicatorWidth;
    private int screenWidth;


    @Override
    public void initData() {
        changeTitleState(true);

        initViewpager();

        initIndicatorWidth();
    }

    /**初始化指示线的宽度*/
    private void initIndicatorWidth() {

        screenWidth = Utils.getScreenWidth(this);
        indicatorWidth = screenWidth /pageSize;

        view_indicator.getLayoutParams().width= indicatorWidth;
        view_indicator.requestLayout();//让View更新一下布局参数


    }

    private void initViewpager() {
        fragments = new ArrayList<>();
        fragments.add(new VideoListFragment());
        fragments.add(new AudioListFragment());
        pageSize = fragments.size();

        MainAdapter adapter=new MainAdapter(getSupportFragmentManager(), fragments);
        mViewpager.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        tv_video.setOnClickListener(this);
        tv_audio.setOnClickListener(this);
        mViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * @param position  当前的index
             * @param positionOffset  当前移动的百分比  完全移动为1.0f
             * @param positionOffsetPixels  当前移动的距离
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scrollIndicator(position,positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                changeTitleState(position==0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 滚动指示线
     * @param position
     * @param positionOffset
     */
    private void scrollIndicator(int position, float positionOffset) {
        float translationX = indicatorWidth*position+indicatorWidth*positionOffset;
//        ViewPropertyAnimator animator;
        ViewHelper.setTranslationX(view_indicator,translationX);

    }

    @Override
    public void initView() {
        tv_audio = findView(R.id.tv_audio);
        tv_video = findView(R.id.tv_video);
        view_indicator = findViewById(R.id.view_indicator);
        mViewpager = findView(R.id.viewpager);
    }

    @Override
    public void onClick(View v, int id) {
        switch (id) {
            case R.id.tv_audio:
                mViewpager.setCurrentItem(1);
                break;
            case R.id.tv_video:
                mViewpager.setCurrentItem(0);
                break;
        }

    }

    /**
     * 改变ViewpagerTab样式  视频、音频
     * @param isSelectVideo  true时为视频
     */
    private void changeTitleState(boolean isSelectVideo) {
        //改变标题的颜色状态
        tv_video.setSelected(isSelectVideo);
        tv_audio.setSelected(!isSelectVideo);

        //缩放标题  动画 用nineold包
        scaleTitle(isSelectVideo ? 1.4f : 1.0f,tv_video);
        scaleTitle(!isSelectVideo ? 1.4f : 1.0f,tv_audio);

    }

    /**
     * 缩放Title
     * @param sclae 比例，放大为>1.0f
     * @param textView 对象
     */
    private void scaleTitle(float sclae, TextView textView) {
        ViewPropertyAnimator.animate(textView).scaleX(sclae).scaleY(sclae);//缩放动画效果
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


}

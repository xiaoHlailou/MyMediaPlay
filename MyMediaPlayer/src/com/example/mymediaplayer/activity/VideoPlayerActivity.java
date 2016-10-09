package com.example.mymediaplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mymediaplayer.R;
import com.example.mymediaplayer.bean.VideoItem;
import com.example.mymediaplayer.interfaces.Keys;
import com.example.mymediaplayer.utils.LogUtils;
import com.example.mymediaplayer.utils.Utils;
import com.example.mymediaplayer.view.VideoView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;


/**
 * Created by hjz on 2016/10/3.
 */
public class VideoPlayerActivity extends BaseActivity {
    /**
     * 手势探测器
     */
    private GestureDetector gestureDetector;

    private VideoView videoView;
    private ArrayList<VideoItem> videoItems;
    private int currentPosition;
    private VideoItem currentVideoItem;
    private TextView tv_title;
    private ImageView iv_battery;
    private TextView tv_system_time;
    private AudioManager audioManager;
    private int maxVolume;
    private int currentVolume;
    private float currentBrightness;
    private SeekBar sb_voice;
    private Button btn_voice;
    private TextView tv_current_position;
    private TextView tv_duration;
    private SeekBar sb_video;
    private Button btn_exit;
    private Button btn_pre;
    private Button btn_play;
    private Button btn_next;
    private Button btn_fullscreen;

    /**
     * 用于更新系统时间
     */
    private static final int UPDATE_SYSTEM_TIME = 0;        //更新系统时间
    private static final int UPDATE_CURRENT_POSITION = 1;   //更新当前播放位置
    private static final int HIDE_CTRL_LAYOUT = 2;          //隐藏控制面板
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_SYSTEM_TIME:
                    updateSystemTime();
                    break;
                case UPDATE_CURRENT_POSITION:
                    updateCurrentPosition();
                    break;
                case HIDE_CTRL_LAYOUT:
                    toggleCtrlLayout();
                    break;
                default:
                    break;
            }
        }
    };
    private float maxVolumedScreenHeightScale;
    private float maxBrightnessScreenHeightScale;
    private View view_brightness;
    private LinearLayout ll_top_ctrl;
    private LinearLayout ll_bottom_ctrl;
    private LinearLayout ll_loading;

    @Override
    public void initData() {

        Uri videoUri=getIntent().getData();
        if (videoUri != null) {
            //说明是从第三方应用跳转过来
            videoView.setVideoURI(videoUri);
            tv_title.setText(videoUri.getPath());
            btn_next.setEnabled(false);
            btn_pre.setEnabled(false);
        } else {
            //说明是从视频列表点击过来的
            videoItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra(Keys.ITEM_LIST);
            currentPosition = getIntent().getIntExtra(Keys.CURRENT_POSITION, -1);
            openVideo();
        }

        initVolume();//初始化音量

        // 3.计算音量最大值与屏幕高的比例(最终算什么值，这个值作为被除数)   mOnGestureListener
        maxVolumedScreenHeightScale = ((float) maxVolume) / Utils.getScreenHeight(this);
        // 3.计算亮度最大值与屏幕高的比例   伪实现
        maxBrightnessScreenHeightScale = 1.0f / Utils.getScreenHeight(this);


    }

    /**
     * 播放第currentPosition个音频
     * @return
     */
    private void openVideo () {
        if (videoItems == null || videoItems.isEmpty() || currentPosition == -1) {
            return;
        }

        ll_loading.setVisibility(View.VISIBLE);

        btn_pre.setEnabled(currentPosition!=0);
        btn_next.setEnabled(currentPosition!=videoItems.size()-1);

        currentVideoItem = videoItems.get(currentPosition);
        videoView.setVideoPath(currentVideoItem.getPath());
    }

    /**
     * 注册电量改变接收者
     */
    private void registerBatteryChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryChangedReceiver, intentFilter);
    }

    /**
     * 初始化音量
     */
    private void initVolume() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = getStreamVolume();
        sb_voice.setMax(maxVolume);
        sb_voice.setProgress(currentVolume);
    }


    @Override
    public void initListener() {
        registerBatteryChangedReceiver();

        videoView.setOnPreparedListener(mOnPreparedListener);
        videoView.setOnCompletionListener(mOnCompletionListener);
        videoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        videoView.setOnInfoListener(mOnInfoListener);

        //滑动音量条
        sb_voice.setOnSeekBarChangeListener(mAudioOnSeekBarChangeListener);
        //滑动进度条  实现快进快退
        sb_video.setOnSeekBarChangeListener(mVideoOnSeekBarChangeListener);

        //手势滑动
        gestureDetector = new GestureDetector(this, mOnGestureListener);

    }

    @Override
    public void initView() {
        videoView = findView(R.id.video_view);

        ll_top_ctrl = findView(R.id.ll_top_ctrl);
        ll_bottom_ctrl = findView(R.id.ll_bottom_ctrl);
        ll_loading = findView(R.id.ll_loading);
        ll_loading.setVisibility(View.VISIBLE);

        tv_title = findView(R.id.tv_title);
        iv_battery = findView(R.id.iv_battery);
        tv_system_time = findView(R.id.tv_system_time);

        btn_voice = findView(R.id.btn_voice);
        sb_voice = findView(R.id.sb_voice);

        tv_current_position = findView(R.id.tv_current_position);
        tv_duration = findView(R.id.tv_duration);
        sb_video = findView(R.id.sb_video);

        btn_exit = findView(R.id.btn_exit);
        btn_pre = findView(R.id.btn_pre);
        btn_play = findView(R.id.btn_play);
        btn_next = findView(R.id.btn_next);
        btn_fullscreen = findView(R.id.btn_fullscreen);

        view_brightness = findView(R.id.view_brightness);
        view_brightness.setVisibility(View.VISIBLE);
        ViewHelper.setAlpha(view_brightness, 0.0f);

        initCtrlLayout();
    }

    /** 初始化控制面板 隐藏 */
    private void initCtrlLayout() {
        //onMeasure onLayout onDraw  刚刚初始化后的控件还没onMeasure
        ll_top_ctrl.measure(0,0);   //让系统主动测量控件的宽高 onMeasure
//        float topTranslationY=ll_top_ctrl.getHeight();
        //顶部控制栏隐藏：Y方向移动控件的高度的负数
        float topTranslationY=ll_top_ctrl.getMeasuredHeight();
        ViewHelper.setTranslationY(ll_top_ctrl,-topTranslationY);

        //底部控制栏隐藏：Y方向移动控件的高度
        ll_bottom_ctrl.measure(0,0);//让系统主动测量控件的宽高 onMeasure
        float bottomTranslationY=ll_bottom_ctrl.getMeasuredHeight();
        ViewHelper.setTranslationY(ll_bottom_ctrl,bottomTranslationY);
    }

    @Override
    public void onClick(View v, int id) {
        removeHideCtrlLayoutMessage();
        switch (id) {
            case R.id.btn_voice:
                toggleMute();
                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_pre:
                pre();
                break;
            case R.id.btn_play:
                playOrPause();
                break;
            case R.id.btn_next:
                next();
                break;
            case R.id.btn_fullscreen:
                toggleFullscreen();
                break;

            default:
                break;
        }
        sendHideCtrlLayoutMessage();
    }

    /** 在全屏和默认大小之间进行切换 */
    private void toggleFullscreen() {
        videoView.toggleFullscreen();
        updateFullscreenBtnBg();
    }


    /** 播放下一个视频 */
    private void next() {
        if (currentPosition!=videoItems.size()-1){
            currentPosition++;
            openVideo();
        }
    }

    /** 播放上一个视频 */
    private void pre() {
        if (currentPosition > 0) {
            currentPosition--;
            openVideo();
        }
    }


    /**
     * 静音切换
     */
    private void toggleMute() {
        if (getStreamVolume() > 0) {
            currentVolume = getStreamVolume();
            setStreamVolume(0);
            sb_voice.setProgress(0);
        } else { //音量=0 恢复原来的音量
            setStreamVolume(currentVolume);
            sb_voice.setProgress(currentVolume);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSystemTime();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(UPDATE_SYSTEM_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBatteryChangedReceiver != null) {
            unregisterReceiver(mBatteryChangedReceiver);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);//把事件传进手势探测器

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                removeHideCtrlLayoutMessage();
                break;
            case MotionEvent.ACTION_UP:
                sendHideCtrlLayoutMessage();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     *  视频缓冲卡顿的监听器
     */
    MediaPlayer.OnInfoListener mOnInfoListener=new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频缓冲卡顿开始
                    ll_loading.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频缓冲卡顿结束
                    hideLoadingDialog();
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    /**
     * 视频播放器缓存更新的监听器
     */
    MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener=new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            updateSecondaryProgress(percent);
        }
    };


    /**
     * 视频播放器准备阶段完成
     */
    MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {

        private int duration;

        @Override
        public void onPrepared(MediaPlayer mp) {
            videoView.start();                                  //开始播放视频
            duration = videoView.getDuration();                 //获取视频总长度

            if (currentVideoItem != null) {
                tv_title.setText(currentVideoItem.getTitle());      //设置标题
            }

            tv_duration.setText(Utils.formatMillis(duration));  //获取视频总长度
            sb_video.setMax(duration);
            updateCurrentPosition();                            //更新播放位置时间
            updatePlayBtnBg();                                  //更新播放按钮背景
            updateFullscreenBtnBg();                            //更新全屏按钮背景

            hideLoadingDialog();
        }
    };


    /**
     * 视频播放结束的回调监听器
     */
    MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            videoView.seekTo(0);//把进度调为0
            tv_current_position.setText(Utils.formatMillis(0L));
        }
    };


    BroadcastReceiver mBatteryChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            LogUtils.i(VideoPlayerActivity.this,intent.getExtras().keySet().toString());
            int level = intent.getIntExtra("level", 0);
            updateBatteryBg(level);
        }
    };

    SeekBar.OnSeekBarChangeListener mAudioOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override   //进度发生改变  fromUser代表是否是用户触发的
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                setStreamVolume(progress);
            }
        }

        @Override   //开始拖动
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeHideCtrlLayoutMessage();
        }

        @Override   //停止拖动
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendHideCtrlLayoutMessage();
        }
    };

    SeekBar.OnSeekBarChangeListener mVideoOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoView.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeHideCtrlLayoutMessage();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendHideCtrlLayoutMessage();
        }
    };

    GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        private boolean isLeftDown;

        @Override   //双击  实现全屏切换
        public boolean onDoubleTap(MotionEvent e) {
//            toggleFullscreen();
//            btn_fullscreen.performClick();  //通过代码方式 执行单击
            onClick(btn_fullscreen);
            return true;
        }

        @Override   //按下
        public boolean onDown(MotionEvent e) {
            currentVolume = getStreamVolume();
            currentBrightness = ViewHelper.getAlpha(view_brightness);
            isLeftDown = e.getX() < Utils.getScreenWidth(getApplication()) / 2;
            return super.onDown(e);
        }

        @Override   //滚动
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float distanceYY = e1.getY() - e2.getY();
            if (isLeftDown) {//改变屏幕亮度
                changeBrightness(distanceYY);
            } else {//改变音量值
                changeVolumn(distanceYY);
            }
            return true;
        }

        @Override   //长按
        public void onLongPress(MotionEvent e) {
            playOrPause();
        }

        @Override   //单击
        public boolean onSingleTapConfirmed(MotionEvent e) {
            toggleCtrlLayout();

            return true;
        }
    };


    /**
     * 改变亮度值  伪实现  用透明度
     *
     * @param distanceYY
     */
    private void changeBrightness(float distanceYY) {
        // 1.触摸事件（onTouchEvent方法中处理）
        // 2.识别手势（GuestureDetactor）
        // 3.计算亮度最大值与屏幕高的比例  在initData()中实现
        // 4.计算移动的距离等于多少对应的亮度值
        float moveBrightness = -distanceYY * maxBrightnessScreenHeightScale;
        // 5.在原来亮度的基础上加上移动对应的亮度值
        float result = currentBrightness + moveBrightness;

        //预防超出范围
        if (result < 0) {
            result = 0f;
        } else if (result > 0.8) {
            result = 0.8f;
        }
        LogUtils.i(this, "brightness:" + result);

        ViewHelper.setAlpha(view_brightness, result);

    }

    /**
     * 改变音量值
     *
     * @param distanceYY 触摸滑动前后的distanceY
     */
    private void changeVolumn(float distanceYY) {
        // 1.触摸事件（onTouchEvent方法中处理）
        // 2.识别手势（GuestureDetactor）
        // 3.计算音量最大值与屏幕高的比例   在initData()中实现
        // 4.计算移动的距离等于多少对应的音量值
        int moveVolumn = (int) (distanceYY * maxVolumedScreenHeightScale);
        // 5.在原来音量的基础上加上移动对应的音量值
        int result = currentVolume + moveVolumn;

        //预防超出范围
        if (result > maxVolume) {
            result = maxVolume;
        } else if (result < 0) {
            result = 0;
        }
        LogUtils.i(VideoPlayerActivity.this, "volumn" + result + "");
        setStreamVolume(result);
        sb_voice.setProgress(result);
    }

    /**
     * 设置系统音量
     *
     * @param index 音量值
     */
    private void setStreamVolume(int index) {
        int streamType = AudioManager.STREAM_MUSIC;
        int flags = 1;    //0：不显示系统音量面板  1：显示系统音量面板
        audioManager.setStreamVolume(streamType, index, flags);
    }

    /**
     * 更新电量背景图片
     *
     * @param level
     */
    private void updateBatteryBg(int level) {
        int resId;
        if (level == 0) {
            resId = R.drawable.ic_battery_0;
        } else if (level <= 10) {
            resId = R.drawable.ic_battery_10;
        } else if (level <= 20) {
            resId = R.drawable.ic_battery_20;
        } else if (level <= 40) {
            resId = R.drawable.ic_battery_40;
        } else if (level <= 60) {
            resId = R.drawable.ic_battery_60;
        } else if (level <= 80) {
            resId = R.drawable.ic_battery_80;
        } else {
            resId = R.drawable.ic_battery_100;
        }

        iv_battery.setBackgroundResource(resId);
    }

    /**
     * 更新系统时间
     */
    private void updateSystemTime() {
        tv_system_time.setText(DateFormat.format("kk:mm:ss", System.currentTimeMillis()));
        handler.sendEmptyMessageDelayed(UPDATE_SYSTEM_TIME, 1000);//监听器
    }

    /**
     * 更新当前视频播放时间
     */
    private void updateCurrentPosition() {
        int currentPosition = videoView.getCurrentPosition();
        tv_current_position.setText(Utils.formatMillis(currentPosition));
        sb_video.setProgress(currentPosition);
        handler.sendEmptyMessageDelayed(UPDATE_CURRENT_POSITION, 300);//防止异步数据有误，循环速率加快
    }

    /**
     * 获取音量值
     */
    private int getStreamVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 播放或者暂停
     */
    private void playOrPause() {
        if (videoView.isPlaying()) {
            videoView.pause();
        } else {
            videoView.start();
        }
        updatePlayBtnBg();
    }

    /** 更新播放按钮背景 */
    private void updatePlayBtnBg() {
        int resId;
        if (videoView.isPlaying()) {
            resId = R.drawable.selector_btn_pause;
        } else {
            resId = R.drawable.selector_btn_play;
        }
        btn_play.setBackgroundResource(resId);
    }

    /** 更新全屏按钮背景 */
    private void updateFullscreenBtnBg() {
        int resId;
        if (videoView.isFullscreen()) {
            resId = R.drawable.selector_btn_defaultscreen;
        } else {
            resId=R.drawable.selector_btn_fullscreen;
        }
        btn_fullscreen.setBackgroundResource(resId);
    }

    /** 显示或隐藏控制面板 */
    private void toggleCtrlLayout() {
        float translationY = ViewHelper.getTranslationY(ll_top_ctrl);
        if (translationY == 0) {
            //如果原来是显示的，则隐藏  translationY 移动到哪里
            ViewPropertyAnimator.animate(ll_top_ctrl).translationY(-ll_top_ctrl.getHeight());
            ViewPropertyAnimator.animate(ll_bottom_ctrl).translationY(ll_bottom_ctrl.getHeight());

            removeHideCtrlLayoutMessage();
        }else {
            //如果原来是隐藏的，则显示
            ViewPropertyAnimator.animate(ll_top_ctrl).translationY(0f);
            ViewPropertyAnimator.animate(ll_bottom_ctrl).translationY(0f);

            //显示控制面板后 启动5秒后隐藏的定时器
            sendHideCtrlLayoutMessage();
        }
    }

    /** 发送隐藏控制面板的消息 */
    private void sendHideCtrlLayoutMessage() {
        removeHideCtrlLayoutMessage();
        handler.sendEmptyMessageDelayed(HIDE_CTRL_LAYOUT, 5000);
    }

    /** 移除隐藏控制面板的消息 */
    private void removeHideCtrlLayoutMessage() {
        handler.removeMessages(HIDE_CTRL_LAYOUT);
    }

    /** 隐藏正在加载Dialog */
    private void hideLoadingDialog() {
//        ll_loading.setVisibility(View.GONE);//没有动画
        //animate指定对象、alpha设置变化到xx透明度、duration持续时间
        ViewPropertyAnimator.animate(ll_loading).alpha(0.0f).setDuration(1500).setListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {}
            public void onAnimationCancel(Animator animation) {}
            public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                ll_loading.setVisibility(View.GONE);
                ViewHelper.setAlpha(ll_loading,1.0f);//设置透明度 给下次使用
            }
        });
    }

    /**
     * 更新第二缓冲进度
     * @param percent
     */
    private void updateSecondaryProgress(int percent) {
        float percentFloat=percent/100f;
        int secondaryProgress= (int) (videoView.getDuration()*percentFloat);
        sb_video.setSecondaryProgress(secondaryProgress);
    }
}

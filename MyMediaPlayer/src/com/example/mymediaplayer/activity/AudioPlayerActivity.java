package com.example.mymediaplayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mymediaplayer.R;
import com.example.mymediaplayer.bean.AudioItem;
import com.example.mymediaplayer.interfaces. IPlayService;
import com.example.mymediaplayer.interfaces.IUi;
import com.example.mymediaplayer.interfaces.Keys;
import com.example.mymediaplayer.service.AudioPlayService;
import com.example.mymediaplayer.utils.LogUtils;
import com.example.mymediaplayer.utils.Utils;
import com.example.mymediaplayer.view.LyricView;

import java.util.ArrayList;

/**
 * Created by hjz on 2016/10/5.
 */
public class AudioPlayerActivity extends BaseActivity implements IUi{

    /** 更新播放时间 */
    public static final int UPDATE_PLAY_TIME = 0;


    private IPlayService playService;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AudioPlayService.PLAY_SERVICE_INTERFACE:
                    playService = (IPlayService) msg.obj;
                    //在这个地方Service和Activity都拿到对方的引用,这时候再去播放音频
                    if (msg.arg1 == AudioPlayService.NO_OPEN_AUDIO) {
                        //不打开 更新ui
                        updateUI(playService.getCurrentAudioItem());
                    } else {
                        playService.openAudio();
                    }
                    break;
                case UPDATE_PLAY_TIME:
                    updatePlayTime();
                    break;
                default:
                    break;

            }
        }
    };

    private Messenger uiMessenger = new Messenger(handler);
    private ServiceConnection conn;
    private Button btn_play;
    private TextView tv_artist;
    private TextView tv_title;
    private TextView tv_play_time;
    private SeekBar sb_audio;
    private ImageView iv_vision;
    private Button btn_play_mode;
    private CharSequence duration;
    private LyricView lyric_view;


    @Override
    public void initData() {
        ArrayList<AudioItem> audioItems = (ArrayList<AudioItem>) getIntent().getSerializableExtra(Keys.ITEM_LIST);
        int position = getIntent().getIntExtra(Keys.CURRENT_POSITION, -1);

        connectService(audioItems, position);

    }

    /** 开启服务 */
    private void connectService(ArrayList<AudioItem> audioItems, int position) {
        LogUtils.i("www","bindService");

        Intent service = new Intent(this, AudioPlayService.class);
        service.putExtra(Keys.ITEM_LIST, audioItems);
        service.putExtra(Keys.CURRENT_POSITION, position);
        //如果点击标题栏ll_root，service传过来what，接收后再传到service的startCommend
        service.putExtra(Keys.NOTIFICATION_WHAT,getIntent().getIntExtra(Keys.NOTIFICATION_WHAT,-1));
        startService(service);
        //服务连接成功  binder是service的handler
        //服务断开
        conn = new ServiceConnection() {
            @Override   //服务连接成功  binder是service的handler
            public void onServiceConnected(ComponentName name, IBinder binder) {
                LogUtils.i(AudioPlayerActivity.this, "服务链接成功");
                Messenger playServiceMessenger = new Messenger(binder);

                Message message = new Message();
                message.what = AudioPlayService.UI_INTERFACE;
                message.obj = AudioPlayerActivity.this;
                message.replyTo = uiMessenger;
                try {
                    playServiceMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            @Override   //服务断开
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(service, conn, BIND_AUTO_CREATE);
    }

    @Override
    public void initListener() {
        sb_audio.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    @Override
    public void initView() {
        btn_play = findView(R.id.btn_play);
        tv_artist = findView(R.id.tv_artist);
        tv_title = findView(R.id.tv_title);
        tv_play_time = findView(R.id.tv_play_time);
        sb_audio = findView(R.id.sb_audio);
        iv_vision = findView(R.id.iv_vision);
        btn_play_mode = findView(R.id.btn_play_mode);
        lyric_view = findView(R.id.lyric_view);
        //加载帧动画
        AnimationDrawable anim= (AnimationDrawable) iv_vision.getBackground();
        anim.start();
    }

    @Override
    public void onClick(View v, int id) {
        switch (id) {
            case R.id.btn_play:
                play();
                break;
            case R.id.btn_pre:
                pre();
                break;
            case R.id.btn_next:
                LogUtils.i("www","next,Activity");
                next();
                break;
            case R.id.btn_play_mode:
                switchPlayMode();
                break;

            default:
                break;
        }
    }

    /** 切换播放模式 */
    private void switchPlayMode() {
        int currentPlayMode = playService.switchPlayMode();

        updatePlayModeBtnBg(currentPlayMode);
    }

    /** 更新播放模式的背景 */
    private void updatePlayModeBtnBg(int currentPlayMode) {
        int resId;
        switch (currentPlayMode) {
            case AudioPlayService.PLAY_MODE_ORDER:
                resId=R.drawable.selector_audio_btn_playmode_order;
                break;
            case AudioPlayService.PLAY_MODE_SINGLE:
                resId=R.drawable.selector_audio_btn_playmode_single;
                break;
            case AudioPlayService.PLAY_MODE_RANDOM:
                resId=R.drawable.selector_audio_btn_playmode_random;
                break;
            default:
                throw new RuntimeException("播放方式异常");
        }
        btn_play_mode.setBackgroundResource(resId);
    }

    /** 播放下一首 */
    private void next() {
        LogUtils.i("www","nextA");
        playService.next();
    }

    /** 播放上一首 */
    private void pre() {
        playService.pre();
    }

    /** 播放或暂停 */
    private void play() {
        if (playService.isPlaying()) {
            playService.pause();
        } else {
            playService.start();
        }

        updatePlayBtnBg();
    }

    /** 更新播放按钮背景 */
    private void updatePlayBtnBg() {
        int resId;
        if (playService.isPlaying()) {
            //如果正在播放，则显示一个暂停按钮
            resId=R.drawable.selector_btn_pause_audio;
        } else {
            //如果正在暂停，则显示一个播放按钮
            resId=R.drawable.selector_btn_play_audio;
        }
        btn_play.setBackgroundResource(resId);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_audio_player;
    }

    /** 在service的mediaplayer的prepared监听器 */
    @Override
    public void updateUI(AudioItem item) {
        LogUtils.i(this, "updateUI");
        updatePlayBtnBg();
        tv_title.setText(item.getTitle());
        tv_artist.setText(item.getArtist());
        sb_audio.setMax(playService.getDuration());
        lyric_view.setMusicPath(item.getPath());

        duration = Utils.formatMillis(playService.getDuration());
        updatePlayTime();
        updatePlayModeBtnBg(playService.getCurrentPlayMode());
    }

    /** 更新播放时间 */
    private void updatePlayTime() {
        int position=playService.getCurrentPosition();
        CharSequence currentPosition= Utils.formatMillis(position);

        tv_play_time.setText(currentPosition+"/"+duration);
        sb_audio.setProgress(playService.getCurrentPosition());
        lyric_view.setCurrentPosition(position);

        handler.sendEmptyMessageDelayed(UPDATE_PLAY_TIME, 30);
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                playService.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}

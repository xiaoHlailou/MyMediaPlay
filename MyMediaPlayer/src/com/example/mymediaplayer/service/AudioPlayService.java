package com.example.mymediaplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.mymediaplayer.R;
import com.example.mymediaplayer.activity.AudioPlayerActivity;
import com.example.mymediaplayer.bean.AudioItem;
import com.example.mymediaplayer.interfaces.IPlayService;
import com.example.mymediaplayer.interfaces.IUi;
import com.example.mymediaplayer.interfaces.Keys;
import com.example.mymediaplayer.utils.LogUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by hjz on 2016/10/6.
 */
public class AudioPlayService extends Service implements IPlayService {

    /**
     * 通知栏常量
     */
    int notificationId = 1;
    private static final int NOTIFICATION_PRE = 1;
    private static final int NOTIFICATION_NEXT = 2;
    private static final int NOTIFICATION_ENTER = 3;
    private NotificationManager notificationManager;

    /**
     * UI接口
     */
    public static final int UI_INTERFACE = 0;
    /**
     * 服务接口
     */
    public static final int PLAY_SERVICE_INTERFACE = 1;

    /**
     * open为-1，不打开（listview或消息蓝点同一首歌）为1 不执行openAudio()
     */
    public int openAudioFlag;
    public static int NO_OPEN_AUDIO = 1;

    /**
     * 播放模式:顺序播放
     */
    public static final int PLAY_MODE_ORDER = 1;
    /**
     * 播放模式:随机播放
     */
    public static final int PLAY_MODE_RANDOM = 2;
    /**
     * 播放模式:单曲循环播放
     */
    public static final int PLAY_MODE_SINGLE = 3;
    /**
     * 当前的播放模式  持久化
     */
    public int currentPlayMode = PLAY_MODE_ORDER;
    private SharedPreferences sp;
    private Random random;

    private MediaPlayer mMediaPlayer;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UI_INTERFACE:
                    ui = (IUi) msg.obj;

                    Messenger uiMessenger = msg.replyTo;
                    Message message = new Message();
                    message.what = PLAY_SERVICE_INTERFACE;
                    message.obj = AudioPlayService.this;
                    message.arg1 = openAudioFlag;
                    try {
                        uiMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }


        }
    };

    private Messenger messenger = new Messenger(handler);
    private ArrayList<AudioItem> audioItems;
    private int currentPosition;
    private IUi ui;
    private AudioItem currentAudioItem;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        random = new Random();
        LogUtils.i(AudioPlayService.this, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(AudioPlayService.this, "onStartCommand");
        currentPlayMode = sp.getInt(Keys.CURRENT_PLAY_MODE, PLAY_MODE_ORDER);

        openAudioFlag = -1;//重置
        int notificationWhat = intent.getIntExtra(Keys.NOTIFICATION_WHAT, -1);
        switch (notificationWhat) {
            case NOTIFICATION_PRE:
                pre();
                break;
            case NOTIFICATION_NEXT:
                next();
                break;
            case NOTIFICATION_ENTER:
                openAudioFlag = NO_OPEN_AUDIO;
                break;
            default:    //不是Notification点进来   是从列表点进来
                audioItems = (ArrayList<AudioItem>) intent.getSerializableExtra(Keys.ITEM_LIST);
                int currentPositionTemp = intent.getIntExtra(Keys.CURRENT_POSITION, -1);
                if (currentPositionTemp == currentPosition && isPlaying()) {
//            if (!isPlaying()) {
//                mMediaPlayer.start();
//            }
                    openAudioFlag = NO_OPEN_AUDIO;
                }
                currentPosition = currentPositionTemp;
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 打开一个音频文件
     */
    @Override
    public void openAudio() {
        if (audioItems == null || audioItems.isEmpty() || currentPosition == -1) {
            return;
        }

        currentAudioItem = audioItems.get(currentPosition);

        //其他播放器暂停播放
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        sendBroadcast(i);

        release();//销毁mMediaPlayer

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setDataSource(this, Uri.parse(currentAudioItem.getPath()));
            mMediaPlayer.prepareAsync();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 清空mMediaPlayer
     */
    private void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i(AudioPlayService.this, "onBind");
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.i(AudioPlayService.this, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(this, "onDestroy");
    }

    /**  */
    @Override
    public void start() {
        LogUtils.i(this, "start");
        if (mMediaPlayer != null) {
            sendNotification();
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        LogUtils.i(this, "pause");
        if (mMediaPlayer != null) {
            notificationManager.cancel(notificationId);
            mMediaPlayer.pause();
        }
    }

    @Override
    public void pre() {
        switch (currentPlayMode) {
            case PLAY_MODE_ORDER:
                if (currentPosition != 0) {
                    currentPosition--;
                } else {
                    currentPosition = audioItems.size() - 1;
                }
                break;
            case PLAY_MODE_RANDOM:
                currentPosition = random.nextInt(audioItems.size());
                break;
            case PLAY_MODE_SINGLE:
                break;
        }
        openAudio();
    }

    @Override
    public void next() {
        LogUtils.i("www","next");
        switch (currentPlayMode) {
            case PLAY_MODE_ORDER:
                if (currentPosition != audioItems.size()-1) {
                    currentPosition++;
                } else {
                    currentPosition = 0;
                }
                break;
            case PLAY_MODE_RANDOM:
                currentPosition = random.nextInt(audioItems.size());
                break;
            case PLAY_MODE_SINGLE:
                break;
        }
        openAudio();
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {//一直被调用
        LogUtils.i("www","getduration");
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int switchPlayMode() {
        switch (currentPlayMode) {
            case PLAY_MODE_ORDER://顺序转单曲
                currentPlayMode = PLAY_MODE_SINGLE;
                break;
            case PLAY_MODE_SINGLE://单曲转随机
                currentPlayMode = PLAY_MODE_RANDOM;
                break;
            case PLAY_MODE_RANDOM://随机转顺序
                currentPlayMode = PLAY_MODE_ORDER;
                break;
            default:
                throw new RuntimeException("播放模式出错");
        }
        sp.edit().putInt(Keys.CURRENT_PLAY_MODE, currentPlayMode).commit();

        return currentPlayMode;
    }

    @Override
    public int getCurrentPlayMode() {
        return currentPlayMode;
    }

    @Override
    public AudioItem getCurrentAudioItem() {
        return currentAudioItem;
    }


    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            start();
            ui.updateUI(currentAudioItem);
        }
    };

    /**
     * 播放结束监听器
     */
    MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    };

    public void sendNotification() {
        // 一闪而过的小图标和文本
        int icon = R.drawable.icon_notification;
        CharSequence tickerText = "当前正在播放：" + currentAudioItem.getTitle();
        long when = System.currentTimeMillis();

        // 通知栏的东西
        CharSequence contentTitle = currentAudioItem.getTitle();
        CharSequence contentText = currentAudioItem.getArtist();
        PendingIntent pendingIntent = getActivityPendingIntent(NOTIFICATION_ENTER);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        builder.setSmallIcon(icon)
                .setTicker(tickerText)
                .setWhen(when)
                .setContentText(contentTitle)   //适配Content低版本
                .setContentTitle(contentText)   //适配Content低版本
                .setContentIntent(pendingIntent)//适配Content低版本
                .setContent(getRemoteViews());// 这个方法在3.0之后才显示 Content高版本

        Notification notification = builder.build();
        notification.flags=Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(notificationId, notification);

    }

    /**
     * 高版本自定义通知栏
     *
     * @return
     */
    private RemoteViews getRemoteViews() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.notification);

        remoteViews.setTextViewText(R.id.tv_title, currentAudioItem.getTitle());
        remoteViews.setTextViewText(R.id.tv_artist, currentAudioItem.getArtist());
        remoteViews.setOnClickPendingIntent(R.id.btn_pre,
                getServicePendingIntent(NOTIFICATION_PRE));
        remoteViews.setOnClickPendingIntent(R.id.btn_next,
                getServicePendingIntent(NOTIFICATION_NEXT));
        remoteViews.setOnClickPendingIntent(R.id.ll_root,
                getActivityPendingIntent(NOTIFICATION_ENTER));

        return remoteViews;
    }

    private PendingIntent getActivityPendingIntent(int requestCode) {
        LogUtils.i("www","getActivityPending");
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(Keys.NOTIFICATION_WHAT, requestCode);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                requestCode, intent, flags);
        return pendingIntent;
    }

    private PendingIntent getServicePendingIntent(int requestCode) {
        LogUtils.i("www","getServicePending");
        Intent intent = new Intent(this, AudioPlayService.class);
        intent.putExtra(Keys.NOTIFICATION_WHAT, requestCode);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getService(this,
                requestCode, intent, flags);
        return pendingIntent;
    }

}

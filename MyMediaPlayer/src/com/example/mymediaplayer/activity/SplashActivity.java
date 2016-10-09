package com.example.mymediaplayer.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.example.mymediaplayer.R;

/**
 * Created by hjz on 2016/9/20.
 */
public class SplashActivity extends BaseActivity {

    Handler handler = new Handler();

    @Override
    public void initData() {
        delayEnterHome();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacksAndMessages(null);
                enterHome();
                break;
            default:
                break;
        }
        return true;
    }

    /**延迟3秒后进入首页*/
    private void delayEnterHome() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                enterHome();
            }
        }, 3000);
    }

    /**重写按下返回键 使splash界面不能返回*/
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    /**进入首页 MainActivity*/
    private void enterHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void onClick(View v, int id) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }
}

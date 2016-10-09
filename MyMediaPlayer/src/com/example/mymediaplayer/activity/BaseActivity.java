package com.example.mymediaplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mymediaplayer.R;
import com.example.mymediaplayer.interfaces.UIOperation;
import com.example.mymediaplayer.utils.Utils;

/**
 * Created by hjz on 2016/9/19.
 */
public abstract class BaseActivity extends FragmentActivity implements UIOperation{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        /** android.R.id.content这个id可以获取到Activity的根view */ 
        View rootView = findViewById(android.R.id.content);
        Utils.findButtonSetOnclickListener(rootView,this);
        Button btn = findView(R.id.btn_back);
        initView();
        initListener();
        initData();
    }



    /**使不用强转*/
    public <T> T findView(int id) {
        T view= (T) super.findViewById(id);
        return view;
    }

    /**在屏幕中央显示Toast*/
    public void showToast(String text) {
        Utils.showToast(this,text);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                animateOnExit();
                break;

            default:
                onClick(v, v.getId());
                break;
        }
    }

    /**
     * 界面退出时添加动画切换效果 必须在finish（）后调用
     */
    protected void animateOnExit() {
        overridePendingTransition(R.anim.alpha_unchanged,
                R.anim.push_right_out);
    }
    /**
     * 界面进入时添加动画切换效果 必须在finish（）后调用
     */
    protected void animateOnEnter() {
        overridePendingTransition(R.anim.push_right_in
                ,R.anim.alpha_unchanged);
    }

}

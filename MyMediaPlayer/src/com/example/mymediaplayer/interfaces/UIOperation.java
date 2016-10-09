package com.example.mymediaplayer.interfaces;

import android.view.View;

/**
 * UI操作的接口
 * Created by hjz on 2016/10/1.
 */
public interface UIOperation extends View.OnClickListener{

    void initData();

    void initListener();

    void initView();

    void onClick(View v, int id);

    /**初始化数据  并显示到界面上*/
    int getLayoutId() ;

}

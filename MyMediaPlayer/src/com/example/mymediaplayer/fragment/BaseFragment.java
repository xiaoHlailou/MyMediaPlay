package com.example.mymediaplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mymediaplayer.R;
import com.example.mymediaplayer.interfaces.UIOperation;
import com.example.mymediaplayer.utils.Utils;

/**
 * Created by hjz on 2016/10/1.
 */
public abstract class BaseFragment extends Fragment implements UIOperation {

    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), null);
        Utils.findButtonSetOnclickListener(rootView,this);
        initView();
        initListener();
        initData();

        return rootView;
    }

    /**
     * 在屏幕中央显示Toast
     */
    public void showToast(String text) {
        Utils.showToast(getActivity(),text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getActivity().finish();
                animateOnExit();
                break;

            default:
                onClick(v, v.getId());
                break;
        }
    }

    /**使不用强转*/
    public <T> T findView(int id) {
        T view= (T) rootView.findViewById(id);
        return view;
    }

    /**
     * 界面退出时添加动画切换效果 必须在finish（）后调用
     */
    protected void animateOnExit() {
        getActivity().overridePendingTransition(R.anim.alpha_unchanged,
                R.anim.push_right_out);
    }
    /**
     * 界面进入时添加动画切换效果 必须在finish（）后调用
     */
    protected void animateOnEnter() {
        getActivity().overridePendingTransition(R.anim.push_right_in
                ,R.anim.alpha_unchanged);
    }

}

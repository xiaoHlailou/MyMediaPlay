package com.example.mymediaplayer.utils;

import android.util.Log;

/**
 * Created by hjz on 2016/10/2.
 */
public class LogUtils {
    public static boolean isShowLog=true;


    public static void i(Object objTag, String msg) {
        if (!isShowLog) {
            return;
        }
        String tag;

        //如果objTag是String，则直接使用
        //如果不是String，则使用它的类名
        if (objTag instanceof String) {
            tag= (String) objTag;
        } else if (objTag instanceof Class) {
            tag=((Class) objTag).getSimpleName();
        }else {
            tag=objTag.getClass().getSimpleName();
        }

        Log.i(tag, msg);
    }
}

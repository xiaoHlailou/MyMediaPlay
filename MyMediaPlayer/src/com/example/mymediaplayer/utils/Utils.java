package com.example.mymediaplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mymediaplayer.interfaces.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by hjz on 2016/10/1.
 */
public class Utils {

    /**
     * 查找Button和ImageButton并设置单击监听器   递归
     * */
    public static void findButtonSetOnclickListener(View view, View.OnClickListener listener){
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup= (ViewGroup) view;
            for (int i=0;i<viewGroup.getChildCount();i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof Button || child instanceof ImageButton) {
                    child.setOnClickListener(listener);
                } else if (child instanceof ViewGroup) {
                    findButtonSetOnclickListener(child,listener);
                }
            }
        }
    }

    /**在屏幕中央显示Toast*/
    public static void showToast(Context context,String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    /**获取屏幕宽度*/
    public static int getScreenWidth(Context context) {
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    /**获取屏幕高度*/
    public static int getScreenHeight(Context context) {
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getHeight();
    }

    /**
     * 打印cursor里面所有记录
     * @param cursor
     */
    public static void printCursor(Cursor cursor) {
        if (cursor == null) {
            LogUtils.i(Utils.class,"null");
            return;
        }

        LogUtils.i(Utils.class,"音频共有："+cursor.getCount()+"条记录");
        while (cursor.moveToNext()) {
            //遍历所有的列
            LogUtils.i(Utils.class,"-------------------");
            for (int i=0;i<cursor.getColumnCount();i++) {
                String columnName = cursor.getColumnName(i);
                String value = cursor.getString(i);
                LogUtils.i(Utils.class,columnName+":"+value);
            }

        }

    }

    /**
     * 格式化一个毫秒值,如果时间大等于1小时，则格式化为01:30:49,否则为30:49
     * @param duration
     * @return
     */
    public static CharSequence formatMillis(long duration) {

        //long转成日历格式
        Calendar calendar=Calendar.getInstance();
        calendar.clear();
        calendar.add(Calendar.MILLISECOND, (int) duration);

        //kk代表小时1~24
        String pattern=duration/ Constants.hourMillis>0?"kk:mm:ss":"mm:ss";
        return DateFormat.format(pattern, calendar);
    }
}

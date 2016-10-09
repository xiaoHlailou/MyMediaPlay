package com.example.mymediaplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.mymediaplayer.R;
import com.example.mymediaplayer.bean.VideoItem;
import com.example.mymediaplayer.utils.LogUtils;
import com.example.mymediaplayer.utils.Utils;

/**
 * Created by hjz on 2016/10/2.
 */
public class VideoListAdapter extends CursorAdapter {

    private View view;

    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    /**
     * 创建一个View
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //创建view
        //用viewHolder保存view里的控件
        //view.setTag
        view = View.inflate(context, R.layout.adapter_media_list, null);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
        viewHolder.tv_size = (TextView) view.findViewById(R.id.tv_size);
        viewHolder.tv_duration = (TextView) view.findViewById(R.id.tv_duration);

        view.setTag(viewHolder);

        return view;
    }

    /**
     * 把数据绑定到View上进行显示
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //取出ViewHolder
        //把数据显示到ViewHolder中的控件
        ViewHolder holder= (ViewHolder) view.getTag();

        VideoItem item = VideoItem.fromCursor(cursor);
        if (TextUtils.isEmpty(item.getTitle())){
            LogUtils.i("VideoListAdapter","cursor为null");
            return;
        }
        holder.tv_title.setText(item.getTitle());
        holder.tv_duration.setText(Utils.formatMillis(item.getDuration()));
        holder.tv_size.setText(Formatter.formatFileSize(context,item.getSize()));

    }

    class ViewHolder {
        TextView tv_title;
        TextView tv_size;
        TextView tv_duration;
    }
}

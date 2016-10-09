package com.example.mymediaplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.mymediaplayer.R;
import com.example.mymediaplayer.bean.AudioItem;

/**
 * Created by hjz on 2016/10/5.
 */
public class AudioListAdapter extends CursorAdapter {
    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.adapter_audio_list, null);

        ViewHolder viewHolder=new ViewHolder();
        viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
        viewHolder.tv_artist = (TextView) view.findViewById(R.id.tv_artist);

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        AudioItem item = AudioItem.fromCursor(cursor);
        ViewHolder viewHolder= (ViewHolder) view.getTag();
        viewHolder.tv_title.setText(item.getTitle());
        viewHolder.tv_artist.setText(item.getArtist());
    }

    class ViewHolder{
        TextView tv_title;
        TextView tv_artist;
    }
}

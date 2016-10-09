package com.example.mymediaplayer.fragment;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mymediaplayer.R;
import com.example.mymediaplayer.activity.AudioPlayerActivity;
import com.example.mymediaplayer.adapter.AudioListAdapter;
import com.example.mymediaplayer.bean.AudioItem;
import com.example.mymediaplayer.interfaces.Keys;
import com.example.mymediaplayer.utils.Utils;

import java.util.ArrayList;

/**
 * Created by hjz on 2016/10/1.
 */
public class AudioListFragment extends BaseFragment {

    private ListView mListView;

    @Override
    public void initData() {
//        getActivity().getContentResolver().query();
        AsyncQueryHandler queryHandler=new AsyncQueryHandler(getActivity().getContentResolver()) {
            @Override   //主线程更新
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                Utils.printCursor(cursor);

                AudioListAdapter adapter = new AudioListAdapter(getActivity(),cursor);
                mListView.setAdapter(adapter);
            }
        };

        int token = 0;
        Object cookie=null;
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection={
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };
        String selection=null;
        String[] selectionArgs=null;
        String orderBy = MediaStore.Audio.Media.TITLE+" ASC";
        queryHandler.startQuery(token,cookie,uri,projection,selection,selectionArgs,orderBy);
    }

    @Override
    public void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override  //由于是CursorAdapter  parent的数据是cursor
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                ArrayList<AudioItem> audioItems = getAudioList(cursor);

                enterAudioPlayerActivity(audioItems,position);
            }
        });
    }

    private void enterAudioPlayerActivity(ArrayList<AudioItem> audioItems, int position) {

        Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
        intent.putExtra(Keys.ITEM_LIST, audioItems);
        intent.putExtra(Keys.CURRENT_POSITION, position);
        startActivity(intent);

    }

    private ArrayList<AudioItem> getAudioList(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        ArrayList<AudioItem> list = new ArrayList<>();
        cursor.moveToFirst();
        do {
            AudioItem item = AudioItem.fromCursor(cursor);
            list.add(item);
        }while (cursor.moveToNext());

        return list;
    }

    @Override
    public void initView() {
        mListView = (ListView) rootView;
    }

    @Override
    public void onClick(View v, int id) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_media_list;
    }
}

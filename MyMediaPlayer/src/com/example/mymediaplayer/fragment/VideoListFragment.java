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
import com.example.mymediaplayer.activity.VideoPlayerActivity;
import com.example.mymediaplayer.activity.VitamioVideoPlayerActivity;
import com.example.mymediaplayer.adapter.VideoListAdapter;
import com.example.mymediaplayer.bean.VideoItem;
import com.example.mymediaplayer.interfaces.Keys;
import com.example.mymediaplayer.utils.Utils;

import java.util.ArrayList;

import io.vov.vitamio.Vitamio;

/**
 * Created by hjz on 2016/10/1.
 */
public class VideoListFragment extends BaseFragment {

    private ListView mListView;

    @Override
    public void initData() {
        //获取音频文件  通过ContentResolver

//        这种方式在主线程
//        getActivity().getContentResolver().query()
        //子线程
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {

            @Override //和handlerMessage一样  运行在主线程
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                //数据在cursor里
                Utils.printCursor(cursor);

                VideoListAdapter adapter = new VideoListAdapter(getActivity(),cursor);
                mListView.setAdapter(adapter);
            }
        };

        int token = 0;			// 相当于Message.what
        Object cookie = null;	// 相当于Message.obj
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {			// 指定要查询哪些列
                MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATA
        };
        String selection = null;		// 指定查询条件
        String[] selectionArgs = null;	// 指定查询条件中的参数
        String orderBy = MediaStore.Video.Media.TITLE + " ASC";
        // 这个方法会运行在子线程
        queryHandler.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
    }

    @Override
    public void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor= (Cursor) parent.getItemAtPosition(position);
                ArrayList<VideoItem> videoItems = getVideoList(cursor);
                enterVideoPlayerActivity(videoItems,position);
            }
        });
    }

    /**
     * 进入视频播放界面
     * @param videoItems 视频数据集合
     * @param position   点击视频位置
     */
    private void enterVideoPlayerActivity(ArrayList<VideoItem> videoItems, int position) {
//        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        Intent intent = new Intent(getActivity(), VitamioVideoPlayerActivity.class);
        intent.putExtra(Keys.ITEM_LIST,videoItems);
        intent.putExtra(Keys.CURRENT_POSITION, position);
        startActivity(intent);
    }

    /**
     * 把Cursor里面的所有记录读取出来 到集合当中
     * @param cursor
     * @return
     */
    private ArrayList<VideoItem> getVideoList(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        ArrayList<VideoItem> videoItems = new ArrayList<>();
        //看CursorAdapter源码  cursor已经移动到position位置了 而我们要整个列表的数据
        cursor.moveToFirst();
        do {
            videoItems.add(VideoItem.fromCursor(cursor));
        } while (cursor.moveToNext());

        return videoItems;
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

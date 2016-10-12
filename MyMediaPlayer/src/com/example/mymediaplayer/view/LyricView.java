package com.example.mymediaplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.mymediaplayer.R;
import com.example.mymediaplayer.bean.LyricItem;
import com.example.mymediaplayer.utils.LogUtils;
import com.example.mymediaplayer.utils.LyricLoader;

import java.util.ArrayList;

/**
 * Created by hjz on 2016/10/10.
 */
public class LyricView extends View {

    /**
     * 默认歌词颜色
     */
    private int defaultColor = Color.WHITE;
    /**
     * 高亮歌词颜色
     */
    private int hignLightColor = Color.GREEN;

    private int heightlightIndex = 0;
    /**
     * 高亮行的索引
     */

    private float defaultSize = getResources().getDimension(R.dimen.default_lyric_size);

    private float hignLightSize = getResources().getDimension(R.dimen.hignlight_lyric_size);
    private ArrayList<LyricItem> lyricItems;
    private Paint paint;
    /**
     * 高亮行的y坐标
     */
    private float hightlightY;
    /**
     * default行高
     */
    private float rowHeight;

    /**
     * 当前播放位置（时间）  由setCurrentPosition传来
     */
    private int currentPosition;

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化操作
     */
    private void init() {
        paint = new Paint();
        paint.setColor(defaultColor);
        paint.setTextSize(defaultSize);
        paint.setAntiAlias(true);//抗锯齿

        rowHeight = getTextHeight("哈哈哈") + 10;

        //写死数据
//   // TODO 模拟歌词数据，后面记得删除
//        heightlightIndex = 4;
//        lyricItems = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            lyricItems.add(new LyricItem(i * 2000, "我是" + (i + 1) + "个白痴"));
//        }




    }

    /**
     * 动态画歌词 根据heightlightIndex来确定高亮行
     * canvas   相当于现实生活中的纸
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (lyricItems == null || lyricItems.isEmpty()) {
            drawCenterText(canvas,"正在查找歌词。。。");
            return;
        }

        LyricItem currentLyricItem = lyricItems.get(heightlightIndex);

        if (heightlightIndex != lyricItems.size() - 1) {
            //如果不是最后一行 才移动
            translationCanvas(canvas, currentLyricItem);
        }

        drawLyrics(canvas, currentLyricItem);
    }

    /**
     * 画歌词
     * @param canvas
     * @param currentLyricItem
     */
    private void drawLyrics(Canvas canvas, LyricItem currentLyricItem) {
        String text = currentLyricItem.getText();
        //画高亮行的歌词
        drawCenterText(canvas, text);

        //画高亮行上面的歌词
        for (int i = 0; i < heightlightIndex; i++) {
            //y=高亮行的y-行差距*行高
            float y = hightlightY - (heightlightIndex - i) * rowHeight;
            drawHorizotalText(canvas, lyricItems.get(i).getText(), y, false);
        }

        //画高亮行下面的歌词
        for (int i = heightlightIndex + 1; i < lyricItems.size(); i++) {
            //y=高亮行的y+行差距*行高
            float y = hightlightY + (i - heightlightIndex) * rowHeight;
            drawHorizotalText(canvas, lyricItems.get(i).getText(), y, false);
        }
    }

    /**
     * 移动画布  实现歌词滚动效果
     * @param canvas
     * @param currentLyricItem
     */
    private void translationCanvas(Canvas canvas, LyricItem currentLyricItem) {
//        translationY=比例关系（高亮行歌词已显示时间/高亮行歌词总显示时间）* 行高
//            a、高亮行歌词已显示时间=当前播放时间-高亮歌词的开始显示时间;
//            b、高亮行歌词总显示时间=下一个高亮行开始时间-高亮行开始时间;
        long showedTime=currentPosition-currentLyricItem.getStartShowTime();
        long totalShowTime=lyricItems.get(heightlightIndex+1).getStartShowTime()
                -currentLyricItem.getStartShowTime();

        float scale=((float) showedTime)/totalShowTime;
        float translationY=scale*rowHeight;
        //移动画布，因为要往上移动，所以加了负号
        canvas.translate(0,-translationY);
    }


    /**
     * 画水平和垂直方向都居中的文本
     *
     * @param canvas
     * @param text
     */
    private void drawCenterText(Canvas canvas, String text) {
        int textHeight = getTextHeight(text);
        //y=歌词View高/2 + 歌词文本高/2;
        hightlightY = getHeight() / 2 + textHeight / 2;
        drawHorizotalText(canvas, text, hightlightY, true);
    }

    /**
     * 画水平居中的文本
     *
     * @param canvas
     * @param text
     * @param y
     * @param isHighlight
     */
    private void drawHorizotalText(Canvas canvas, String text, float y, boolean isHighlight) {
        paint.setColor(isHighlight ? hignLightColor : defaultColor);
        paint.setTextSize(isHighlight ? hignLightSize : defaultSize);

        int textWidth = getTextWidth(text);
        //x=歌词View宽/2-歌词文本宽/2;
        float x = getWidth() / 2 - textWidth / 2;

        canvas.drawText(text, x, y, paint);
    }

    /**
     * 获取文本的高
     *
     * @param text
     * @return
     */
    private int getTextHeight(String text) {
        Rect bounds = new Rect();//这个对象用于保存文本的上、下、左、右的位置
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }

    /**
     * 获取文本的宽
     *
     * @param text
     * @return
     */
    private int getTextWidth(String text) {
        Rect bounds = new Rect();//这个对象用于保存文本的上、下、左、右的位置
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    /**
     * 设置音频当前播放的位置,计算歌词高亮位置  activity每300毫秒调用一次
     *
     * @param currentPosition 当前位置（时间）
     */
    public void setCurrentPosition(int currentPosition) {
        if (lyricItems == null || lyricItems.isEmpty()) {
            return;
        }

        this.currentPosition=currentPosition;

        //1.计算高亮行的索引
        for (int i = 0; i < lyricItems.size(); i++) {
//            if (当前播放时间>=歌词开始显示时间){
//                if (当前已是最后一行) {
//                    当前行就是高亮行
//                } else if (当前播放时间<下一行歌词的开始显示时间){
//                    当前行就是高亮行
//                }
//            }
            LyricItem lyricItem = lyricItems.get(i);
            if (currentPosition >= lyricItem.getStartShowTime()) {
                if (i == lyricItems.size() - 1) {
                    heightlightIndex = i;
                    break;
                } else if (currentPosition < lyricItems.get(i + 1).getStartShowTime()) {
                    heightlightIndex = i;
                    break;
                }
            }
        }

        //2.重新画歌词，调用onDraw  用
        invalidate();

    }

    /**
     * 设置音乐文件路径，这个方法会把这个路径中的歌词文件读取解析为 LyricItem的集合
     * @param path
     */
    public void setMusicPath(String path) {
        heightlightIndex=0;//播放一首新歌 在activity.updateUI调用 把高亮词索引重置

        //读取音乐路径下对应的歌词文件，并解析成歌词info，并且显示出来
        lyricItems = LyricLoader.loadLyric(path);
    }
}

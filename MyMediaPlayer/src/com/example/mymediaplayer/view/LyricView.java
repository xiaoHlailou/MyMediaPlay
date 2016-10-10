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

        // TODO 模拟歌词数据，后面记得删除
        heightlightIndex = 4;
        lyricItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lyricItems.add(new LyricItem(i * 2000, "我是" + (i + 1) + "个白痴"));
        }
    }

    /**
     * canvas   相当于现实生活中的纸
     */
    @Override
    protected void onDraw(Canvas canvas) {

        LyricItem currentLyricItem = lyricItems.get(heightlightIndex);
        String text = currentLyricItem.getText();

        //画高亮行的歌词
        drawCenterText(canvas, text);

        //画高亮行上面的歌词
        for (int i = 0; i < heightlightIndex; i++) {
            //y=高亮行的y-行差距*行高
            float y = hightlightY - (heightlightIndex - i) * rowHeight;
            drawHorizotalText(canvas, lyricItems.get(i).getText(), y,false);
        }

        //画高亮行下面的歌词
        for (int i = heightlightIndex + 1; i < lyricItems.size(); i++) {
            //y=高亮行的y+行差距*行高
            float y = hightlightY + (i - heightlightIndex) * rowHeight;
            drawHorizotalText(canvas, lyricItems.get(i).getText(), y,false);
        }
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
        drawHorizotalText(canvas, text, hightlightY,true);
    }

    /**
     * 画水平居中的文本
     *
     * @param canvas
     * @param text
     * @param y
     * @param isHighlight
     */
    private void drawHorizotalText(Canvas canvas, String text, float y,boolean isHighlight) {
        paint.setColor(isHighlight?hignLightColor:defaultColor);
        paint.setTextSize(isHighlight?hignLightSize:defaultSize);

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
}

package com.stream.prettylive.streaming.internal.sdk.components.effect;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.stream.prettylive.R;

public class SeekBarWithNumber extends AppCompatSeekBar {

    private int mTitleTextColor;
    private float mTitleTextSize;
    private String mTitleText;

    private int img;
    private Bitmap map;

    private float img_width, img_height;
    Paint paint;

    private float numTextWidth;

    private Rect rect_seek;
    private Paint.FontMetrics fm;
    private float textCenterX;
    private float textBaselineY;

    private int offsetValue;

    public SeekBarWithNumber(Context context) {
        this(context, null);
    }

    public SeekBarWithNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarWithNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme()
            .obtainStyledAttributes(attrs, R.styleable.SeekBarWithNumber, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.SeekBarWithNumber_textSize) {
                mTitleTextSize = array.getDimension(attr, 15f);
            } else if (attr == R.styleable.SeekBarWithNumber_textColor) {
                mTitleTextColor = array.getColor(attr, Color.WHITE);
            } else if (attr == R.styleable.SeekBarWithNumber_textBackground) {
                img = array.getResourceId(attr, R.drawable.beauty_icon_indicator);
            }
        }
        array.recycle();
        getImgWH();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(mTitleTextSize);
        paint.setColor(mTitleTextColor);
        paint.setTypeface(Typeface.DEFAULT);

        int halfBgWidth = (int) Math.ceil(img_width) / 2;
        int halfBgHeight = (int) Math.ceil(img_height) + 5;
        setPadding(halfBgWidth, halfBgHeight, halfBgWidth, 0);
    }

    private void getImgWH() {
        map = BitmapFactory.decodeResource(getResources(), img);
        img_width = map.getWidth();
        img_height = map.getHeight();
    }

    private Bitmap rotateBitmap(Bitmap origin, Float alpha) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);

        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM == origin) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getTextLocation();
        rect_seek = this.getProgressDrawable().getBounds();

        float bm_x = rect_seek.width() * (getProgress() - offsetValue) / getMax();

        float bm_y = 0.0f;

        double text_x = bm_x + (img_width - numTextWidth) / 2;
        double text_y = textBaselineY + bm_y + (0.16 * img_height / 2);

        canvas.drawBitmap(map, bm_x, bm_y, paint);
        canvas.drawText(mTitleText, (float) text_x, (float) text_y, paint);
    }


    private void getTextLocation() {
        fm = paint.getFontMetrics();
        mTitleText = getProgress() + "";
        numTextWidth = paint.measureText(mTitleText);

        textBaselineY = (img_height - 8) / 2;
    }

    @Override
    public void setProgress(int progress) {
        super.setProgress(progress - offsetValue);
        invalidate();
    }

    @Override
    public int getProgress() {
        return super.getProgress() + offsetValue;
    }

    public void setOffsetValue(int offset) {
        offsetValue = offset;
        postInvalidate();
    }

    public int getOffsetValue() {
        return offsetValue;
    }

    @Override
    public synchronized void setMax(int max) {
        super.setMax(max - offsetValue);
    }
}

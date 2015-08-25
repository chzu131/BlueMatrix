package com.BlueMatrix.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.BlueMatrix.Activity.R;

/**
 * Created by ouyonglun on 2015/7/24.
 */
public class CustomPreview extends View {

    /** Led状态 */
    private Drawable mCheckOn;
    private Drawable mCheckOff;

    /** Led阵列 */
    private int COL = 12;
    private int ROW = 12;
    private boolean[][] mCustomPattern = new boolean[ROW][COL];
    private boolean[][] mCustomData = null;
    private Rect[][] mBounds = new Rect[ROW][COL];

    public CustomPreview(Context context) {
        super(context);
        init();
    }

    public CustomPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCheckOn = getResources().getDrawable(R.drawable.checkbox_on);
        mCheckOff = getResources().getDrawable(R.drawable.checkbox_off);

        resetData();
        initBounds();
    }

    private void initBounds() {
        if (COL <= 0 || ROW <= 0) {
            return;
        }
        int trolW = getWidth() - getPaddingLeft() - getPaddingRight();
        int trolH = getHeight() - getPaddingTop() - getPaddingBottom();

        int trolGalW = (int) (trolW * 0.1f);
        int trolGalH = (int) (trolH * 0.1f);

        int galW = (int) ((float) trolGalW / (COL - 1));
        int galH = (int) ((float) trolGalH / (ROW - 1));
        int cellW = (int) ((float) (trolW - trolGalW) / COL);
        int cellH = (int) ((float) (trolH - trolGalH) / ROW);

        for (int i = 0; i < COL; i++) {
            for (int j = 0; j < ROW; j++) {
                int left = (cellW + galW) * i + getPaddingLeft();
                int top = (cellH + galH) * j+ getPaddingTop();
                int right = left + cellW;
                int bottom = top + cellH;
                Rect rect = new Rect(left, top, right, bottom);
                mBounds[j][i] = rect;
            }
        }
    }

    public void resetData() {
        for (int i = 0; i < COL; i++) {
            for (int j = 0; j < ROW; j++) {
                mCustomPattern[j][i] = false;
            }
        }
        invalidate();
    }

    public void setCustomData(boolean[][] data) {
        removeCallbacks(mAnimationRunnable);
        mCustomData = data;
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (data != null && data.length > j && data[j] != null && data[j].length > i) {
                    mCustomPattern[j][i] = data[j][i];
                } else {
                    mCustomPattern[j][i] = false;
                }
            }
        }
        invalidate();
        startAnimation(0);
    }

    private boolean mBreakAnimation = false;
    private void startAnimation(final int startPixel) {
        if (mCustomData == null) {
            return;
        }
        mAnimationRunnable.setStartPixel(startPixel);
        postDelayed(mAnimationRunnable, 200);

    }

    private AnimtiaonRunnable mAnimationRunnable = new AnimtiaonRunnable();

    class AnimtiaonRunnable implements Runnable {
        private int mStartPixel;
        @Override
        public void run() {
            if (mCustomData != null && mCustomData[0] != null && mCustomData[0].length <= COL + mStartPixel) {
                return;
            }
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COL; j++) {
                    if (mCustomData != null && mCustomData.length > j && mCustomData[j] != null && mCustomData[j].length > i + mStartPixel) {
                        mCustomPattern[j][i] = mCustomData[j][i + mStartPixel];
                    } else {
                        mCustomPattern[j][i] = false;
                    }
                }
            }
            invalidate();
            startAnimation(mStartPixel + 1);
        }

        public void setStartPixel(int startPixel) {
            mStartPixel = startPixel;
        }
    };


    public byte[] getCustomData(boolean[][] data) {
        if (data != null && data.length > 0 && data[0] != null) {
            //获取文本数据
            int col = data[0].length;
            int row = data.length;

            int[] base = {8, 4, 2, 1};
            int colDataLenght;
            colDataLenght = col / 4;
            if ( col % 4 != 0 )
            {
                colDataLenght++;
            }
            if (colDataLenght % 2!=0 )
            {
                colDataLenght++;
            }
            int patternDataLenght = colDataLenght * row;
            byte[] customPattern = new byte[patternDataLenght];

            int index = 0;
            int baseData = 0;

            for (int j = 0; j < row; j++) {
                for (int i = 0; i < col; i++) {
                    if (index == i / 4 + j * colDataLenght) {
                        if (data[j][i]) {
                            baseData += base[i % 4];
                        }
                        customPattern[index] = (byte) baseData;
                    } else {
                        index = i / 4 + j * colDataLenght;
                        baseData = 0;
                        if (data[j][i]) {
                            baseData += base[i % 4];
                        }
                        customPattern[index] = (byte) baseData;
                    }
                }
            }
            return customPattern;
        } else {
            //获取显示板数据
            int[] base = {8, 4, 2, 1};
            int colDataLenght;
            colDataLenght = COL / 4;
            if ( COL % 4 != 0) {
                colDataLenght++;
            }
            int patternDataLenght = colDataLenght * ROW;
            byte[] customPattern = new byte[patternDataLenght];

            int index = 0;
            int baseData = 0;
            for (int j = 0; j < ROW; j++) {
                for (int i = 0; i < COL; i++) {
                    if (index == i / 4 + j * colDataLenght) {
                        if (mCustomPattern[j][i]) {
                            baseData += base[i % 4];
                        }
                        customPattern[index] = (byte) baseData;
                    } else {
                        index = i / 4 + j * colDataLenght;
                        baseData = 0;
                        if (mCustomPattern[j][i]) {
                            baseData += base[i % 4];
                        }
                        customPattern[index] = (byte) baseData;
                    }
                }
            }
            return customPattern;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw && h != oldh) {
            initBounds();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < COL; i++) {
            for (int j = 0; j < ROW; j++ ) {
                boolean pattern = mCustomPattern[j][i];
                if (!pattern) {
                    mCheckOff.setBounds(mBounds[j][i]);
                    mCheckOff.draw(canvas);
                } else {
                    mCheckOn.setBounds(mBounds[j][i]);
                    mCheckOn.draw(canvas);
                }
            }
       }
    }
}




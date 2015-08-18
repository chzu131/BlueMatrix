package com.BlueMatrix.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.BlueMatrix.Activity.R;

/**
 * Created by ouyonglun on 2015/7/24.
 */
public class CustomView extends View {

    /** ָʾ��״̬ */
    private Drawable mCheckOn;
    private Drawable mCheckOff;

    /** �Ƿ�ѡ�л��� */
    private boolean mIsDraw = true;

    /** LED������ */
    private int COL = 12;
    private int ROW = 12;
    private boolean[][] mCustomPattern = new boolean[ROW][COL];
    private Rect[][] mBounds = new Rect[ROW][COL];

    /** ���ڱ�ǻ����LED��  */
    private int[]  mIndex = {0, 0};

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    /**
     * ͨ��x,y��ȡ���ӵ�λ��
     * @param x
     * @param y
     * @param index
     */
    private void getIndex(int x, int y, int[] index) {
        for (int i = 0; i < COL; i++) {
            for (int j = 0; j < ROW; j++) {
                Rect rect = mBounds[j][i];
                boolean contains = rect.contains(x, y);
                if (contains) {
                    index[0] = i;
                    index[1] = j;
                    break;
                }
            }
        }
    }

    public void setIsDraw(boolean isDraw) {
        mIsDraw = isDraw;
    }


    public void resetData() {
        for (int i = 0; i < COL; i++) {
            for (int j = 0; j < ROW; j++) {
                mCustomPattern[j][i] = false;
            }
        }
        invalidate();
    }

    public byte[] getCustomData() {
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw && h != oldh) {
            initBounds();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                getIndex(x, y, mIndex);
                if (mIsDraw) {
                    mCustomPattern[mIndex[1]][mIndex[0]] = true;
                } else {
                    mCustomPattern[mIndex[1]][mIndex[0]] = false;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
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




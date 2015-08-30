package com.BlueMatrix.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.BlueMatrix.ble.BlueAction;
import com.BlueMatrix.view.CustomPreview;

public class CustomTextActivity extends Activity implements View.OnClickListener {

    private EditText mCustomText;
    private Button mPreviewButton;
    private Button mSendButton;
    private Button mBackButton;
    private CustomPreview mCustomPreview;

    Paint mPaint = new Paint();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_text_layout);

        mCustomText = (EditText) findViewById(R.id.custom_text);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/00starmap.TTF");
        mCustomText.setTypeface(typeface);
        mCustomText.setDrawingCacheEnabled(true);
        mPreviewButton = (Button) findViewById(R.id.preview_button);
        mPreviewButton.setOnClickListener(this);
        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(this);
        mBackButton = (Button) findViewById(R.id.back);
        mBackButton.setOnClickListener(this);
        mCustomPreview = (CustomPreview) findViewById(R.id.custom_preview);

        mPaint.setColor(0xff000000);
        mPaint.setTextSize(10);
        mPaint.setTypeface(typeface);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.preview_button :
                if(!CheckText())
                {
                    Toast.makeText(this, "不能超过5个字符", Toast.LENGTH_LONG).show();
                    break;
                }
                //预览LED效果
                mCustomText.setCursorVisible(false);
                mCustomText.destroyDrawingCache();
                mCustomText.buildDrawingCache();

                mCustomText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean[][] drawCaches = getTextData();
                        mCustomPreview.setCustomData(drawCaches);
                        mCustomText.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCustomText.setCursorVisible(true);
                            }
                        }, 50);
                    }
                }, 20);
                break;
            case R.id.send_button :
                if(!CheckText())
                {
                    Toast.makeText(this, "不能超过5个字符", Toast.LENGTH_LONG).show();
                    break;
                }
                //发送数据到蓝牙设备
                byte[] customData = mCustomPreview.getCustomData(getTextData());
                Toast.makeText(this, R.string.sending_data, Toast.LENGTH_LONG).show();
                BlueAction blueAction= new BlueAction();
                blueAction.SendTextPattern(customData,getTextLengh());
                break;
            case R.id.back :
                //发送数据到蓝牙设备
                Intent intent = new Intent();
                intent.setClass(this, MainMenuActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    //检查文本框文字是否合法
    public boolean CheckText()
    {
        if(mCustomText.length() > 5)
        {
            return false;
        }
        return true;
    }

    public int getTextLengh()
    {
       return mCustomText.length();
    }

    private boolean[][] getTextData() {
        Bitmap drawingCache = mCustomText.getDrawingCache();
        String cacheText = mCustomText.getText().toString();
        int textWidth = (int) mPaint.measureText(cacheText);
        if (textWidth <= 0) {
            return null;
        }
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        int cacheH = 12;
        int cacheW = (int) (textWidth);

        Bitmap bitmap = Bitmap.createBitmap(cacheW, cacheH, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);
        canvas.drawColor(0xffffffff);
        canvas.drawText(cacheText, 0, 9, mPaint);

        int cacheWidth = bitmap.getWidth();
        int cacheHeight = bitmap.getHeight();
        boolean[][] drawCaches = new boolean[cacheHeight][cacheWidth];
        for (int j = 0; j < cacheHeight; j++) {
            for (int i = 0; i < cacheWidth; i++) {
                int pixel = bitmap.getPixel(i, j);
                if (pixel == 0xffffffff) {
                    drawCaches[j][i] = false;
                } else {
                    drawCaches[j][i] = true;
                }
            }
        }
        return drawCaches;
    }
}

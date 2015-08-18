package com.BlueMatrix.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.BlueMatrix.ble.BlueAction;
import com.BlueMatrix.view.CustomView;

public class NewCustomActivity extends Activity implements View.OnClickListener, RadioButton.OnCheckedChangeListener {

    private RadioButton mPaintBox;
    private RadioButton mShuaBox;
    private Button mSendBotton;
    private Button mResetBotton;
    private Button mBackBotton;
    private CustomView mCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_layout);

        mPaintBox = (RadioButton) findViewById(R.id.paint);
        mPaintBox.setOnCheckedChangeListener(this);

        mShuaBox = (RadioButton) findViewById(R.id.shua);
        mShuaBox.setOnCheckedChangeListener(this);

        mSendBotton = (Button) findViewById(R.id.send);
        mSendBotton.setOnClickListener(this);

        mResetBotton = (Button) findViewById(R.id.reset);
        mResetBotton.setOnClickListener(this);

        mBackBotton = (Button) findViewById(R.id.back);
        mBackBotton.setOnClickListener(this);

        mCustomView = (CustomView) findViewById(R.id.custom_view);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mPaintBox && isChecked) {
            mCustomView.setIsDraw(true);
            mShuaBox.setChecked(false);
        } else if (buttonView == mShuaBox && isChecked) {
            mCustomView.setIsDraw(false);
            mPaintBox.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.send) {
            byte[] customData = mCustomView.getCustomData();
            Toast.makeText(this, "系统处理中...", Toast.LENGTH_LONG).show();
            BlueAction blueAction= new BlueAction();
            blueAction.SendCustomPattern(customData);
        } else if (id == R.id.reset) {
            mCustomView.resetData();
        } else if (id == R.id.back) {
            Intent intent = new Intent();
            intent.setClass(this, MainMenuActivity.class);
            startActivity(intent);
        }
    }
}

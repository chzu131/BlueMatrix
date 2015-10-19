package com.BlueMatrix.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.BlueMatrix.ble.BlueAction;
import com.BlueMatrix.ble.RBLService;
import com.BlueMatrix.tools.Memory;
import com.BlueMatrix.tools.MyDialog;
import com.BlueMatrix.view.CustomView;

public class NewCustomActivity extends Activity implements View.OnClickListener, RadioButton.OnCheckedChangeListener {

    private ToggleButton mPaintBox;
    private Button mSendBotton;
    private Button mResetBotton;
    private Button mBackBotton;
    private CustomView mCustomView;
    private Button mDisconnetButton;

    MyDialog mMyDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_layout);

        mPaintBox = (ToggleButton) findViewById(R.id.paint);
        mPaintBox.setOnCheckedChangeListener(this);

        mSendBotton = (Button) findViewById(R.id.send);
        mSendBotton.setOnClickListener(this);

        mResetBotton = (Button) findViewById(R.id.reset);
        mResetBotton.setOnClickListener(this);

        mBackBotton = (Button) findViewById(R.id.back);
        mBackBotton.setOnClickListener(this);

        mDisconnetButton = (Button) findViewById(R.id.disconnet_button);
        mDisconnetButton.setOnClickListener(this);

        mCustomView = (CustomView) findViewById(R.id.custom_view);


        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        mMyDialog = new MyDialog(this, "Please wait while loading...");
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(RBLService.ACTION_DATA_WRITE_SUCCESS);
        intentFilter.addAction(RBLService.ACTION_DATA_WRITE_FAILURE);


        return intentFilter;
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mPaintBox) {
            if (isChecked) {
                mCustomView.setIsDraw(true);
            } else {
                mCustomView.setIsDraw(false);
            }
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                //连接断开，返回
                Intent intent2 = new Intent(NewCustomActivity.this, ScanDeviceActivity.class);
                startActivity(intent2);
                finish();
            }
            else if (RBLService.ACTION_DATA_WRITE_SUCCESS.equals(action))
            {
                //Toast.makeText(getApplicationContext(), "finish", Toast.LENGTH_SHORT).show();
                mMyDialog.hideDialog();
            }
            else if (RBLService.ACTION_DATA_WRITE_FAILURE.equals(action))
            {
                //Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
                mMyDialog.hideDialog();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //初始化蓝牙操作类
        BlueAction blueAction= new BlueAction();
        if(!blueAction.IsConnectBT())
        {
            //连接断开，返回
            Intent intent2 = new Intent(NewCustomActivity.this, ScanDeviceActivity.class);
            startActivity(intent2);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mGattUpdateReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.send) {
            byte[] customData = mCustomView.getCustomData();
            //Toast.makeText(this, "System processing...", Toast.LENGTH_SHORT).show();
            BlueAction blueAction= new BlueAction();
            blueAction.SendCustomPattern(customData);

            mMyDialog.showDialog();

        } else if(id == R.id.disconnet_button){
            BlueAction blueAction= new BlueAction();
            blueAction.DisconnectBT();
            Memory memory = new Memory(this);
            memory.ClearLastMacAddress();

            Intent intent = new Intent(getApplicationContext(), ScanDeviceActivity.class);
            startActivity(intent);
        }else if (id == R.id.reset) {
            mCustomView.resetData();
        } else if (id == R.id.back) {
            Intent intent = new Intent();
            intent.setClass(this, MainMenuActivity.class);
            startActivity(intent);
        }
    }
}

package com.BlueMatrix.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.BlueMatrix.ble.BlueAction;
import com.BlueMatrix.ble.RBLService;
import com.BlueMatrix.sound.Memory;
import com.BlueMatrix.sound.Sound;

public class MainMenuActivity extends Activity implements View.OnClickListener {
    private final static String TAG = MainMenuActivity.class.getSimpleName();

    private View menuCenter;
    private View menuLeft;
    private View menuRight;
    private View menuDown;
    private View menuUp;
    private View mDisconnetButton;

    private String mDeviceName;
    private String mDeviceAddress;
    private RBLService mBluetoothLeService;
    private static BlueAction blueAction;  //提供蓝牙操作
    Memory memory;
    //private Sound sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        menuCenter = findViewById(R.id.menu_cneter);
        menuCenter.setOnClickListener(this);

        menuLeft = findViewById(R.id.menu_left);
        menuLeft.setOnClickListener(this);


        menuRight = findViewById(R.id.menu_right);
        menuRight.setOnClickListener(this);

        menuDown = findViewById(R.id.menu_down);
        menuDown.setOnClickListener(this);

        menuUp = findViewById(R.id.menu_up);
        menuUp.setOnClickListener(this);

        mDisconnetButton = findViewById(R.id.disconnet_button);
        mDisconnetButton.setOnClickListener(this);

        menuLeft.setEnabled(false);
        menuUp.setEnabled(false);
        menuRight.setEnabled(false);
        menuCenter.setEnabled(false);
        menuDown.setEnabled(false);
        mDisconnetButton.setEnabled(false);

        initBlueServiec();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        //sound = new Sound();
        //sound.initSoundPool(this);
        memory = new Memory(this);
    }

    private void initBlueServiec() {
        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(DeviceActivity.EXTRA_DEVICE_ADDRESS);
        mDeviceName = intent.getStringExtra(DeviceActivity.EXTRA_DEVICE_NAME);
        getActionBar().setTitle(mDeviceName);

        Intent gattServiceIntent = new Intent(this, RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.disconnet_button:
            {
                BlueAction blueAction= new BlueAction();
                blueAction.DisconnectBT();
                Memory memory = new Memory(this);
                memory.ClearLastMacAddress();

                Intent intent = new Intent(this, ScanDeviceActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_cneter: {
                Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, CustomTextActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_left:
                Toast.makeText(this, "Turn left", Toast.LENGTH_LONG).show();
                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_LEFT);
                }
                break;
            case R.id.menu_right:
                Toast.makeText(this, "Turn right", Toast.LENGTH_LONG).show();

                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_RIGHT);
                }
                break;
            case R.id.menu_down: {
                Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, NewCustomActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.menu_up:
                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_UP);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mGattUpdateReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //断开连接
        mBluetoothLeService.disconnect();
        mBluetoothLeService.close();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                //sound.playSound();
                //连接断开，返回
                Intent intent2 = new Intent(MainMenuActivity.this, ScanDeviceActivity.class);
                startActivity(intent2);
                //finish();
            }
            else if (RBLService.ACTION_GATT_CONNECTED.equals(action))
            {
                menuLeft.setEnabled(true);
                menuUp.setEnabled(true);
                menuRight.setEnabled(true);
                menuCenter.setEnabled(true);
                menuDown.setEnabled(true);
                mDisconnetButton.setEnabled(true);
            }
            else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                getGattService(mBluetoothLeService.getSupportedGattService());
            }
            else if (RBLService.ACTION_DATA_AVAILABLE.equals(action))
            {
                // displayData(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
            }
        }
    };

    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null)
            return;

        blueAction.getGattService(gattService);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((RBLService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            if(!mBluetoothLeService.isConnected() && (mDeviceAddress != null) )
            {
                mBluetoothLeService.connect(mDeviceAddress);
                memory.SaveMacAddress(mDeviceAddress);
            }
            //初始化蓝牙操作类
            blueAction = new BlueAction(mBluetoothLeService);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


}

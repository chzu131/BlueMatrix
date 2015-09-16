package com.BlueMatrix.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.BlueMatrix.ble.BlueAction;
import com.BlueMatrix.ble.RBLService;
import com.BlueMatrix.tools.DirectionService;
import com.BlueMatrix.tools.Memory;

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
    private DirectionService mDirectionService;
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

        initBlueServiec();

        memory = new Memory(this);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);// 屏幕熄掉后依然运行
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mDirectionService.mReceiver, filter);
    }

    private void initBlueServiec() {
        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(DeviceActivity.EXTRA_DEVICE_ADDRESS);
        mDeviceName = intent.getStringExtra(DeviceActivity.EXTRA_DEVICE_NAME);
        getActionBar().setTitle(mDeviceName);

        Intent gattServiceIntent = new Intent(this, RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        Intent gattDirectionServiceIntent = new Intent(this, DirectionService.class);
       // startService(gattDirectionServiceIntent);
        bindService(gattDirectionServiceIntent, mDirectionServiceConnection, BIND_AUTO_CREATE);
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
                StopDirectionService();

                Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, CustomTextActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_left:
               // Toast.makeText(this, "Turn left", Toast.LENGTH_LONG).show();
                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_LEFT);
                }
                break;
            case R.id.menu_right:
                //Toast.makeText(this, "Turn right", Toast.LENGTH_LONG).show();

                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_RIGHT);
                }
                break;
            case R.id.menu_down: {
                StopDirectionService();

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
        registerReceiver(mServiceUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mServiceUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        intentFilter.addAction(DirectionService.ACTION_DIRCTION_LEFT);
        intentFilter.addAction(DirectionService.ACTION_DIRCTION_RIGHT);

        return intentFilter;
    }
    //设置按键状态
    private void SetButtonStauts(boolean flag)
    {
        menuLeft.setEnabled(flag);
        menuUp.setEnabled(flag);
        menuRight.setEnabled(flag);
        menuCenter.setEnabled(flag);
        menuDown.setEnabled(flag);
        mDisconnetButton.setEnabled(flag);
    }

    private final BroadcastReceiver mServiceUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //方向命令
            if (DirectionService.ACTION_DIRCTION_LEFT.equals(action)) {
                blueAction.PatternRegularCommand(BlueAction.PATTERN_LEFT);
            }
            else if (DirectionService.ACTION_DIRCTION_RIGHT.equals(action)) {
                blueAction.PatternRegularCommand(BlueAction.PATTERN_RIGHT);
            }

            //蓝牙命令
            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                StopDirectionService();

                //连接断开，返回
                Intent intent2 = new Intent(MainMenuActivity.this, ScanDeviceActivity.class);
                startActivity(intent2);
            }
            else if (RBLService.ACTION_GATT_CONNECTED.equals(action))
            {
                //设置按键状态
                SetButtonStauts(true);
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


    private final ServiceConnection mDirectionServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mDirectionService = ((DirectionService.LocalBinder) service).getService();
            mDirectionService.init(MainMenuActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            mDirectionService = null;
        }
    };

    private void StopDirectionService(){
        Intent gattDirectionServiceIntent = new Intent(this, DirectionService.class);
        unbindService(mDirectionServiceConnection);
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
                //设置按键状态
                SetButtonStauts(false);

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

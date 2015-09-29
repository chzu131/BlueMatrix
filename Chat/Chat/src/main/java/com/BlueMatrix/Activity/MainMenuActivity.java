package com.BlueMatrix.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Toast;
import android.widget.ToggleButton;

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
    ToggleButton mToggleButton;

    private View buttonSmile;
    private View buttonHeart;
    private View buttonSOS;
    private View buttonForidden;
    private View buttonStop;

    private String mDeviceName;
    private String mDeviceAddress;
    private RBLService mBluetoothLeService;
    private DirectionService mDirectionService;
    private static BlueAction blueAction;  //提供蓝牙操作
    Memory memory;
    private boolean mAutoModeStatus;

    private ProgressDialog mWaitDialog = null;
    //private Sound sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        menuCenter = findViewById(R.id.menu_center);
        menuCenter.setOnClickListener(this);

        menuLeft = findViewById(R.id.menu_left);
        menuLeft.setOnClickListener(this);

        menuRight = findViewById(R.id.menu_right);
        menuRight.setOnClickListener(this);

        menuDown = findViewById(R.id.menu_down);
        menuDown.setOnClickListener(this);

        menuUp = findViewById(R.id.menu_up);
        menuUp.setOnClickListener(this);

        buttonSmile = findViewById(R.id.smile_button);
        buttonSmile.setOnClickListener(this);

        buttonHeart = findViewById(R.id.heart_button);
        buttonHeart.setOnClickListener(this);

        buttonSOS = findViewById(R.id.sos_button);
        buttonSOS.setOnClickListener(this);

        buttonForidden = findViewById(R.id.foridden_button);
        buttonForidden.setOnClickListener(this);

        buttonStop = findViewById(R.id.stop_button);
        buttonStop.setOnClickListener(this);

        mDisconnetButton = findViewById(R.id.disconnet_button);
        mDisconnetButton.setOnClickListener(this);

        mToggleButton = (ToggleButton)findViewById(R.id.AutoModeToggle);
        mToggleButton.setOnClickListener(this);

        initBlueService();

        memory = new Memory(this);

        //如果智能控制的界面隐藏了，屏蔽智能控制的功能
        View layoutIntelligenceControl = findViewById(R.id.layoutIntelligenceControl);
        if(layoutIntelligenceControl.getVisibility() == View.GONE) {
            if(memory.getAutoModeStatus() == true) {
                memory.SaveAutoModeStatus(false);
            }
        }

        Intent gattDirectionServiceIntent = new Intent(this, DirectionService.class);

        mAutoModeStatus = memory.getAutoModeStatus();
        if(mAutoModeStatus) {
            mToggleButton.setChecked(true);
            bindService(gattDirectionServiceIntent, mDirectionServiceConnection, BIND_AUTO_CREATE);
        }
        else{
            mToggleButton.setChecked(false);
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);// 屏幕熄掉后依然运行
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mDirectionService.mReceiver, filter);

        initWaitDialog();
    }

    private void initBlueService() {
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
            case R.id.AutoModeToggle:{
                if(!mAutoModeStatus){
                    mAutoModeStatus = true;
                    memory.SaveAutoModeStatus(true);
                    mToggleButton.setChecked(true);
                    Intent gattDirectionServiceIntent = new Intent(this, DirectionService.class);
                    bindService(gattDirectionServiceIntent,
                            mDirectionServiceConnection, BIND_AUTO_CREATE);
                }else{
                    mAutoModeStatus = false;
                    memory.SaveAutoModeStatus(false);
                    mToggleButton.setChecked(false);
                    unbindService(mDirectionServiceConnection);
                }
                break;
            }

            case R.id.menu_center: {
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
                    showWaitDialog();
                }
                break;
            case R.id.menu_right:
                //Toast.makeText(this, "Turn right", Toast.LENGTH_LONG).show();

                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_RIGHT);
                    showWaitDialog();
                }
                break;
            case R.id.menu_down: {
                StopDirectionService();

                Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, NewCustomActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.smile_button:
                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_SMILE);
                    showWaitDialog();
                }
                break;
            case R.id.heart_button:
                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_HEART);
                    showWaitDialog();
                }
                break;
            case R.id.sos_button:
                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_SOS);
                    showWaitDialog();
                }
                break;
            case R.id.foridden_button:
                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_FORIDDEN);
                    showWaitDialog();
                }
                break;
            case R.id.stop_button:
                if(blueAction != null) {
                    blueAction.PatternRegularCommand(BlueAction.PATTERN_STOP);
                    showWaitDialog();
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
        intentFilter.addAction(RBLService.ACTION_DATA_WRITE_SUCCESS);
        intentFilter.addAction(RBLService.ACTION_DATA_WRITE_FAILURE);

        intentFilter.addAction(DirectionService.ACTION_DIRCTION_LEFT);
        intentFilter.addAction(DirectionService.ACTION_DIRCTION_RIGHT);

        return intentFilter;
    }

    private void initWaitDialog()
    {
        mWaitDialog = new ProgressDialog(this);
        mWaitDialog.setMessage("Please wait while loading...");
        mWaitDialog.setIndeterminate(true);
        mWaitDialog.setCancelable(false);
    }

    private void showWaitDialog()
    {
        if(mWaitDialog != null)
        {
            mWaitDialog.show();
        }
    }

    private void hideWaitDialog()
    {
        if(mWaitDialog != null)
        {
            mWaitDialog.hide();
        }
    }
    //设置按键状态
    private void SetButtonStatus(boolean flag)
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
            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)){
                StopDirectionService();
                //连接断开，返回
                Intent intent2 = new Intent(MainMenuActivity.this, ScanDeviceActivity.class);
                startActivity(intent2);
            }
            else if (RBLService.ACTION_GATT_CONNECTED.equals(action)){
                //设置按键状态
                SetButtonStatus(true);
                hideWaitDialog();
            }
            else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                getGattService(mBluetoothLeService.getSupportedGattService());
            }
            else if (RBLService.ACTION_DATA_WRITE_SUCCESS.equals(action)){
               // Toast.makeText(getApplicationContext(), "finish", Toast.LENGTH_SHORT).show();
                hideWaitDialog();
            }
            else if (RBLService.ACTION_DATA_WRITE_FAILURE.equals(action)){
                Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
                hideWaitDialog();
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
        if(mAutoModeStatus) {
            unbindService(mDirectionServiceConnection);
        }
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
                SetButtonStatus(false);
                showWaitDialog();

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

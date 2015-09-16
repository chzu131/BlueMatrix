package com.BlueMatrix.tools;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.BlueMatrix.ble.BlueAction;
import com.BlueMatrix.ble.RBLService;


/**
 * Created by chzu131 on 2015/9/14.
 */
public class DirectionService extends Service {
    private static final String TAG = "DirectionService";
    private static SensorManager mySensorManager;
    private static float Yaw = 0, Pitch = 0, Roll = 0;
    private static float LastYaw = 0;
    private static boolean fisrtReadYaw = true;
    private static float YAW_ANGLE = 30;
    private final IBinder mBinder = new LocalBinder();

    private static PowerManager.WakeLock mWakeLock;
    private static PowerManager mPowerManager;

    private RBLService mBluetoothLeService;
    private static BlueAction blueAction;

    private static boolean mScreenOn = true;


    public final static String ACTION_DIRCTION_LEFT = "ACTION_DIRCTION_LEFT";
    public final static String ACTION_DIRCTION_RIGHT = "ACTION_DIRCTION_RIGHT";

    static DirectionService This;


    public DirectionService()
    {
        This = this;
    }

    public class LocalBinder extends Binder {
        public DirectionService getService() {
            return DirectionService.this;
        }
    }

    public void init(Context context)
    {
        if(mySensorManager == null) {
            mySensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
            mySensorManager.registerListener(
                    //注册监听
                    mySensorListener,
                    //监听器SensorListener对象
                    SensorManager.SENSOR_ORIENTATION,
                    //传感器的类型为姿态
                    SensorManager.SENSOR_DELAY_UI
                    //频度
            );

            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);// CPU保存运行


            Intent gattServiceIntent = new Intent(this, RBLService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
    }

    public static BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                mScreenOn = false;
                if (mySensorManager != null) {
                    mWakeLock.acquire();
                    mySensorManager.unregisterListener(This.mySensorListener);
                    mySensorManager.registerListener(
                            This.mySensorListener,
                            SensorManager.SENSOR_ORIENTATION,
                            SensorManager.SENSOR_DELAY_UI);
                }
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                mScreenOn = false;
                if (mySensorManager != null) {
                    mWakeLock.release();
                }
            }

        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((RBLService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {

            }

            //初始化蓝牙操作类
            blueAction = new BlueAction(mBluetoothLeService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public float GetYaw()  {return Yaw;}
    public float GetPitch()  {return Pitch;}
    public float GetRoll()  {return Roll;}

    private  SensorListener mySensorListener = new SensorListener(){
        @Override
        public void onAccuracyChanged(int sensor, int accuracy) {}
        //重写onAccuracyChanged方法
        @Override
        public void onSensorChanged(int sensor, float[] values) {
            //重写onSensorChanged方法
            if(sensor == SensorManager.SENSOR_ORIENTATION) {
                //检查姿态的变化
                String intentAction;
                Yaw = values[0];
                Pitch = values[1];
                Roll = values[2];
                if (!fisrtReadYaw){
                    if(Math.abs(LastYaw - Yaw) >= YAW_ANGLE) {
                        System.out.println(Yaw);

                        if(LastYaw > Yaw) {

                            blueAction.PatternRegularCommand(BlueAction.PATTERN_LEFT);

                        }else{
                            blueAction.PatternRegularCommand(BlueAction.PATTERN_RIGHT);
                        }
                        LastYaw = Yaw;

                    }
                }
                else{
                    fisrtReadYaw = false;
                    LastYaw = Yaw;
                }

            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private  void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
}

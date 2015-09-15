package com.BlueMatrix.tools;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by chzu131 on 2015/9/14.
 */
public class DirectionService extends Service {
    private static SensorManager mySensorManager;
    private static float Yaw = 0, Pitch = 0, Roll = 0;
    private static float LastYaw = 0;
    private static boolean fisrtReadYaw = true;
    private static float YAW_ANGLE = 30;
    private final IBinder mBinder = new LocalBinder();

    public final static String ACTION_DIRCTION_LEFT = "ACTION_DIRCTION_LEFT";
    public final static String ACTION_DIRCTION_RIGHT = "ACTION_DIRCTION_RIGHT";

    public DirectionService()
    {}

    public class LocalBinder extends Binder {
        public DirectionService getService() {
            return DirectionService.this;
        }
    }

    public void init(Context context)
    {
        mySensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
        mySensorManager.registerListener(
        //注册监听
        mySensorListener,
        //监听器SensorListener对象
        SensorManager.SENSOR_ORIENTATION,
        //传感器的类型为姿态
        SensorManager.SENSOR_DELAY_UI
        //频度
        );
    }
    public float GetYaw()  {return Yaw;}
    public float GetPitch()  {return Pitch;}
    public float GetRoll()  {return Roll;}

    private SensorListener mySensorListener = new SensorListener(){
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
                            intentAction = ACTION_DIRCTION_LEFT;
                        }else{
                            intentAction = ACTION_DIRCTION_RIGHT;
                        }
                        LastYaw = Yaw;
                        broadcastUpdate(intentAction);
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

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }


}

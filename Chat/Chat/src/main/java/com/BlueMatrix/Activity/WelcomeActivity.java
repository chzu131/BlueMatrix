package com.BlueMatrix.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.BlueMatrix.tools.Memory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chzu131 on 2015/10/19.
 */
public class WelcomeActivity extends Activity {
    private Timer mTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
        @Override
        public void run() {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ScanDeviceActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
         }
        }, 800);
    }

}

package com.BlueMatrix.tools;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chzu131 on 2015/10/7.
 */
public class MyDialog {
    private ProgressDialog mWaitDialog = null;
    private Timer mTimer = null;

    private int WAIT_DIALOG_SHOW_TIME = 10000;

    private void initDialog(Context context, String text) {
        mWaitDialog = new ProgressDialog(context);
        mWaitDialog.setMessage(text);
        mWaitDialog.setIndeterminate(true);
        mWaitDialog.setCancelable(false);
    }

    public MyDialog(Context context, String text){
        initDialog(context, text);
    }

    public void showDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.show();

            //间隔时间后，检查对话框是否隐藏。没有隐藏，则隐藏。
            if(mTimer == null) {
                mTimer = new Timer();
            }

            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mWaitDialog.isShowing()) {
                        mWaitDialog.dismiss();
                    }
                }
            }, WAIT_DIALOG_SHOW_TIME);
        }
    }

    public void hideDialog() {
        if (mWaitDialog != null) {
            if (mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
                //停止检查"对话框是否已经隐藏"的定时器
                if(mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        }
    }
}

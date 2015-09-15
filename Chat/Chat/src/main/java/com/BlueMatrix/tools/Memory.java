package com.BlueMatrix.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by chzu131 on 2015/9/10.
 */
public class Memory {


    private static String mDeviceAddress = null;
    private static Context context = null;

    public Memory(Context context)
    {
        this.context = context;
    }

    public void SaveMacAddress(String DeviceAddress)
    {
        String code = DeviceAddress.trim();
        //步骤2-1：创建一个SharedPreferences.Editor接口对象，lock表示要写入的XML文件名，MODE_WORLD_WRITEABLE写操作
        SharedPreferences.Editor editor = context.getSharedPreferences("lock", context.MODE_WORLD_WRITEABLE).edit();
        editor.clear();
        //步骤2-2：将获取过来的值放入文件
        editor.putString("code", code);
        //步骤3：提交
        editor.commit();
    }

    public void ClearLastMacAddress()
    {
        //步骤2-1：创建一个SharedPreferences.Editor接口对象，lock表示要写入的XML文件名，MODE_WORLD_WRITEABLE写操作
        SharedPreferences.Editor editor = context.getSharedPreferences("lock", context.MODE_WORLD_WRITEABLE).edit();
        //步骤2-2：将获取过来的值放入文件
        editor.putString("code", "");
        //步骤3：提交
        editor.commit();
    }
    public String GetLastMacAddress()
    {
        //步骤1：创建一个SharedPreferences接口对象
        SharedPreferences read = context.getSharedPreferences("lock", context.MODE_WORLD_READABLE);
        //步骤2：获取文件中的值
        mDeviceAddress = read.getString("code", "");
        return mDeviceAddress;
    }

}

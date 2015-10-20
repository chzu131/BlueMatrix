package com.BlueMatrix.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by chzu131 on 2015/9/10.
 */
public class Memory {


    private static String mDeviceAddress = null;
    private static Context context = null;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences read ;
    private static String mXMLName = "Data";
    private static String DeviceAddressNode = "DeviceAddress";
    private static boolean autoModeNodeStatus = false;
    private static String AutoModeNode = "AutoModeNode";
    public static boolean firstLogin = true;    //保存是否第一次启动

    public Memory(Context context){
        this.context = context;
        editor = context.getSharedPreferences(mXMLName, context.MODE_WORLD_WRITEABLE).edit();
        read = context.getSharedPreferences(mXMLName, context.MODE_WORLD_READABLE);
    }

    public void SaveMacAddress(String DeviceAddress)
    {
        String code = DeviceAddress.trim();
        if(editor != null) {
            //editor.clear();
            //步骤2-2：将获取过来的值放入文件
            editor.putString(DeviceAddressNode, code);
            //步骤3：提交
            editor.commit();
        }
    }

    public void ClearLastMacAddress()
    {
        if(editor != null) {
            editor.putString(DeviceAddressNode, "");
            editor.commit();
        }
    }
    public String GetLastMacAddress()
    {
        if(read != null){
            mDeviceAddress = read.getString(DeviceAddressNode, "");
        }
        return mDeviceAddress;
    }

    public boolean getAutoModeStatus()
    {
        if(read != null){
            autoModeNodeStatus = read.getBoolean(AutoModeNode, false);
        }
        return autoModeNodeStatus;
    }

    public void SaveAutoModeStatus(boolean bAutoModeStatus)
    {
        if(editor != null) {
            //editor.clear();
            //步骤2-2：将获取过来的值放入文件
            editor.putBoolean(AutoModeNode, bAutoModeStatus);
            //步骤3：提交
            editor.commit();
        }
    }

}

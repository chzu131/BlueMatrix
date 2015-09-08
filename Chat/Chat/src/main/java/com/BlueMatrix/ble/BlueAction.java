package com.BlueMatrix.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * Created by chzu131 on 2015/5/28.
 */
public class BlueAction {
    private static Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();
    private static RBLService mBluetoothLeService;
    public  static int PATTERN_LEFT = 0;
    public  static int PATTERN_UP = 1;
    public  static int  PATTERN_RIGHT= 2;

    public BlueAction()
    {

    }
    public BlueAction(RBLService service)
    {
        mBluetoothLeService = service;
    }
    //传送自定义图案
    public void SendCustomPattern(byte data[])
    {
        if(data.length > 64)
        {
            return;
        }
        byte CustomPattern[] = new byte[24+1];//+1是加上字符长度
        BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_CUSTOMECOMMAND);
        if(characteristic != null)
        {
            CustomPattern[0] = (byte)(24);
            for (int i = 0; i < data.length/3; i++)
            {
                CustomPattern[i*2+1] = (byte)(data[i*3]<<4);
                CustomPattern[i*2+1] |= (byte)data[i*3+1];

                CustomPattern[i*2+2] = (byte)(data[i*3+2]<<4);
                CustomPattern[i*2+2] |= (byte)(0x0);
            }

            characteristic.setValue(CustomPattern);
            mBluetoothLeService.writeCharacteristic(characteristic);
        }
    }

    //传送文字图案
    public void SendTextPattern(byte data[], int TextLengh)
    {
        if(data.length > 144)
        {
            return;
        }

        byte CustomTextPattern[] = new byte[data.length/2 + data.length%2 + 1 ];//+1是加上字符长度
        BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TEXTCOMMAND);
        if(characteristic != null)
        {
            CustomTextPattern[0] = (byte)(data.length/2 + data.length%2);
            for (int i = 0; i < data.length-1; i+=2)
            {
                CustomTextPattern[i/2+1] = (byte)(data[i]<<4);
                CustomTextPattern[i/2+1] |= (byte)data[i+1];
            }
            characteristic.setValue(CustomTextPattern);
            mBluetoothLeService.writeCharacteristic(characteristic);
        }
    }
    public void getGattService(BluetoothGattService gattService)
    {
        BluetoothGattCharacteristic characteristic;
        characteristic= gattService.getCharacteristic(RBLService.UUID_BLE_SHIELD_CUSTOMECOMMAND);
        map.put(characteristic.getUuid(), characteristic);

        characteristic = gattService.getCharacteristic(RBLService.UUID_BLE_SHIELD_REGULARCOMMAND);
        map.put(characteristic.getUuid(), characteristic);

        characteristic = gattService.getCharacteristic(RBLService.UUID_BLE_SHIELD_TEXTCOMMAND);
        map.put(characteristic.getUuid(), characteristic);
    }

    //传送规则图案
    public void PatternRegularCommand(int pattern)
    {
        byte RegularPattern[]={0};
        RegularPattern[0]= (byte)pattern;
        BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_REGULARCOMMAND);
        if(characteristic != null)
        {
            characteristic.setValue(RegularPattern);
            mBluetoothLeService.writeCharacteristic(characteristic);
        }
    }

//    public static byte[] hexStringToByteArray(String s)
//    {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                    + Character.digit(s.charAt(i+1), 16));
//        }
//        return data;
//    }
//
//    //十进制转为十六进制
//    private byte  ByteConvertToHex(byte b)
//    {
//        byte ret = b;
//        if(b >= '0' && b <= '9')
//        {
//            ret = (byte)(b - '0');
//
//        }
//        if(b >= 'a' && b <= 'f')
//        {
//            ret = (byte)(b - 'f' + 15);
//        }
//        if(b >= 'A' && b <= 'F')
//        {
//            ret = (byte)(b - 'F'+ 15);
//        }
//        return ret;
//    }
}

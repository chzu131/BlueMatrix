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
    private final int PATTERN_CUSTOM = 6;
    private byte CustomPattern[]={
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0,
            0x0,0x0,0x0,0x0
    };

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
        BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
        if(characteristic != null)
        {
            for (int i = 0,j = 0; j < data.length; i++,j++)
            {
                if((i+1)%4 == 0)
                {
                    CustomPattern[i] = 0x0;     //每一行最后四个灯，保留0x0，与底层兼容
                }
                else
                {
                    CustomPattern[i] = ByteConvertToHex(data[j]);
                }

            }
            characteristic.setValue(CustomPattern);
            mBluetoothLeService.writeCharacteristic(characteristic);
        }
    }

    //传送文字图案
    public void SendTextPattern(byte data[])
    {
        if(data.length > 100)
        {
            return;
        }
        byte CustomTextPattern[] = new byte[data.length];
        BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
        if(characteristic != null)
        {
            for (int i = 0; i < data.length; i++)
            {
                CustomTextPattern[i] = ByteConvertToHex(data[i]);
            }
            characteristic.setValue(CustomTextPattern);
            mBluetoothLeService.writeCharacteristic(characteristic);
        }
    }
    public void getGattService(BluetoothGattService gattService)
    {
        BluetoothGattCharacteristic characteristic = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
        map.put(characteristic.getUuid(), characteristic);

        BluetoothGattCharacteristic characteristic2 = gattService.getCharacteristic(
                RBLService.UUID_BLE_SHIELD_REGULARCOMMAND);
        map.put(characteristic2.getUuid(), characteristic2);
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

    //十进制转为十六进制
    private byte  ByteConvertToHex(byte b)
    {
        byte ret = b;
        if(b >= '0' && b <= '9')
        {
            ret = (byte)(b - '0');

        }
        if(b >= 'a' && b <= 'f')
        {
            ret = (byte)(b - 'f' + 15);
        }
        if(b >= 'A' && b <= 'F')
        {
            ret = (byte)(b - 'F'+ 15);
        }
        return ret;
    }
}

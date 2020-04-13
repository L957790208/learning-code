package com.heartrate.my;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public class MyBand {
	
	private static BluetoothDevice device;
	private static BluetoothGatt gatt;	

    public static void setBluetoothDevice(BluetoothDevice d , BluetoothGatt bg){
    	 device = d;
    	 gatt = bg; 
    }
    public static BluetoothDevice getBluetoothDevice(){
    	return device;
    }
	
	public static BluetoothGatt getGatt(){
		return gatt;
	}
	

	
}













package com.heartrate.my;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.os.Message;
/**
 * Author: xiaojun
 * Email: 786206257@qq.com
 * Date: 21/2/17
 */
public class  MyApplication extends Application {

	private BluetoothDevice device;
	public BluetoothGatt gatt;		
	public MeasurementData mUserData;
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler(){
		
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("mHandler---------");
    		mUserData = (MeasurementData) msg.obj;   		
    		//updatUI(mUserData); 		
        }	
	};		
	public BluetoothDevice getBluetoothDevice(){
		return this.device;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
	}
	
}






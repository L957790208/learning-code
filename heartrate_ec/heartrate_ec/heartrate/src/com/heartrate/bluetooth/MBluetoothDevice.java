package com.heartrate.bluetooth;

import java.io.Serializable;

import android.bluetooth.BluetoothDevice;

@SuppressWarnings("serial")
public class MBluetoothDevice implements Serializable {

	public BluetoothDevice device;
    
	public BluetoothDevice getDevice() {
		return device;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

}

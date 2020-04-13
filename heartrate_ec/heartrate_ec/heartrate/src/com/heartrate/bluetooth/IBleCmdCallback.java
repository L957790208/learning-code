package com.heartrate.bluetooth;

public abstract interface IBleCmdCallback {
	public abstract void connectState(int paramInt);
	public abstract void connectionError(int paramInt);
	public abstract void syncState(int paramInt);
}

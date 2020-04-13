package com.heartrate.entry;
import java.io.Serializable;
public class BaseDevice implements Serializable{
  private long connectedTime;
  private String mac;
  private String name;
  private int profileType = 1;
  private int rssi;

  public BaseDevice() {
  }

  public BaseDevice(String paramString1, String paramString2, int paramInt){
    this.name = paramString1;
    this.mac = paramString2;
    this.rssi = paramInt;
  }

  public BaseDevice(String paramString1, String paramString2, int paramInt1, int paramInt2) {
    this.name = paramString1;
    this.mac = paramString2;
    this.rssi = paramInt1;
    this.profileType = paramInt2;
  }

  public BaseDevice(String paramString1, String paramString2, int paramInt, long paramLong){
    this.name = paramString1;
    this.mac = paramString2;
    this.rssi = paramInt;
    this.connectedTime = paramLong;
  }

  public BaseDevice(String paramString1, String paramString2, int paramInt1, long paramLong, int paramInt2){
    this.name = paramString1;
    this.mac = paramString2;
    this.rssi = paramInt1;
    this.profileType = paramInt2;
    this.connectedTime = paramLong;
  }

  public long getConnectedTime() {
    return this.connectedTime;
  }

  public String getMac(){
    return this.mac;
  }

  public String getName(){
    return this.name;
  }

  public int getProfileType() {
    return this.profileType;
  }

  public int getRssi(){
    return this.rssi;
  }

  public void setConnectedTime(long paramLong){
    this.connectedTime = paramLong;
  }

  public void setName(String paramString){
    this.name = paramString;
  }

  public void setProfileType(int paramInt) {
    this.profileType = paramInt;
  }

  public void setRssi(int paramInt){
    this.rssi = paramInt;
  }
  
}





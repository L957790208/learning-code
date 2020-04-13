package com.heartrate.bluetooth;

import android.content.Context;
import android.content.Intent;

public class BroadCastAction
{
  public static final String ACTION_ASK_CONNECT_STATUS = "com.ainia.ble.ACTION_ASK_CONNECT_STATUS";
  public static final String ACTION_BLE_CONNECT = "com.ainia.ble.ACTION_BLE_CONNECT";
  public static final String ACTION_BLE_SEND_DATA = "com.ainia.ble.ACTION_BLE_SEND_DATA";
  public static final String ACTION_BLE_START_SCAN = "com.ainia.ble.ACTION_BLE_START_SCAN";
  public static final String ACTION_BLE_STOP_SCAN = "com.ainia.ble.ACTION_BLE_STOP_SCAN";
  public static final String ACTION_BLE_UNBUNDLING = "com.ainia.ble.ACTION_BLE_UNBUNDLING";
  public static final String ACTION_BLE_UPDATE_SETTING = "com.ainia.ble.ACTION_BLE_UPDATE_SETTING";
  public static final String ACTION_SEND_CLOCK = "com.ainia.ble.ACTION_SEND_CLOCK";
  public static final String BROADCAST_DATA = "com.ainia.ble.BROADCAST_DATA";
  public static final String EXTRA_CLOCK_NUMBER = "com.ainia.ble.EXTRA_CLOCK_NUMBER";
  public static final String EXTRA_DEVICE_ADDRESS = "com.ainia.ble.DEVICE_ADDRESS";
  public static final String EXTRA_DEVICE_NAME = "com.ainia.ble.DEVICE_NAME";
  public static final String EXTRA_DEVICE_POWER = "com.ainia.ble.EXTRA_DEVICE_POWER";
  public static final String EXTRA_DEVICE_RSSI = "com.ainia.ble.DEVICE_RSSI";
  public static final String RETURN_BLE_CONNECT = "com.ainia.ble.RETURN_BLE_CONNECT";
  public static final String RETURN_BLE_CONNECTED = "com.ainia.ble.RETURN_BLE_CONNECTED";
  public static final String RETURN_BLE_CONNECTING = "com.ainia.ble.RETURN_BLE_CONNECTING";
  public static final String RETURN_BLE_DEVICE_INFO = "com.ainia.ble.RETURN_BLE_DEVICE_INFO";
  public static final String RETURN_BLE_DISCONNECT = "com.ainia.ble.RETURN_BLE_DISCONNECT";
  public static final String RETURN_BLE_DISCONNECTED = "com.ainia.ble.RETURN_BLE_DISCONNECTED";
  public static final String RETURN_BLE_DISCOVER = "com.ainia.ble.RETURN_BLE_DISCOVER";
  public static final String RETURN_BLE_ERROR = "com.ainia.ble.RETURN_BLE_ERROR";
  public static final String RETURN_BLE_FARAWAY = "com.ainia.ble.RETURN_BLE_FARAWAY";
  public static final String RETURN_BLE_NOCONNECT = "com.ainia.ble.RETURN_BLE_NOCONNECT";
  public static final String RETURN_BLE_NOTIFY_DATA = "com.ainia.ble.RETURN_BLE_NOTIFY_DATA";
  public static final String RETURN_BLE_PHOTO = "com.ainia.ble.RETURN_BLE_PHOTO";
  public static final String RETURN_BLE_READ_DATA = "com.ainia.ble.RETURN_BLE_READ_DATA";
  public static final String RETURN_BLE_SCANING = "com.ainia.ble.RETURN_BLE_SCANING";
  public static final String RETURN_BLE_SCAN_DEVICE = "com.ainia.ble.RETURN_BLE_SCAN_DEVICE";
  public static final String RETURN_BLE_SCAN_OVER = "com.ainia.ble.RETURN_BLE_SCAN_OVER";
  public static final String RETURN_BLE_WRITE_DATA = "com.ainia.ble.RETURN_BLE_WRITE_DATA";
  public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
  private static String TAG = "11111111110000";

  public static void broadcastUpdate(Context paramContext, String paramString)
  {
    CommonInfo.ALog(TAG, "-----send broadcast action[" + paramString + "]-----");
    paramContext.sendBroadcast(new Intent(paramString));
  }

  public static void broadcastUpdate(Context paramContext, String paramString, Intent paramIntent)
  {
    paramIntent.setAction(paramString);
    paramContext.sendBroadcast(paramIntent);
  }

  public static void broadcastUpdate(Context paramContext, String paramString, byte[] paramArrayOfByte)
  {
    Intent localIntent = new Intent(paramString);
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 0))
      localIntent.putExtra("com.ainia.ble.BROADCAST_DATA", paramArrayOfByte);
    paramContext.sendBroadcast(localIntent);
  }
}
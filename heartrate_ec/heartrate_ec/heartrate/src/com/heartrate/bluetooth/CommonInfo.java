package com.heartrate.bluetooth;

import com.heartrate.tools.ConfigInfo;

public class CommonInfo
{
  public static final int HANDLER_WELCOME_TO_MAIN = 65536;
  public static final int NOTIFICATION_ID = 131586;
  public static final int NOTIFICATION_SERVICE_ID = 65793;
  public static final int REQUEST_BLE_OPEN = 8193;
  public static final int REQUEST_DEVICE_SELECT = 8195;
  public static final int REQUEST_SCAN_BLE = 8194;
  public static final int REQUEST_SET_CLOCK = 8197;
  public static final int REQUEST_UNBUNDLING = 8196;
  public static final int SCANBLE_STATUS_CONNECT = 19;
  public static final int SCANBLE_STATUS_NORMAL = 17;
  public static final int SCANBLE_STATUS_SCAN = 18;
  public static final int SCANBLE_UI_PHONE_COLOR = 278;
  public static final int SCANBLE_UI_PHONE_GRAY = 277;
  public static final int SCANBLE_UI_PROGRESS_LEFT = 274;
  public static final int SCANBLE_UI_PROGRESS_MIDDLE = 275;
  public static final int SCANBLE_UI_PROGRESS_NONE = 273;
  public static final int SCANBLE_UI_PROGRESS_RIGHT = 276;
  public static final int SCANBLE_UI_RING_COLOR = 258;
  public static final int SCANBLE_UI_RING_GRAY = 257;
  public static final int TIMER_STOP_SCAN_FLAG = 196609;

  public static final void ALog(String paramString1, String paramString2)
  {
    ConfigInfo.ALog(paramString1, paramString2);
  }

  public static final boolean getClockFlag(int paramInt)
  {
    return (paramInt & 0x80) > 0;
  }

  public static final boolean getClockFlag(int paramInt1, int paramInt2)
  {
    return (paramInt1 & 1 << paramInt2) > 0;
  }

  public static final String getClockString(int paramInt)
  {
    String str = "";
    if ((paramInt & 0x1) > 0)
      str = str + "周一 ";
    if ((paramInt & 0x2) > 0)
      str = str + "周二 ";
    if ((paramInt & 0x4) > 0)
      str = str + "周三 ";
    if ((paramInt & 0x8) > 0)
      str = str + "周四 ";
    if ((paramInt & 0x10) > 0)
      str = str + "周五 ";
    if ((paramInt & 0x20) > 0)
      str = str + "周六 ";
    if ((paramInt & 0x40) > 0)
      str = str + "周日 ";
    return str;
  }
}
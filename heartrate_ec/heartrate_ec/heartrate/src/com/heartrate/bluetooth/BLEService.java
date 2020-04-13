package com.heartrate.bluetooth;

import java.util.Calendar;
import java.util.UUID;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


/*要先在手机把位置打开，后面改应用自动打开，新的系统bluetooth要用位置
1. DeviceListActivity 发送广播 "com.ainia.ble.ACTION_BLE_START_SCAN"
2. 这里的mServiceBroadcastReceiver 收到广播
3. 调用startBLEScan（）
4. 扫描到bluetooth 回调这个方法  发送广播通知扫描到"com.ainia.ble.RETURN_BLE_SCAN_DEVICE"
5. DeviceListActivity 收到广播 listview显示出来
6. 选中一个发送广播通知道服务停止扫描，连接蓝牙
7.. //连接成功发送广播通知 BroadCastAction.broadcastUpdate(BLEService.this.appContext,
						 "com.ainia.ble.RETURN_BLE_CONNECT");
	收到连接成功 用SharedPreferences	把Address存起来				 
8. 读数据 BroadCastAction.broadcastUpdate(this.context, "com.ainia.ble.ACTION_BLE_READ_DATA");
            收到广播 readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_USER_DATA);
                   调用//BluetoothGatt.readCharacteristic 发起读UUID 返回的数据在onCharacteristicRead	
                   收到数据     下发广播通知       
 9. 通知 UUID notificationCharacteristic(characteristic,enable) ，跟读一样先拿到BluetoothGattCharacteristic           
   setCharacteristicNotification()第二个参数，使能手环发通知给手机。     
           收到设备发来的通知会调这个方法onCharacteristicChanged()，在这里面用广播把数据下发下去。     		             
    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
				Profile.UUID_DESCRIPTOR_UPDATE_NOTIFICATION);    			
   		if (descriptor != null) {       			
   			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
   			mBluetoothGatt.writeDescriptor(descriptor);
   		}
   ENABLE_NOTIFICATION_VALUE  使能通知
   DISABLE_NOTIFICATION_VALUE 禁用通知
 10. 写数据 收到写广播 ，跟读一样先拿到BluetoothGattCharacteristic characteristic 
     characteristic.setValue(value);
	 mBluetoothGatt.writeCharacteristic(characteristic);
 11. 当手环离开通信距离或关闭蓝牙时，下次自动连接上的方法 ，自动扫描。
      bleScanState用来标志，开启自动扫描。
	  1  表示 在DeviceListActivity打开扫描，这时不用判断信号，直接把扫描到的用广播发下去
	  2 表示手环断开后开启自动扫描，这时判断信号强度到一定值自己连接设备。	    
*/ 

public class BLEService extends Service{
	
	  private String USERSTEP = "user_step_data";
	  private static final String TAG = "message";
	  private Context appContext;	
	  private final IBinder mBinder = new LocalBinder();
	  private BluetoothAdapter mBluetoothAdapter;
	  private BluetoothGatt mBluetoothGatt;
	  private BluetoothManager mBluetoothManager;
	  private boolean mCallStatus = false;
	  private String mConnectStatus;
	  private boolean mFirstFlag = true;
	  private boolean mUnbundling = false;
	  private String mAddress = "";
	  private String mPower = "0";
	  private String mRSSI = "0";
	  private boolean mSMSStatus = false;
	  
	  private boolean bleConnentState = false;
	  
	  /*bleScanState用来标志，扫描到蓝牙后怎么处理，
	   *  1  表示 在DeviceListActivity打开扫描，这时不用判断信号，直接把扫描到的用广播发下去
	   *  2 表示手环断开后开启自动扫描，这时判断信号强度到一定值自己连接设备。
	  */
	  private short  bleScanState = 0;   
	  
	  private int mSPH_Duration;
	  private int mSPH_Rssi;	  
	  //mIncomingFlag来电还打电话都会进"android.intent.action.PHONE_STATE"
	  private static boolean mIncomingFlag = false;  
	  
	  //service的广播在app一打开就开始监听
	  private BroadcastReceiver mServiceBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
		    String str1 = intent.getAction();		          
		    if (str1.equals("com.ainia.ble.ACTION_BLE_READ_DATA")) {
		    	System.out.println("BLEService----read--data--ask");
		    	String key = (String) intent.getSerializableExtra("read_ble_uuid");
		    	if (key.equals("0000")) {
		    		readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_USER_DATA);
		    		return;
				}
		    	if (key.equals("0000")) {
		    		readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_TIME_SYNC);
		    		return;
				}	
		    	if (key.equals("0000")) {
		    		readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_RSC_M);
		    		return;
				}	
		    	if (key.equals("0000")) {
		    		readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_IPONE);
		    		return;
				}
		    	if (key.equals("0000")) {
		    		readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_ALARM_CLOCK);
		    		return;
				}		    	
		    	if (key.equals("0000")) {
		    		readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_SEARCH_IPONE);
		    		return;
				}
		    	if (key.equals("1530")) {
		    		readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_REMINDER_FUNCTION);
		    		return;
				}
		    	if (key.equals("0000")) {
		    		readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_REGULAR_SLEEP);
		    		return;
				}		    	
			}			    		  		    
		    if (str1.equals("com.ainia.ble.ACTION_BLE_READ_USER_INFO")) {
		    	System.out.println("---BLEService----ACTION_BLE_READ_USER_INFOk");
		    	readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_USER_DATA);
		    	return ;
			}
		    if (str1.equals("com.ainia.ble.ACTION_BLE_WIRTE_USER_INFO")) {
		    	System.out.println("BLEService--WIRTE_USER_INFO--");
		    	byte[] value = intent.getByteArrayExtra("wirte_ble_info");
		    	wirteBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_USER_DATA,value);
		    	return ;
			}			    
		    if (str1.equals("com.ainia.ble.ACTION_BLE_SYNC_SYSTEM_TIME")) {
		    	byte[] timeValue = getSystemTime();
		    	wirteBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_TIME_SYNC,timeValue);
		    	return ;
			}		    
		    if (str1.equals("com.ainia.ble.ACTION_BLE_WRITE_REGULAR_SLEEP")) {
		    	byte[] timeValue = intent.getByteArrayExtra("write.regular.sleep");
		    	wirteBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_REGULAR_SLEEP,timeValue);
		    	return ;
			}		    
		    if (str1.equals("com.ainia.ble.ACTION_BLE_WRITE_ALARM_CLOCK")) {
		    	byte[] timeValue = intent.getByteArrayExtra("write.alarm.clock");
		    	wirteBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_ALARM_CLOCK,timeValue);
		    	return ;							
			}
		    if (str1.equals("com.ainia.ble.ACTION_BLE_WRITE_CALL")) {
		    	byte[] mValue = intent.getByteArrayExtra("write.call.info");
		    	wirteBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_IPONE,mValue);
		    	return ;
			}	
		    if (str1.equals("com.ainia.ble.ACTION_BLE_WRITE_MESSAGE_PUSH")) {
		    	wirteBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_MESSAGE_PUSH,getMessagePush());
		    	return ;
			}	
		    if (str1.equals("com.ainia.ble.ACTION_BLE_WRITE_MESSAGE_ENABLE")) {
		    	byte[] mValue = intent.getByteArrayExtra("write.message.enable");
		    	wirteBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_REMINDER_FUNCTION, mValue);
		    	System.out.println("ACTION_BLE_WRITE_MESSAGE_ENABLE");
		    	return ;
			}		    
		    if (str1.equals("com.ainia.ble.ACTION_BLE_NOTIFICATION")) {
		    	System.out.println("BLEService---notification--ask");
		    	String notificationCharacteristic = (String) intent.getSerializableExtra("ble_notification_uuid");
		    	boolean notificationEnable = intent.getBooleanExtra("ble_notification_enale", false);	
		    	if (notificationCharacteristic.equals("0000")) {
		    		notificationCharacteristic(Profile.UUID_SERVICE_RSC,Profile.UUID_RSC_M,notificationEnable);
		    		return;
				}
		    	return ;
			}		    
		    		    
		    if (str1.equals("com.ainia.ble.ACTION_BLE_START_SCAN")) {
		    	//
		    	/*byte[] i = {0x0,0x0,0x5,0x0,0x1,0x0,0x01,0x00,0x01,0x01};
		    	mDataBroadcastSend(i);
		    	测试数据
		    	 */
		    	bleScanState = 1;
		    	BLEService.this.startBLEScan();
		    	return ;
			}
		    if (str1.equals("com.ainia.ble.ACTION_BLE_STOP_SCAN")) {	
		    	bleScanState = 0;
		    	BLEService.this.stopBLEScan();
		    	return ;
			}
		     //收到是点的哪个item
		    if (str1.equals("com.ainia.ble.RETURN_DEVICE_ITEM")) {	  		    	
		    	stopBLEScan();
		    	
		    	String item = (String) intent.getSerializableExtra("device_item_address");
		    	mAddress = item;
		    	System.out.println("BLEService---"+mAddress);
		    	//deviceMap.get(item);
		    	mConnect(mAddress);	    	
		    	return ;
			}		    		    
		    if (str1.equals("com.ainia.ble.ACTION_BLE_UNBUNDLING")){
		        BLEService.this.mAddress = "";
		        BLEService.this.mUnbundling = true;
		        if (BLEService.this.mBluetoothGatt == null){
		            BroadCastAction.broadcastUpdate(BLEService.this.appContext, "com.ainia.ble.RETURN_BLE_ERROR");
		            return;
		        }
		        BLEService.this.mBluetoothGatt.disconnect();
		        BLEService.this.mBluetoothGatt.close();
		        BLEService.this.mBluetoothGatt = null;
		        return;
		    }	

	        // 如果是拨打电话
	        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
	        	//
	        	mIncomingFlag = false;
	        	
	            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
	            
	            Log.i(TAG, "call OUT:" + phoneNumber);
	            
	        }	

	        if (str1 != null && "com.android.phone.NotificationMgr.MissedCall_intent".equals(str1)) { 
	            int mMissCallCount = intent.getExtras().getInt("MissedCallNumber"); 
	            System.out.println("未接来电个数 1---" + mMissCallCount);
	          }	        
	        
	        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {	        	
	            // 如果是来电
	        	System.out.println("-------PHONE_STATE------");
	            TelephonyManager tManager = (TelephonyManager) context
	                    .getSystemService(Service.TELEPHONY_SERVICE);
	            Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_WRITE_CALL");
	            switch (tManager.getCallState()) {            
	            //电话进来时
	            case TelephonyManager.CALL_STATE_RINGING:
	              //  mIncomingNumber = intent.getStringExtra("incoming_number");
	            	System.out.println(intent.getStringExtra("incoming_number")); 
	            	//System.out.println(intent.getIntExtra(MISSECALL_EXTRA, 0));//未接电话
	            	System.out.println("未接来电个数 2---" +  readMissCall());
	            	
	    			
	    		    localIntent.putExtra("write.call.info", getCall(intent.getStringExtra("incoming_number")));
	    		    BroadCastAction.broadcastUpdate(appContext, 
	    		   		"com.ainia.ble.ACTION_BLE_WRITE_CALL", localIntent);	
	    		    
	                break;
	            // 接起电话时
	            case TelephonyManager.CALL_STATE_OFFHOOK:
	            	System.out.println("---------vCALL_STATE_OFFHOOK---------");
	    		    localIntent.putExtra("write.call.info", ugetCall(intent.getStringExtra("incoming_number")));
	    		    BroadCastAction.broadcastUpdate(appContext, 
	    		   		"com.ainia.ble.ACTION_BLE_WRITE_CALL", localIntent);		            	
	                break;
	            //无任何状态时 
	            case TelephonyManager.CALL_STATE_IDLE:
	            	System.out.println("---------CALL_STATE_IDLE---------");
	    		    localIntent.putExtra("write.call.info", ungetCall(intent.getStringExtra("incoming_number")));
	    		    BroadCastAction.broadcastUpdate(appContext, 
	    		   		"com.ainia.ble.ACTION_BLE_WRITE_CALL", localIntent);	            	
	                break;
	            }
	        }
/*	        if (str1.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
 * 
			}*/
	        if (str1.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
	            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
	                    BluetoothAdapter.ERROR);
	            switch (state) {
	                case BluetoothAdapter.STATE_OFF:
	                	System.out.println("STATE_OFF 手机蓝牙关闭");
	                    break;
/*	                case BluetoothAdapter.STATE_TURNING_OFF:
	                    System.out.println("STATE_TURNING_OFF 手机蓝牙正在关闭");
	                    break;*/
	                case BluetoothAdapter.STATE_ON:
	                    System.out.println("STATE_ON 手机蓝牙开启");
	                    break;
/*	                case BluetoothAdapter.STATE_TURNING_ON:
	                    System.out.println("STATE_TURNING_ON 手机蓝牙正在开启");
	                    break;*/
	            }
	        }	            
		}
		 	   
	  };
	  
	  @SuppressLint({"HandlerLeak"})
	  private Handler mHandler = new Handler()
	  {
	    public void handleMessage(Message paramAnonymousMessage)
	    {
	      switch (paramAnonymousMessage.what)
	      {
	      default:
	      case 196609:
	      }
	      while (true)
	      {
	        super.handleMessage(paramAnonymousMessage);
	        BLEService.this.stopBLEScan();
	        return;
	      }
	    }
	  };

	  private ScanCallback scanCallback = new ScanCallback(){
			@Override
			public void onScanResult(int callbackType, ScanResult result){
				System.out.println("---BLEService----ScanCallback()---------" + bleScanState);
				BluetoothDevice device = result.getDevice();
				/*				if (!deviceMap.containsKey(device.getAddress())){
				deviceMap.put("aa", device);					
			}	*/
				if (bleScanState == 1) {			
					Intent localIntent = new Intent("com.ainia.ble.RETURN_BLE_SCAN_DEVICE");
				    localIntent.putExtra("com.ainia.ble.DEVICE_ADDRESS", device.getAddress());
				    localIntent.putExtra("com.ainia.ble.DEVICE_NAME", device.getName());
				    localIntent.putExtra("com.ainia.ble.DEVICE_RSSI", result.getRssi());
				    BroadCastAction.broadcastUpdate(BLEService.this.appContext, 
				    		"com.ainia.ble.RETURN_BLE_SCAN_DEVICE", localIntent);	
				    return ;
				}
				if (bleScanState == 2) {	
					if (device.getAddress().equals(mAddress)) {	
						if(result.getRssi() > -88){
							mConnect(mAddress);
							stopBLEScan();
						}
					}
				    return ;
				}
			}
		};	  	  
	  
	public void onCreate(){
		System.out.println("---BLEService------onCreate----");
		registerReceiver(this.mServiceBroadcastReceiver, makeBroadcastIntentFilter());
		this.appContext = getApplicationContext();
		if (initAddress()) {
			initialize();
			mConnect(mAddress);	
		}
		//this.mAddress = this.mSharePreHelper.getBLEDeviceAddress();	
	    super.onCreate();
	    
	}
    // 启动 搜索bluetoothDevice
	protected boolean startBLEScan() {
		// TODO Auto-generated method stub
	    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(null == mBluetoothAdapter)
		{
			System.out.println("--BLEService---null == mBluetoothAdapter--");
			return false;
		}
		BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
		if(null == scanner){
			System.out.println("--BLEService---null == scanner--");
			return false;
		}
		scanner.startScan(scanCallback);
		System.out.println("--BLEService---startScan--");
		return true;	
	}
	// 停止 搜索bluetoothDevice
	protected boolean stopBLEScan() {
		// TODO Auto-generated method stub
		System.out.println("--stopBLEScan--");
		if (this.mBluetoothAdapter == null){
			return false;
		}			  
		BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
		if(null == scanner){
			return false;
		}
		scanner.stopScan(scanCallback);
		System.out.println("--BLEService---stopScan111--");
		return false;
	}
	private IntentFilter makeBroadcastIntentFilter() {
	    IntentFilter localIntentFilter = new IntentFilter();
	    localIntentFilter.setPriority(1000);
	    localIntentFilter.addAction("com.ainia.ble.ACTION_ASK_CONNECT_STATUS");
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_CONNECT");
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_READ_DATA");//读数据
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_SEND_DATA");//写数据
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_NOTIFICATION");//通知	    
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_START_SCAN");//开始找设备
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_STOP_SCAN");//停止找设备
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_READ_USER_INFO");//读设备用户信息	
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WIRTE_USER_INFO");//向设备写入用户信息	
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_SYNC_SYSTEM_TIME");//同步时间
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_REGULAR_SLEEP");//设置自动睡眠时间	 
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_ALARM_CLOCK");//设置闹钟
	    localIntentFilter.addAction("com.android.phone.NotificationMgr.MissedCall_intent"); //未接来电广播
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_CALL");//来电广播给手环	    
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_MESSAGE_PUSH");//信息广播给手环	   
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_MESSAGE_ENABLE");//来电短信应用信息开关
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_UPDATE_SETTING");
	    localIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_UNBUNDLING");//解除绑定
	    localIntentFilter.addAction("com.ainia.ble.ACTION_SEND_CLOCK");
	    localIntentFilter.addAction("com.ainia.ble.RETURN_DEVICE_ITEM");//选中的设备
	    localIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//手机本身蓝牙状态的广播
	    localIntentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");//蓝牙连接断开
	    localIntentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");//蓝牙连接    	    
	    localIntentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
	    localIntentFilter.addAction("android.intent.action.PHONE_STATE");	   
   
	    return localIntentFilter;
	  }

	public void onDestroy()
	  {
		  System.out.println("---BLEService------onDestroy----");
		  unregisterReceiver(this.mServiceBroadcastReceiver);
	    super.onDestroy();
	  }
	  
	@Override
	  public IBinder onBind(Intent paramIntent){
		 System.out.println("---BLEService------IBinder----");
	    return this.mBinder;
	  }
	
	  public class LocalBinder extends Binder{
	    public LocalBinder(){
	    }
	    public BLEService getService(){
	      return BLEService.this;
	    }
	  }

	  private boolean initialize(){
	   System.out.println("=====BLEService.initialize=====");
	    if (this.mBluetoothManager == null) {
	      this.mBluetoothManager = ((BluetoothManager)getSystemService("bluetooth"));
	      if (this.mBluetoothManager == null)
	        return false;
	    }
	    if (this.mBluetoothAdapter == null){
	      this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
	      if (this.mBluetoothAdapter == null)
	        return false;
	    }
/*	    if (this.mAddress.length() == 0)
	        this.mConnectStatus = "com.ainia.ble.RETURN_BLE_NOCONNECT";
	    while (true){      
	        this.mConnectStatus = "com.ainia.ble.RETURN_BLE_CONNECTING";
	        connect(this.mAddress);
	        return true;
	    
	    } */
	    return true;
	  }	  
	  private boolean connect(String paramString){
		  
	    if (this.mBluetoothGatt != null)
	    {
	   //   CommonInfo.ALog(this.TAG, "=====BLEService.connect(" + paramString + ") re conn=====");
	      boolean bool = this.mBluetoothGatt.connect();
	      if (bool)
	      {
	     //   CommonInfo.ALog(this.TAG, "=====BLEService.connect(" + paramString + ") re conn OK=====");
	        return bool;
	      }
	    //  CommonInfo.ALog(this.TAG, "=====BLEService.connect(" + paramString + ") re conn ERR=====");
	      return bool;
	    }
	   // CommonInfo.ALog(this.TAG, "=====BLEService.connect(" + paramString + ") new conn=====");
	    if (paramString.length() == 0)
	    {
	      BroadCastAction.broadcastUpdate(this.appContext, "com.ainia.ble.RETURN_BLE_ERROR");
	      return false;
	    }
	    if (this.mBluetoothAdapter == null)
	    {
	      BroadCastAction.broadcastUpdate(this.appContext, "com.ainia.ble.RETURN_BLE_ERROR");
	      return false;
	    }
	    BluetoothDevice localBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(paramString);
	    if (localBluetoothDevice == null)
	    {
	      BroadCastAction.broadcastUpdate(this.appContext, "com.ainia.ble.RETURN_BLE_NOCONNECT");
	      return false;
	    }
	    this.mAddress = paramString;
	    this.mBluetoothGatt = localBluetoothDevice.connectGatt(this.appContext, false, this.mGattCallback);
	    return true;
	  }
	  
	  private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
			@Override  //当连接上设备或者失去连接时会回调该函数
			public void onConnectionStateChange(BluetoothGatt gatt, int status,int newState) {
				System.out.println("连接上或失去连接了设备" + status + "...." + newState);
				
				if (newState == BluetoothProfile.STATE_CONNECTED) { 
					//这句一定要有，开始把它注释了，调这个时BluetoothGatt.getService一直返回空，搞了个多小时
					//Attempt to invoke virtual method 'android.bluetooth.BluetoothGattCharacteristic 
					//android.bluetooth.BluetoothGattService.getCharacteristic(java.util.UUID)' 
					//on a null object reference
					mBluetoothGatt.discoverServices(); 
					bleConnentState = true;
					//连接成功后就去找出该设备中的服务 private BluetoothGatt mBluetoothGatt;
					 //连接成功
					//Toast.makeText(getApplication(), "连接成功", Toast.LENGTH_SHORT).show();
					//Looper.loop();

				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {  
					//断开连接
					bleConnentState = false;
					BroadCastAction.broadcastUpdate(BLEService.this.appContext, 
							"com.ainia.ble.DISCONNECTED");
					//这里是当手环断开后，开始扫描，当扫描到上次连接的设备连接上后关闭扫描。
					bleScanState = 2;
					BLEService.this.startBLEScan();
					
				//	Toast.makeText(getApplication(), "断开连接", Toast.LENGTH_SHORT).show();
				//	Looper.loop();
					
					return ;
				}
			}
			@Override  //当设备是否找到服务时，会回调该函数
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				System.out.println("设备找到服务");
				if (status == BluetoothGatt.GATT_SUCCESS) {   //找到服务了
					//在这里可以对服务进行解析，寻找到你需要的服务	
					 BroadCastAction.broadcastUpdate(BLEService.this.appContext,
							 "com.ainia.ble.RETURN_BLE_CONNECT_OK");
					 if (gatt == null){
						 System.out.println("没有寻找到服务	");
						 return ;
					 }else {
						// listBluetoothGattService = gatt.getServices();
						// displayGattServices(listBluetoothGattService);										
					 }					
				} else {
					//Log.w(TAG, "onServicesDiscovered received: " + status);
				}
			}
			@Override  //当读取设备时会回调该函数
			public void onCharacteristicRead(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
				//System.out.println("onCharacteristicRead");
				if (status == BluetoothGatt.GATT_SUCCESS) {
				  //读取到的数据存在characteristic当中，可以通过characteristic.getValue();函数取出。然后再进行解析操作。
					System.out.println(" "+characteristic.getValue());
					//数据下发的方法
					mDataBroadcastSend(characteristic.getValue());
				//	printHexString(characteristic.getValue());				
				}
			}
			@Override //当向设备Descriptor中写数据时，会回调该函数
			public void onDescriptorWrite(BluetoothGatt gatt,BluetoothGattDescriptor descriptor, int status) {
				System.out.println("onDescriptorWriteonDescriptorWrite = " + status + ", descriptor =" + descriptor.getUuid().toString());
				System.out.println("-----BLEService--------打开通知");
			}			
			@Override //设备发出通知时会调用到该接口
			public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic) {
				System.out.println("--------onCharacteristicChanged-----");
				if (characteristic.getValue() != null) {
					  mDataBroadcastSend(characteristic.getValue());
				}
			}
			/*
			 * RSSI在扫描时可以通过扫描回调接口获得，但是在连接之后要不断地使用				
				mBluetoothGatt.readRemoteRssi()向底层驱动发出读取RSSI请求，当底层获取到新的RSSI后会进行以下回调：	
				public voidonReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
				rssi即是新的信号强度值。
			 */
			@Override
			public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
				
				System.out.println("rssi = " + rssi);
				
			}
	        @Override //当向Characteristic写数据时会回调该函数
	        public void onCharacteristicWrite(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
	                   System.out.println("-----BLEService--------写入成功");
	                   BroadCastAction.broadcastUpdate(BLEService.this.appContext, "com.ainia.ble.ACTION_BLE_WRITE_SUCCESS");
	        };		
		};				  		
		//读数据
		public void readBluetoothData(UUID serviceUUID, UUID characteristicUUID){
			try{							
				if(null == mBluetoothGatt){				
					throw new Exception("connect to miband first");
				}
				BluetoothGattService bluetoothGattService = mBluetoothGatt.getService(serviceUUID);	
				if (bluetoothGattService ==null) {
					System.out.println("BLEService---nnnnnnnnnnnnnn");
				}
				System.out.println("BLEService---bbbbbbb");
				BluetoothGattCharacteristic chara = bluetoothGattService.getCharacteristic(characteristicUUID);
				if (null == chara){
					//this.onFail(-1, "BluetoothGattCharacteristic " + characteristicUUID + " is not exsit");
					Toast.makeText(appContext, "no characteristicUUID", Toast.LENGTH_SHORT).show();
					return;
				}				
				readCharacteristic(chara);
				if (false == this.mBluetoothGatt.readCharacteristic(chara)){
					//this.onFail(-1, "gatt.writeCharacteristic() return false");
					System.out.println("BLEService---3333333333333");
				}
			} catch (Throwable tr){				
				System.out.println("BLEService----read-data-fail--"+tr.getMessage());
				//this.onFail(-1, tr.getMessage());
			}
		}
		/*
		 * 读数据readCharacteristic调用成功会回调步骤BluetoothGattCallback中的onCharacteristicRead函数
		 * */
		public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
			if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
				return;
			}
			//BluetoothGatt.readCharacteristic 发起读UUID 返回的数据在onCharacteristicRead
			if(this.mBluetoothGatt.readCharacteristic(characteristic)) {
				//Toast.makeText(appContext, "读成功",Toast.LENGTH_SHORT).show();
			}
		}	
		//写数据
		public void wirteBluetoothData(UUID serviceUUID, UUID characteristicUUID, byte[] value){
			try{							
				if(null == mBluetoothGatt){
					
					throw new Exception("connect to miband first");
				}
				BluetoothGattService bluetoothGattService = mBluetoothGatt.getService(serviceUUID);	
				if (bluetoothGattService ==null) {					
				}
				BluetoothGattCharacteristic chara = bluetoothGattService.getCharacteristic(characteristicUUID);
				if (null == chara){
					//this.onFail(-1, "BluetoothGattCharacteristic " + characteristicUUID + " is not exsit");
					Toast.makeText(appContext, "No find --" + characteristicUUID, Toast.LENGTH_SHORT).show();
					return;
				}			
				wirteCharacteristic(chara, value);
				if (false == this.mBluetoothGatt.writeCharacteristic(chara)){
					//this.onFail(-1, "gatt.writeCharacteristic() return false")				
				}
			} catch (Throwable tr){				
				System.out.println("BLEService----read-data-fail--"+tr.getMessage());
				//this.onFail(-1, tr.getMessage());
			}
		}		
		/*
		 * 写数据wirteCharacteristic调用成功会回调步骤BluetoothGattCallback中的onCharacteristicWrite函数
		 * */		
		public void wirteCharacteristic(BluetoothGattCharacteristic characteristic,byte[] value) {

			if (mBluetoothAdapter == null || mBluetoothGatt == null) {
				System.out.println("BLEService-------BluetoothAdapter not initialized");
				return;
			}
			characteristic.setValue(value);
			mBluetoothGatt.writeCharacteristic(characteristic);
		}				
		//通知
		public void notificationCharacteristic(UUID serviceUUID, UUID characteristicUUID, boolean enable){
			try{							
				if(null == mBluetoothGatt){				
					throw new Exception("connect to miband first");
				}
				BluetoothGattService bluetoothGattService = mBluetoothGatt.getService(serviceUUID);	
				if (bluetoothGattService ==null) {					
				}
				BluetoothGattCharacteristic chara = bluetoothGattService.getCharacteristic(characteristicUUID);
				if (null == chara){
					//this.onFail(-1, "BluetoothGattCharacteristic " + characteristicUUID + " is not exsit");
					Toast.makeText(appContext, "no characteristicUUID", Toast.LENGTH_SHORT).show();
					return;
				}				
				setNotificationCharacteristic(chara, enable);
			} catch (Throwable tr){				
				//this.onFail(-1, tr.getMessage());
			}
		}		
		/*
		 * setNotificationCharacteristic 开关具有可通知的characteristic
		 * */
		public void setNotificationCharacteristic(BluetoothGattCharacteristic characteristic,boolean enable) {
			if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
				return;
			}  
			//setCharacteristicNotification要通知的uuid
			//setCharacteristicNotification()第二个参数，使能手环发通知给手机
			//这个就像是通知总开关，writeDescriptor
			mBluetoothGatt.setCharacteristicNotification(characteristic, true);
    		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
    				Profile.UUID_DESCRIPTOR_UPDATE_NOTIFICATION);    		
    		if (enable) {
    			Toast.makeText(appContext, "开通知",Toast.LENGTH_SHORT).show();	
        		if (descriptor != null) {       			
        			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        			mBluetoothGatt.writeDescriptor(descriptor);
        		}    			
			}else {
				Toast.makeText(appContext, "关通知",Toast.LENGTH_SHORT).show();	
	    		if (descriptor != null) {
	    			descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	    			mBluetoothGatt.writeDescriptor(descriptor);
	    		}
			}		
		}						
		//接收到的数据，通过广播发出
		private void mDataBroadcastSend(byte[] value) {
			// TODO Auto-generated method stub
            Intent localIntent = new Intent("com.ainia.ble.RETURN_BLE_DATA");
            localIntent.putExtra("return_ble_data", value);
			BroadCastAction.broadcastUpdate(BLEService.this.appContext, 
			    "com.ainia.ble.RETURN_BLE_DATA", localIntent);				
		}				
        private boolean initAddress(){
        	SharedPreferences sharedPreferences= getSharedPreferences("com_my_heartrate",
                    Activity.MODE_PRIVATE);
             mAddress=sharedPreferences.getString("bluetooth_address","noaddress");
             if (mAddress == "noaddress") {
            	 
				System.out.println("没有连接过的设备");
				return false;
			}else{
				return true;
			}
        }
        
		private void mConnect(String paramString) {
			// TODO Auto-generated method stub
			//该函数才是真正的去进行连接
			BluetoothDevice localBluetoothDevice = mBluetoothAdapter.getRemoteDevice(paramString);
			mBluetoothGatt = localBluetoothDevice.connectGatt(appContext, false, mGattCallback);	
			//MyBand.setBluetoothDevice(device,mBluetoothGatt);
			System.out.println("BLEService---------mConnect()");			
		} 			
		private byte[] getSystemTime() {
			// TODO Auto-generated method stub
			byte[] data = {0,0,0,0,0,0,0};
			Calendar mCalendar=Calendar.getInstance();
			int mYear = mCalendar.get(Calendar.YEAR);
			int mMonth = mCalendar.get(Calendar.MONTH) + 1;//Calendar的月份是从 0开始算的， 所以要+1
			int mDate = mCalendar.get(Calendar.DATE);
			int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);			
			int mMinuts=mCalendar.get(Calendar.MINUTE);
			int mSecond=mCalendar.get(Calendar.SECOND);
			data[0] = (byte) (mYear & 0xFF);
		    data[1] =  (byte) ((mYear >> 8) & 0xFF);
		    data[2] = (byte) (mMonth & 0xFF);
		    data[3] = (byte) (mDate & 0xFF);
		    data[4] = (byte) (mHour & 0xFF);
		    data[5] = (byte) (mMinuts & 0xFF);
		    data[6] = (byte) (mSecond & 0xFF);		    
			return data;
		}	
		
		//要发送给手环来电的信息 来电话时
		private byte[] getCall(String string) {
			// TODO Auto-generated method stub
			byte[] data = {1,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0 };
			if(string == null){
	            return data;
	        }
			for (int i = 0; i < string.length(); i++) {
				data[i+2]  = (byte) string.charAt(i); 
			}			
			return data;
		}	
		//要发送给手环来电的信息 来电话后没有接 
		private byte[] ungetCall(String string) {
			// TODO Auto-generated method stub
			byte[] data = {0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0 };
			if(string == null){
	            return data;
	        }
			data[1] = (byte) readMissCall();
			for (int i = 0; i < string.length(); i++) {
				data[i+2]  = (byte) string.charAt(i); 
			}		
			return data;
		}	
		//要发送给手环来电的信息 接起电话时
		private byte[] ugetCall(String string) {
			// TODO Auto-generated method stub
			byte[] data = {0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0 };
			if(string == null){
	            return data;
	        }
			for (int i = 0; i < string.length(); i++) {
				data[i+2]  = (byte) string.charAt(i); 
			}			
			return data;
		}			
		//要发送给手环信息的内容
		private byte[] getMessagePush() {
			// TODO Auto-generated method stub
			byte[] data = {0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0 };
			
			return data;
		}
/*
 * 广播只是当有新的未接来电时才会发送，但是如果有旧的未接来电没有读取时，上面的广播就得不到数据了，那就必须得从数据库中查找了。
 */
		private int readMissCall() { 
			  int result = 0; 
			  Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] { 
			      Calls.TYPE 
			    }, " type=? and new=?", new String[] { 
			        Calls.MISSED_TYPE + "", "1" 
			    }, "date desc"); 
			 
			  if (cursor != null) { 
			    result = cursor.getCount(); 
			    cursor.close(); 
			  } 
			  return result; 
			}	
}




                   











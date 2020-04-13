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


/*Ҫ�����ֻ���λ�ô򿪣������Ӧ���Զ��򿪣��µ�ϵͳbluetoothҪ��λ��
1. DeviceListActivity ���͹㲥 "com.ainia.ble.ACTION_BLE_START_SCAN"
2. �����mServiceBroadcastReceiver �յ��㲥
3. ����startBLEScan����
4. ɨ�赽bluetooth �ص��������  ���͹㲥֪ͨɨ�赽"com.ainia.ble.RETURN_BLE_SCAN_DEVICE"
5. DeviceListActivity �յ��㲥 listview��ʾ����
6. ѡ��һ�����͹㲥֪ͨ������ֹͣɨ�裬��������
7.. //���ӳɹ����͹㲥֪ͨ BroadCastAction.broadcastUpdate(BLEService.this.appContext,
						 "com.ainia.ble.RETURN_BLE_CONNECT");
	�յ����ӳɹ� ��SharedPreferences	��Address������				 
8. ������ BroadCastAction.broadcastUpdate(this.context, "com.ainia.ble.ACTION_BLE_READ_DATA");
            �յ��㲥 readBluetoothData(Profile.UUID_SERVICE_RSC,Profile.UUID_USER_DATA);
                   ����//BluetoothGatt.readCharacteristic �����UUID ���ص�������onCharacteristicRead	
                   �յ�����     �·��㲥֪ͨ       
 9. ֪ͨ UUID notificationCharacteristic(characteristic,enable) ������һ�����õ�BluetoothGattCharacteristic           
   setCharacteristicNotification()�ڶ���������ʹ���ֻ���֪ͨ���ֻ���     
           �յ��豸������֪ͨ����������onCharacteristicChanged()�����������ù㲥�������·���ȥ��     		             
    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
				Profile.UUID_DESCRIPTOR_UPDATE_NOTIFICATION);    			
   		if (descriptor != null) {       			
   			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
   			mBluetoothGatt.writeDescriptor(descriptor);
   		}
   ENABLE_NOTIFICATION_VALUE  ʹ��֪ͨ
   DISABLE_NOTIFICATION_VALUE ����֪ͨ
 10. д���� �յ�д�㲥 ������һ�����õ�BluetoothGattCharacteristic characteristic 
     characteristic.setValue(value);
	 mBluetoothGatt.writeCharacteristic(characteristic);
 11. ���ֻ��뿪ͨ�ž����ر�����ʱ���´��Զ������ϵķ��� ���Զ�ɨ�衣
      bleScanState������־�������Զ�ɨ�衣
	  1  ��ʾ ��DeviceListActivity��ɨ�裬��ʱ�����ж��źţ�ֱ�Ӱ�ɨ�赽���ù㲥����ȥ
	  2 ��ʾ�ֻ��Ͽ������Զ�ɨ�裬��ʱ�ж��ź�ǿ�ȵ�һ��ֵ�Լ������豸��	    
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
	  
	  /*bleScanState������־��ɨ�赽��������ô����
	   *  1  ��ʾ ��DeviceListActivity��ɨ�裬��ʱ�����ж��źţ�ֱ�Ӱ�ɨ�赽���ù㲥����ȥ
	   *  2 ��ʾ�ֻ��Ͽ������Զ�ɨ�裬��ʱ�ж��ź�ǿ�ȵ�һ��ֵ�Լ������豸��
	  */
	  private short  bleScanState = 0;   
	  
	  private int mSPH_Duration;
	  private int mSPH_Rssi;	  
	  //mIncomingFlag���绹��绰�����"android.intent.action.PHONE_STATE"
	  private static boolean mIncomingFlag = false;  
	  
	  //service�Ĺ㲥��appһ�򿪾Ϳ�ʼ����
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
		    	��������
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
		     //�յ��ǵ���ĸ�item
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

	        // ����ǲ���绰
	        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
	        	//
	        	mIncomingFlag = false;
	        	
	            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
	            
	            Log.i(TAG, "call OUT:" + phoneNumber);
	            
	        }	

	        if (str1 != null && "com.android.phone.NotificationMgr.MissedCall_intent".equals(str1)) { 
	            int mMissCallCount = intent.getExtras().getInt("MissedCallNumber"); 
	            System.out.println("δ��������� 1---" + mMissCallCount);
	          }	        
	        
	        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {	        	
	            // ���������
	        	System.out.println("-------PHONE_STATE------");
	            TelephonyManager tManager = (TelephonyManager) context
	                    .getSystemService(Service.TELEPHONY_SERVICE);
	            Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_WRITE_CALL");
	            switch (tManager.getCallState()) {            
	            //�绰����ʱ
	            case TelephonyManager.CALL_STATE_RINGING:
	              //  mIncomingNumber = intent.getStringExtra("incoming_number");
	            	System.out.println(intent.getStringExtra("incoming_number")); 
	            	//System.out.println(intent.getIntExtra(MISSECALL_EXTRA, 0));//δ�ӵ绰
	            	System.out.println("δ��������� 2---" +  readMissCall());
	            	
	    			
	    		    localIntent.putExtra("write.call.info", getCall(intent.getStringExtra("incoming_number")));
	    		    BroadCastAction.broadcastUpdate(appContext, 
	    		   		"com.ainia.ble.ACTION_BLE_WRITE_CALL", localIntent);	
	    		    
	                break;
	            // ����绰ʱ
	            case TelephonyManager.CALL_STATE_OFFHOOK:
	            	System.out.println("---------vCALL_STATE_OFFHOOK---------");
	    		    localIntent.putExtra("write.call.info", ugetCall(intent.getStringExtra("incoming_number")));
	    		    BroadCastAction.broadcastUpdate(appContext, 
	    		   		"com.ainia.ble.ACTION_BLE_WRITE_CALL", localIntent);		            	
	                break;
	            //���κ�״̬ʱ 
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
	                	System.out.println("STATE_OFF �ֻ������ر�");
	                    break;
/*	                case BluetoothAdapter.STATE_TURNING_OFF:
	                    System.out.println("STATE_TURNING_OFF �ֻ��������ڹر�");
	                    break;*/
	                case BluetoothAdapter.STATE_ON:
	                    System.out.println("STATE_ON �ֻ���������");
	                    break;
/*	                case BluetoothAdapter.STATE_TURNING_ON:
	                    System.out.println("STATE_TURNING_ON �ֻ��������ڿ���");
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
    // ���� ����bluetoothDevice
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
	// ֹͣ ����bluetoothDevice
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
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_READ_DATA");//������
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_SEND_DATA");//д����
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_NOTIFICATION");//֪ͨ	    
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_START_SCAN");//��ʼ���豸
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_STOP_SCAN");//ֹͣ���豸
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_READ_USER_INFO");//���豸�û���Ϣ	
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WIRTE_USER_INFO");//���豸д���û���Ϣ	
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_SYNC_SYSTEM_TIME");//ͬ��ʱ��
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_REGULAR_SLEEP");//�����Զ�˯��ʱ��	 
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_ALARM_CLOCK");//��������
	    localIntentFilter.addAction("com.android.phone.NotificationMgr.MissedCall_intent"); //δ������㲥
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_CALL");//����㲥���ֻ�	    
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_MESSAGE_PUSH");//��Ϣ�㲥���ֻ�	   
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_MESSAGE_ENABLE");//�������Ӧ����Ϣ����
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_UPDATE_SETTING");
	    localIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
	    localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_UNBUNDLING");//�����
	    localIntentFilter.addAction("com.ainia.ble.ACTION_SEND_CLOCK");
	    localIntentFilter.addAction("com.ainia.ble.RETURN_DEVICE_ITEM");//ѡ�е��豸
	    localIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//�ֻ���������״̬�Ĺ㲥
	    localIntentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");//�������ӶϿ�
	    localIntentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");//��������    	    
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
			@Override  //���������豸����ʧȥ����ʱ��ص��ú���
			public void onConnectionStateChange(BluetoothGatt gatt, int status,int newState) {
				System.out.println("�����ϻ�ʧȥ�������豸" + status + "...." + newState);
				
				if (newState == BluetoothProfile.STATE_CONNECTED) { 
					//���һ��Ҫ�У���ʼ����ע���ˣ������ʱBluetoothGatt.getServiceһֱ���ؿգ����˸���Сʱ
					//Attempt to invoke virtual method 'android.bluetooth.BluetoothGattCharacteristic 
					//android.bluetooth.BluetoothGattService.getCharacteristic(java.util.UUID)' 
					//on a null object reference
					mBluetoothGatt.discoverServices(); 
					bleConnentState = true;
					//���ӳɹ����ȥ�ҳ����豸�еķ��� private BluetoothGatt mBluetoothGatt;
					 //���ӳɹ�
					//Toast.makeText(getApplication(), "���ӳɹ�", Toast.LENGTH_SHORT).show();
					//Looper.loop();

				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {  
					//�Ͽ�����
					bleConnentState = false;
					BroadCastAction.broadcastUpdate(BLEService.this.appContext, 
							"com.ainia.ble.DISCONNECTED");
					//�����ǵ��ֻ��Ͽ��󣬿�ʼɨ�裬��ɨ�赽�ϴ����ӵ��豸�����Ϻ�ر�ɨ�衣
					bleScanState = 2;
					BLEService.this.startBLEScan();
					
				//	Toast.makeText(getApplication(), "�Ͽ�����", Toast.LENGTH_SHORT).show();
				//	Looper.loop();
					
					return ;
				}
			}
			@Override  //���豸�Ƿ��ҵ�����ʱ����ص��ú���
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				System.out.println("�豸�ҵ�����");
				if (status == BluetoothGatt.GATT_SUCCESS) {   //�ҵ�������
					//��������ԶԷ�����н�����Ѱ�ҵ�����Ҫ�ķ���	
					 BroadCastAction.broadcastUpdate(BLEService.this.appContext,
							 "com.ainia.ble.RETURN_BLE_CONNECT_OK");
					 if (gatt == null){
						 System.out.println("û��Ѱ�ҵ�����	");
						 return ;
					 }else {
						// listBluetoothGattService = gatt.getServices();
						// displayGattServices(listBluetoothGattService);										
					 }					
				} else {
					//Log.w(TAG, "onServicesDiscovered received: " + status);
				}
			}
			@Override  //����ȡ�豸ʱ��ص��ú���
			public void onCharacteristicRead(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
				//System.out.println("onCharacteristicRead");
				if (status == BluetoothGatt.GATT_SUCCESS) {
				  //��ȡ�������ݴ���characteristic���У�����ͨ��characteristic.getValue();����ȡ����Ȼ���ٽ��н���������
					System.out.println(" "+characteristic.getValue());
					//�����·��ķ���
					mDataBroadcastSend(characteristic.getValue());
				//	printHexString(characteristic.getValue());				
				}
			}
			@Override //�����豸Descriptor��д����ʱ����ص��ú���
			public void onDescriptorWrite(BluetoothGatt gatt,BluetoothGattDescriptor descriptor, int status) {
				System.out.println("onDescriptorWriteonDescriptorWrite = " + status + ", descriptor =" + descriptor.getUuid().toString());
				System.out.println("-----BLEService--------��֪ͨ");
			}			
			@Override //�豸����֪ͨʱ����õ��ýӿ�
			public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic) {
				System.out.println("--------onCharacteristicChanged-----");
				if (characteristic.getValue() != null) {
					  mDataBroadcastSend(characteristic.getValue());
				}
			}
			/*
			 * RSSI��ɨ��ʱ����ͨ��ɨ��ص��ӿڻ�ã�����������֮��Ҫ���ϵ�ʹ��				
				mBluetoothGatt.readRemoteRssi()��ײ�����������ȡRSSI���󣬵��ײ��ȡ���µ�RSSI���������»ص���	
				public voidonReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
				rssi�����µ��ź�ǿ��ֵ��
			 */
			@Override
			public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
				
				System.out.println("rssi = " + rssi);
				
			}
	        @Override //����Characteristicд����ʱ��ص��ú���
	        public void onCharacteristicWrite(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
	                   System.out.println("-----BLEService--------д��ɹ�");
	                   BroadCastAction.broadcastUpdate(BLEService.this.appContext, "com.ainia.ble.ACTION_BLE_WRITE_SUCCESS");
	        };		
		};				  		
		//������
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
		 * ������readCharacteristic���óɹ���ص�����BluetoothGattCallback�е�onCharacteristicRead����
		 * */
		public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
			if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
				return;
			}
			//BluetoothGatt.readCharacteristic �����UUID ���ص�������onCharacteristicRead
			if(this.mBluetoothGatt.readCharacteristic(characteristic)) {
				//Toast.makeText(appContext, "���ɹ�",Toast.LENGTH_SHORT).show();
			}
		}	
		//д����
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
		 * д����wirteCharacteristic���óɹ���ص�����BluetoothGattCallback�е�onCharacteristicWrite����
		 * */		
		public void wirteCharacteristic(BluetoothGattCharacteristic characteristic,byte[] value) {

			if (mBluetoothAdapter == null || mBluetoothGatt == null) {
				System.out.println("BLEService-------BluetoothAdapter not initialized");
				return;
			}
			characteristic.setValue(value);
			mBluetoothGatt.writeCharacteristic(characteristic);
		}				
		//֪ͨ
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
		 * setNotificationCharacteristic ���ؾ��п�֪ͨ��characteristic
		 * */
		public void setNotificationCharacteristic(BluetoothGattCharacteristic characteristic,boolean enable) {
			if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
				return;
			}  
			//setCharacteristicNotificationҪ֪ͨ��uuid
			//setCharacteristicNotification()�ڶ���������ʹ���ֻ���֪ͨ���ֻ�
			//���������֪ͨ�ܿ��أ�writeDescriptor
			mBluetoothGatt.setCharacteristicNotification(characteristic, true);
    		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
    				Profile.UUID_DESCRIPTOR_UPDATE_NOTIFICATION);    		
    		if (enable) {
    			Toast.makeText(appContext, "��֪ͨ",Toast.LENGTH_SHORT).show();	
        		if (descriptor != null) {       			
        			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        			mBluetoothGatt.writeDescriptor(descriptor);
        		}    			
			}else {
				Toast.makeText(appContext, "��֪ͨ",Toast.LENGTH_SHORT).show();	
	    		if (descriptor != null) {
	    			descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	    			mBluetoothGatt.writeDescriptor(descriptor);
	    		}
			}		
		}						
		//���յ������ݣ�ͨ���㲥����
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
            	 
				System.out.println("û�����ӹ����豸");
				return false;
			}else{
				return true;
			}
        }
        
		private void mConnect(String paramString) {
			// TODO Auto-generated method stub
			//�ú�������������ȥ��������
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
			int mMonth = mCalendar.get(Calendar.MONTH) + 1;//Calendar���·��Ǵ� 0��ʼ��ģ� ����Ҫ+1
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
		
		//Ҫ���͸��ֻ��������Ϣ ���绰ʱ
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
		//Ҫ���͸��ֻ��������Ϣ ���绰��û�н� 
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
		//Ҫ���͸��ֻ��������Ϣ ����绰ʱ
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
		//Ҫ���͸��ֻ���Ϣ������
		private byte[] getMessagePush() {
			// TODO Auto-generated method stub
			byte[] data = {0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0 };
			
			return data;
		}
/*
 * �㲥ֻ�ǵ����µ�δ������ʱ�Żᷢ�ͣ���������оɵ�δ������û�ж�ȡʱ������Ĺ㲥�͵ò��������ˣ��Ǿͱ���ô����ݿ��в����ˡ�
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




                   











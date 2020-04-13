package com.heartrate.activity;

import java.util.List;
import com.heartrate.bluetooth.BroadCastAction;
import com.heartrate.service.NeNotificationService;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MessageSettingActivity extends BasicActivity implements OnClickListener, OnCheckedChangeListener {
	
	private CheckBox cbCall, cbSms, cbAppMsg ;
	private ImageView imBack;
	private TextView tvSave;	
	private boolean callSwitchTag,smsSwitchTag,appMsgSwitchTag ;
	private Intent upservice;
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		                      
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String string = intent.getAction();
			System.out.println("MessageSettingActivity---BroadcastReceiver");	
			if (string.equals("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS")) {
				 Toast.makeText(MessageSettingActivity.this, "信息设置成功", Toast.LENGTH_SHORT).show();
				 return ;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_setting);
		initView();
		initSwitchTag();
		upservice = new Intent(this, NeNotificationService.class); 
	}

	private void initView() {
		// TODO Auto-generated method stub
		imBack = (ImageView) findViewById(R.id.message_cb_im_back);
		tvSave = (TextView) findViewById(R.id.message_cb_tv_save);
		cbCall = (CheckBox) findViewById(R.id.message_cb_call_switch);
		cbSms = (CheckBox) findViewById(R.id.message_cb_sms_switch);
		cbAppMsg = (CheckBox) findViewById(R.id.message_cb_appmsg_switch);
		
		imBack.setOnClickListener(this);
		tvSave.setOnClickListener(this);
		cbAppMsg.setOnCheckedChangeListener(this);
		cbCall.setOnCheckedChangeListener(this);
		cbSms.setOnCheckedChangeListener(this);
		findViewById(R.id.message_cb_button1).setOnClickListener(this);
	}
	private void initSwitchTag() {
		// TODO Auto-generated method stub
		callSwitchTag = false;
		smsSwitchTag = false;
		appMsgSwitchTag = false;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.message_cb_im_back:
			finish();
			break;
		case R.id.message_cb_tv_save:
			Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_WRITE_MESSAGE_ENABLE");
		    localIntent.putExtra("write.message.enable", getMessageEnable());
		    BroadCastAction.broadcastUpdate(this, 
		   		"com.ainia.ble.ACTION_BLE_WRITE_MESSAGE_ENABLE", localIntent);				
			break;
		case R.id.message_cb_button1:
			updateServiceStatus(true);

			break;
		default:
			break;
		}
	}

	//这里防止多次启动服务，所以先判断服务是否在运行中
	private void updateServiceStatus(boolean start){
		System.out.println("......./这里防止多次启动服务，所以先判断服务是否在运行中.........");
			boolean bRunning = isServiceRunning(this, "com.nis.bcreceiver.NeNotificationService");
			
			//没有Running则启动
			if (start && !bRunning) {
				this.startService(upservice);
			} else if(!start && bRunning) {
				this.stopService(upservice);
			}		
			
	}
	public boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(100);
		if (serviceList.size() == 0) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	private byte[] getMessageEnable() {
		byte[] data ={0,0,0,0,0,0,0};
		if (callSwitchTag) {
			data[1] =  (byte) 1;
		}
		if (smsSwitchTag) {
			data[2] =  (byte) 1;
		}		
		if (appMsgSwitchTag) {
			data[3] =  (byte) 1;
			data[4] = (byte)  1;
		}						
		return data;
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.message_cb_call_switch:
			callSwitchTag = isChecked;
			break;
		case R.id.message_cb_sms_switch:
			smsSwitchTag = isChecked;			
			break;	
		case R.id.message_cb_appmsg_switch:
			//appMsgSwitchTag = isChecked;	
			openAccessibility();
			break;					
		default:
			break;
		}
	}
	  private void openAccessibility() {
	  //  if (!NotificationService.getNotificationIsRun(this))
	      startActivityForResult(new Intent("android.settings.ACCESSIBILITY_SETTINGS"), 123);
	  }
	private IntentFilter makeIntentFilter() {
		 IntentFilter localIntentFilter = new IntentFilter();	
		 localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS");
		 return localIntentFilter;
	}	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		registerReceiver(this.mBroadcastReceiver, makeIntentFilter());
	}	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(this.mBroadcastReceiver);
	}

}















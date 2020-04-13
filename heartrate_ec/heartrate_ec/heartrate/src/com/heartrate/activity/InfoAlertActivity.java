package com.heartrate.activity;

import com.heartrate.bluetooth.BroadCastAction;

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

public class InfoAlertActivity extends BasicActivity implements OnClickListener, OnCheckedChangeListener{

	private ImageView imBack;
	private TextView tvSave;
	private CheckBox cbCall, cbSms, cbApp;
	private boolean callCheckTag, smsCheckTag, appCheckTag;
	
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String str = intent.getAction();
			System.out.println("DeviceSettingActivity---BroadcastReceiver");
			if (str.equals("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS")) {
				 Toast.makeText(InfoAlertActivity.this, "…Ë÷√≥…π¶", Toast.LENGTH_SHORT).show();
				 return ;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_alert);
		findView();
		
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		registerReceiver(mBroadcastReceiver, makeIntentFilter());
	}
	
	private IntentFilter makeIntentFilter() {
		 IntentFilter localIntentFilter = new IntentFilter();
		 localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS");
		 return localIntentFilter;
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(mBroadcastReceiver);
		
	}
	private void findView() {
		// TODO Auto-generated method stub
		imBack = (ImageView) findViewById(R.id.info_alert_im_back);
		tvSave = (TextView) findViewById(R.id.info_alert_tv_save);
		cbCall = (CheckBox) findViewById(R.id.info_alert_cb_call);
		cbSms = (CheckBox) findViewById(R.id.info_alert_cb_sms);
		cbApp = (CheckBox) findViewById(R.id.info_alert_cb_app);
		imBack.setOnClickListener(this);
		tvSave.setOnClickListener(this);
		cbApp.setOnCheckedChangeListener(this);
		cbCall.setOnCheckedChangeListener(this);
		cbSms.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.info_alert_im_back:
			finish();
			break;

		case R.id.info_alert_tv_save:
			saveInfoAlertSetting();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.info_alert_cb_app:
			appCheckTag = isChecked;
			break;
		case R.id.info_alert_cb_call:
			callCheckTag = isChecked;
			break;
		case R.id.info_alert_cb_sms:
			smsCheckTag = isChecked;
			break;			
		default:
			break;
		}
		
	}

	private void saveInfoAlertSetting() {
		Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_WRITE_INFO_ALERT_SETTING");
	    localIntent.putExtra("write.info.alert.setting", getInfoAlert());
	    BroadCastAction.broadcastUpdate(this, 
	    	"com.ainia.ble.ACTION_BLE_WRITE_INFO_ALERT_SETTING", localIntent);	
	    
	}
	private byte[] getInfoAlert() {
		// TODO Auto-generated method stub
		byte[] data = {0,0,0,0,0,0,0};
		if (callCheckTag) {
			data[1] = 0x01;
		}
		if (smsCheckTag) {
			data[2] = 0x01;
		}
		if (appCheckTag) {
			data[3] = 0x01;
			data[4] = 0x01;
		}		
		return data;
	}  
	
	
	
	
	
	
}




















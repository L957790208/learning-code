package com.heartrate.activity;

import java.util.Calendar;

import com.heartrate.activity.R;
import com.heartrate.bluetooth.BroadCastAction;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DeviceSettingActivity extends Activity implements OnClickListener{

    private RelativeLayout btnSynTime, btnAlarmClock, btnRegularSleep,
    					   btnTX, btnAntiLost, btnRemoveBinding;
	
	private ImageView imBack;
	public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String string = intent.getAction();
			System.out.println("DeviceSettingActivity---BroadcastReceiver");
			if (string.equals("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS")) {
				 Toast.makeText(DeviceSettingActivity.this, "时间同步完成", Toast.LENGTH_SHORT).show();
				 return ;
			}
			if (string.equals("com.ainia.ble.RETURN_BLE_DATA")) {
				 intent.getSerializableExtra("return_ble_data");
		    	 System.out.println(""+intent.getSerializableExtra("return_ble_data"));
		    	 byte [] data = (byte[]) intent.getSerializableExtra("return_ble_data");
		    	//更新ui
		    	// updatUserUI(data);
							
			}
		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_setting);
		Window window = this.getWindow();
		//取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); 

		//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); 
		//设置状态栏颜色
		window.setStatusBarColor(getResources().getColor(R.color.steelblue));				
		
		intiView();	
		
	}
	
	private void intiView() {
		// TODO Auto-generated method stub
		imBack = (ImageView) findViewById(R.id.iv_setting_back);
		btnAlarmClock = (RelativeLayout) findViewById(R.id.btn_alarm_clock);
		btnAntiLost = (RelativeLayout) findViewById(R.id.btn_anti_lost);
		btnRegularSleep = (RelativeLayout) findViewById(R.id.btn_regular_sleep);
		btnRemoveBinding = (RelativeLayout) findViewById(R.id.btn_remove_binding);
		btnSynTime = (RelativeLayout) findViewById(R.id.btn_syn_time);
		btnTX = (RelativeLayout) findViewById(R.id.btn_tx);
		
		imBack.setOnClickListener(this);
		btnAlarmClock.setOnClickListener(this);
		btnAntiLost.setOnClickListener(this);
		btnRegularSleep.setOnClickListener(this);
		btnRemoveBinding.setOnClickListener(this);
		btnSynTime.setOnClickListener(this);
		btnTX.setOnClickListener(this);
		
	}


	private IntentFilter makeIntentFilter() {
		 IntentFilter localIntentFilter = new IntentFilter();
		 localIntentFilter.addAction("com.ainia.ble.RETURN_BLE_DATA");
		 localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS");
		 return localIntentFilter;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {	
		case R.id.iv_setting_back:
			finish();
			break;
		case R.id.btn_alarm_clock:
			intent = new Intent(this,AlarmClockActivity.class); 
		    startActivity(intent);
			break;
		case R.id.btn_anti_lost:
			
			break;
		case R.id.btn_regular_sleep:
			intent = new Intent(this,RegularSleepActivity.class); 
		    startActivity(intent);			
			break;
		case R.id.btn_remove_binding:
			unBidingBle();
			break;
		case R.id.btn_syn_time:
			BroadCastAction.broadcastUpdate(this, "com.ainia.ble.ACTION_BLE_SYNC_SYSTEM_TIME");
			
			break;		
		case R.id.btn_tx:
			intent = new Intent(this,MessageSettingActivity.class); 
		    startActivity(intent);				
			break;			
		default:
			break;
		}
	}
	private void unBidingBle() {		
    	SharedPreferences sharedPreferences= getSharedPreferences("com_my_heartrate",
                Activity.MODE_PRIVATE);  	
         sharedPreferences.edit().clear().commit(); 
         BroadCastAction.broadcastUpdate(this, "com.ainia.ble.ACTION_BLE_UNBUNDLING");
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
		unregisterReceiver(mBroadcastReceiver); 
	}	


	
	
	
	
	
}











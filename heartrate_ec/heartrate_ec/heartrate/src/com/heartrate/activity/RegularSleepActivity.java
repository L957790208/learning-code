package com.heartrate.activity;

import com.heartrate.bluetooth.BLEService;
import com.heartrate.bluetooth.BroadCastAction;
import com.heartrate.utils.Tools;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

public class RegularSleepActivity extends BasicActivity implements OnClickListener, OnCheckedChangeListener{
	private CheckBox mainSwitch, timeSleepSwitch, siestaSwitch,sleepRemindSwitch;
	private boolean mainSwitchTag,timeSleepSwitchTag,siestaSwitchTag,sleepRemindTag;
	
	private int currentTag = 1; // 当前时间标志位
	private TextView startSleepTime, stopSleepTime, sleepRemindTime, startSiesta, stopSiesta;
	private String alarm1Time, alarm2Time, alarm3Time, alarm4Time, alarm5Time; // 闹钟时间		
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 String string = intent.getAction();
			 System.out.println("RegularSleepActivity---BroadcastReceiver");	
			 if (string.equals("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS")) {
				  Toast.makeText(RegularSleepActivity.this, "定时睡眠设置成功", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_regular_sleep);
		initSwitchTag();
		initview();
	}

	private IntentFilter makeIntentFilter() {
		 IntentFilter localIntentFilter = new IntentFilter();
		 localIntentFilter.addAction("com.ainia.ble.RETURN_BLE_DATA");
		 localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS");
		 return localIntentFilter;
	}

	private void initSwitchTag() {
		// TODO Auto-generated method stub
		mainSwitchTag = false;
		timeSleepSwitchTag= false;
		siestaSwitchTag = false;
		sleepRemindTag = false;
	}

	private void initview() {
		// TODO Auto-generated method stub
		mainSwitch = (CheckBox) findViewById(R.id.cb_rs_main_switch);
		timeSleepSwitch = (CheckBox) findViewById(R.id.cb_rs_sleep_switch);
		siestaSwitch = (CheckBox) findViewById(R.id.cb_rs_siesta_switch);
		sleepRemindSwitch = (CheckBox) findViewById(R.id.cb_rs_remind_switch);
		mainSwitch.setOnCheckedChangeListener(this);
		timeSleepSwitch.setOnCheckedChangeListener(this);
		siestaSwitch.setOnCheckedChangeListener(this);
		sleepRemindSwitch.setOnCheckedChangeListener(this);
		
		findViewById(R.id.iv_timing_sleep_back).setOnClickListener(this);
		findViewById(R.id.tv_timing_sleep_save).setOnClickListener(this);	
		
		startSleepTime = (TextView) findViewById(R.id.tv_rs_start_time);
		stopSleepTime = (TextView) findViewById(R.id.tv_rs_stop_time);
		startSiesta = (TextView) findViewById(R.id.tv_rs_start_siesta);
		stopSiesta = (TextView) findViewById(R.id.tv_rs_stop_siesta);
		sleepRemindTime = (TextView) findViewById(R.id.tv_rs_remind);
	
		startSleepTime.setOnClickListener(this);
		stopSleepTime.setOnClickListener(this);		
		startSiesta.setOnClickListener(this);
		stopSiesta.setOnClickListener(this);
		sleepRemindTime.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_timing_sleep_back:
			finish();
			break;
		case R.id.tv_timing_sleep_save:		
			setRegularSleep();
			break;
		case R.id.tv_rs_start_time:	
			currentTag = 1;
			showTimePickDialog(startSleepTime.getText().toString());
			break;
		case R.id.tv_rs_stop_time:		
			currentTag = 2;
			showTimePickDialog(stopSleepTime.getText().toString());			
			break;	
		case R.id.tv_rs_remind:		
			currentTag = 3;
			showTimePickDialog(stopSiesta.getText().toString());			
			break;			
		case R.id.tv_rs_start_siesta:	
			currentTag = 4;
			showTimePickDialog(startSiesta.getText().toString());
			break;
		case R.id.tv_rs_stop_siesta:		
			currentTag = 5;
			showTimePickDialog(stopSiesta.getText().toString());			
			break;			
			
		default:
			break;
		}
	}

	//写入定时睡眠时间
	private void setRegularSleep() {
		// TODO Auto-generated method stub	
		Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_WRITE_REGULAR_SLEEP");
	    localIntent.putExtra("write.regular.sleep", getSettingTime());
	    BroadCastAction.broadcastUpdate(this, 
	   		"com.ainia.ble.ACTION_BLE_WRITE_REGULAR_SLEEP", localIntent);		
	}
	private byte[] getSettingTime() {
		byte[] data ={(byte) 0xbe, 0x01, 0x07,(byte) 0xfe, 0x00,
				(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,
				(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff};		
		if (mainSwitchTag) {
			System.out.println("--总开关--开");
			data[4] = 0x01; //总开关 00关
			if (timeSleepSwitchTag) {
				int startHour = 0, startMinute = 0;
				int stopHour = 0, stopMinute = 0;
				String[] timesStart = new String[] { "00", "00" };
				String[] timesStop = new String[] { "00", "00" };
				
				timesStart = startSleepTime.getText().toString().split(":");	
				timesStop = stopSleepTime.getText().toString().split(":");
				
				startHour = Integer.parseInt(timesStart[0]);
				startMinute = Integer.parseInt(timesStart[1]);
				
				stopHour = Integer.parseInt(timesStop[0]);
				stopMinute = Integer.parseInt(timesStop[1]);
				
				data[5] = (byte) (startHour & 0xff);
				data[6] = (byte) (startMinute & 0xff);
				
				data[9] = (byte) (stopHour & 0xff);				
				data[10] = (byte) (stopMinute & 0xff);		
				System.out.println("--睡觉--startHour--"+startHour +"---startMinute" + startMinute);
				System.out.println("--睡觉--stopHour--"+stopHour +"---stopMinute" + stopMinute);
				
			}
			if (sleepRemindTag) {
				int sleepRemindHour = 0, sleepRemindMinute = 0;
				String[] timesSleepRemind = new String[] { "00", "00" };
				
				timesSleepRemind = sleepRemindTime.getText().toString().split(":");	
				
				sleepRemindHour = Integer.parseInt(timesSleepRemind[0]);
				sleepRemindMinute = Integer.parseInt(timesSleepRemind[1]);
				
				data[7] = (byte) (sleepRemindHour & 0xff);
				data[8] = (byte) (sleepRemindMinute & 0xff);

				System.out.println("--睡眠提醒--sleepRemindHour--"+sleepRemindHour +"---sleepRemindMinute" + sleepRemindMinute);				
			}
			
			if (siestaSwitchTag) {
				int startHour = 0, startMinute = 0;
				int stopHour = 0, stopMinute = 0;
				String[] timesStart = new String[] { "00", "00" };
				String[] timesStop = new String[] { "00", "00" };
				
				timesStart = startSiesta.getText().toString().split(":");	
				timesStop = stopSiesta.getText().toString().split(":");
				
				startHour = Integer.parseInt(timesStart[0]);
				startMinute = Integer.parseInt(timesStart[1]);
				
				stopHour = Integer.parseInt(timesStop[0]);
				stopMinute = Integer.parseInt(timesStop[1]);
				
				data[11] = (byte) (startHour & 0xff);
				data[12] = (byte) (startMinute & 0xff);
				
				data[15] = (byte) (stopHour & 0xff);				
				data[16] = (byte) (stopMinute & 0xff);			
				System.out.println("--午睡--startHour--"+startHour +"---startMinute" + startMinute);
				System.out.println("--午睡--stopHour--"+stopHour +"---stopMinute" + stopMinute);				
			}

		}else {
			System.out.println("--总开关--关");
			return data;
		}
		return data;
				
	}

	private void showTimePickDialog(String timingTime) {
		int hour = 0;
		int minute = 0;
		String[] times = new String[] { "00", "00" };
		times = timingTime.split(":");		
		hour = Integer.valueOf(times[0]);
		minute = Integer.valueOf(times[1]);
		TimePickerDialog dialog = new TimePickerDialog(this,
				R.style.dialog_style_light, new OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {						
						switch (currentTag) {
						case 1:
							alarm1Time = getTime(hourOfDay, minute);
							setTimeString(startSleepTime, alarm1Time);
							break;
						case 2:
							alarm2Time = getTime(hourOfDay, minute);
							setTimeString(stopSleepTime, alarm2Time);
							break;
						case 3:
							alarm3Time = getTime(hourOfDay, minute);
							setTimeString(sleepRemindTime, alarm3Time);
							break;							
						case 4:
							alarm4Time = getTime(hourOfDay, minute);
							setTimeString(startSiesta, alarm4Time);
							break;
						case 5:
							alarm5Time = getTime(hourOfDay, minute);
							setTimeString(stopSiesta, alarm5Time);
							break;

						default:
							break;
						}
					}
				}, hour, minute, true);
		dialog.show();
	}
	
	private String getTime(int hour, int minute) {
		String time = null;
		time = String.format("%02d", hour) + ":"
				+ String.format("%02d", minute);
		return time;
	}	
	
	//根据公英制显示时间
	private void setTimeString(TextView tv,String time){
//		if (this.unit == 0){
//			tv.setText(time);
//		}else{
		tv.setText(time);
		//只显示12小时制		
			//tv.setText(Tools.time24Totime12(time));
//		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.cb_rs_main_switch:
			mainSwitchTag = isChecked;
			break;
		case R.id.cb_rs_sleep_switch:
			timeSleepSwitchTag = isChecked;			
			break;	
		case R.id.cb_rs_remind_switch:
			sleepRemindTag = isChecked;			
			break;			
		case R.id.cb_rs_siesta_switch:
			siestaSwitchTag = isChecked;			
			break;			
		default:
			break;
		}

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





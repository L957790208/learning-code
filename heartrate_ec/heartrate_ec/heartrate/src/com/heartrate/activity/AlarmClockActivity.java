package com.heartrate.activity;

import java.util.Calendar;

import com.heartrate.activity.R.id;
import com.heartrate.bluetooth.BroadCastAction;

import android.R.integer;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

public class AlarmClockActivity extends BasicActivity implements OnClickListener, OnCheckedChangeListener {
	
	private int currentTag = 1; // 当前时间标志位
	private CheckBox cbEat, cbDrinking, cbAlarm ,cbCustom ;
	private ImageView ivBack;
	private TextView tvSave, tvCustom,
	                 tvEat, tvDrinking, tvAlarm, tvCustomTime ;
	
	private boolean eatSwitchTag,drinkingSwitchTag,alarmSwitchTag,customTimeSwitchTag;
	
	private String alarm1Time, alarm2Time, alarm3Time, alarm4Time; // 闹钟时间		
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {									
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 String string = intent.getAction();
			 if (string.equals("")) {
				
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_clock);
		initSwitchTag();
		initView();
		
	}
	
	private void initSwitchTag() {
		// TODO Auto-generated method stub
		eatSwitchTag = false;
		drinkingSwitchTag = false;
		alarmSwitchTag = false;
		customTimeSwitchTag = false;
	}

	private void initView() {
		// TODO Auto-generated method stub
		cbAlarm = (CheckBox) findViewById(R.id.alarm_clock_cb_alarm_switch);
		cbCustom = (CheckBox) findViewById(R.id.alarm_clock_cb_custom_switch);
		cbDrinking = (CheckBox) findViewById(R.id.alarm_clock_cb_drinking_switch);
		cbEat = (CheckBox) findViewById(R.id.alarm_clock_cb_eat_switch);
		
		ivBack = (ImageView) findViewById(R.id.alarm_clock_tv_back);
		tvSave = (TextView) findViewById(R.id.alarm_clock_tv_save);	
		
		
	    tvAlarm = (TextView) findViewById(R.id.alarm_clock_tv_alarm_time);		
		tvCustom = (TextView) findViewById(R.id.alarm_clock_tv_custom);
		tvCustomTime = (TextView) findViewById(R.id.alarm_clock_tv_custom_time);
		tvDrinking = (TextView) findViewById(R.id.alarm_clock_tv_drinking_time);
		tvEat = (TextView) findViewById(R.id.alarm_clock_tv_eat_time);

	    tvSave.setOnClickListener(this);
	    ivBack.setOnClickListener(this);
		tvAlarm.setOnClickListener(this);
		tvAlarm.setOnClickListener(this);
		tvCustom.setOnClickListener(this);
		tvCustomTime.setOnClickListener(this);
		tvDrinking.setOnClickListener(this);
		tvEat.setOnClickListener(this);
		
		cbAlarm.setOnCheckedChangeListener(this);
		cbCustom.setOnCheckedChangeListener(this);
		cbDrinking.setOnCheckedChangeListener(this);
		cbEat.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.alarm_clock_tv_back:
			finish();
			break;
		case R.id.alarm_clock_tv_save:
			Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_WRITE_ALARM_CLOCK");
		    localIntent.putExtra("write.alarm.clock", getSettingTime());
		    BroadCastAction.broadcastUpdate(this, 
		   		"com.ainia.ble.ACTION_BLE_WRITE_ALARM_CLOCK", localIntent);	
			break;
		case R.id.alarm_clock_tv_custom:
			
			break;			
		case R.id.alarm_clock_tv_eat_time:
			currentTag = 1;
			showTimePickDialog(tvEat.getText().toString());			
			break;		
		case R.id.alarm_clock_tv_drinking_time:
			currentTag = 2;
			showTimePickDialog(tvDrinking.getText().toString());				
			break;			
		case R.id.alarm_clock_tv_alarm_time:
			currentTag = 3;
			showTimePickDialog(tvAlarm.getText().toString());				
			break;
		case R.id.alarm_clock_tv_custom_time:
			currentTag = 4;
			showTimePickDialog(tvCustomTime.getText().toString());				
			break;		
		default:
			break;
		}
	}	
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.alarm_clock_cb_eat_switch:
			eatSwitchTag = isChecked;
			break;
		case R.id.alarm_clock_cb_drinking_switch:
			drinkingSwitchTag = isChecked;			
			break;	
		case R.id.alarm_clock_cb_alarm_switch:
			alarmSwitchTag = isChecked;			
			break;			
		case R.id.alarm_clock_cb_custom_switch:
			customTimeSwitchTag = isChecked;			
			break;			
		default:
			break;
		}
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
							setTimeString(tvEat, alarm1Time);
							break;
						case 2:
							alarm2Time = getTime(hourOfDay, minute);
							setTimeString(tvDrinking, alarm2Time);
							break;
						case 3:
							alarm3Time = getTime(hourOfDay, minute);
							setTimeString(tvAlarm, alarm3Time);
							break;							
						case 4:
							alarm4Time = getTime(hourOfDay, minute);
							setTimeString(tvCustomTime, alarm4Time);
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
	
	private byte[] getSettingTime() {
		Calendar mCalendar=Calendar.getInstance();
		int mYear = mCalendar.get(Calendar.YEAR) % 2000 ;
		int mMonth = mCalendar.get(Calendar.MONTH) + 1 ;//Calendar的月份是从 0开始算的， 所以要+1
		int mDate = mCalendar.get(Calendar.DATE) ;
		//alarmNo用于标志闹钟编号，weekTag星期重复
		int alarmNo = 0, weekTag = 0;		
		byte[] data ={ (byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,
				       (byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,
				       (byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,
				       (byte) 0xff,(byte) 0xff};				
			if (eatSwitchTag) {
				alarmNo = 1;
				int alarmHour = 0, alarmMinute = 0;		
				String[] alarmTimes = new String[] { "00", "00" };				
				alarmTimes = tvEat.getText().toString().split(":");					
				alarmHour = Integer.parseInt(alarmTimes[0]);
				alarmMinute = Integer.parseInt(alarmTimes[1]);					
				data[0] = (byte) ((mYear <<2 ) | (mMonth >> 2));
			    data[1] = (byte) (((mMonth << 6) & 0xc0) | (mDate << 1) | (alarmHour >> 4));
			    data[2] = (byte) (((alarmHour << 4) & 0xf0) | (alarmMinute >> 2 ));
			    data[3] = (byte) (((alarmMinute << 6) & 0xc0) | (alarmNo << 3));
				data[4] = (byte) (weekTag & 0xff); 					
			}
			if (drinkingSwitchTag) {
				alarmNo = 2;
				int alarmHour = 0, alarmMinute = 0;		
				String[] alarmTimes = new String[] { "00", "00" };				
				alarmTimes = tvDrinking.getText().toString().split(":");					
				alarmHour = Integer.parseInt(alarmTimes[0]);
				alarmMinute = Integer.parseInt(alarmTimes[1]);					
				data[5] = (byte) ((mYear <<2 ) | (mMonth >> 2));
			    data[6] = (byte) (((mMonth << 6) & 0xc0) | (mDate << 1) | (alarmHour >> 4));
			    data[7] = (byte) (((alarmHour << 4) & 0xf0) | (alarmMinute >> 2 ));
			    data[8] = (byte) (((alarmMinute << 6) & 0xc0) | (alarmNo << 3));
				data[9] = (byte) (weekTag & 0xff); 			
			}
			if (alarmSwitchTag) {
				alarmNo = 3;
				int alarmHour = 0, alarmMinute = 0;		
				String[] alarmTimes = new String[] { "00", "00" };				
				alarmTimes = tvAlarm.getText().toString().split(":");					
				alarmHour = Integer.parseInt(alarmTimes[0]);
				alarmMinute = Integer.parseInt(alarmTimes[1]);					
				data[10] = (byte) ((mYear <<2 ) | (mMonth >> 2));
			    data[11] = (byte) (((mMonth << 6) & 0xc0) | (mDate << 1) | (alarmHour >> 4));
			    data[12] = (byte) (((alarmHour << 4) & 0xf0) | (alarmMinute >> 2 ));
			    data[13] = (byte) (((alarmMinute << 6) & 0xc0) | (alarmNo << 3));
				data[14] = (byte) (weekTag & 0xff); 			
			}
			if (customTimeSwitchTag) {
				alarmNo = 4;
				int alarmHour = 0, alarmMinute = 0;		
				String[] alarmTimes = new String[] { "00", "00" };				
				alarmTimes = tvCustomTime.getText().toString().split(":");					
				alarmHour = Integer.parseInt(alarmTimes[0]);
				alarmMinute = Integer.parseInt(alarmTimes[1]);					
				data[15] = (byte) ((mYear <<2 ) | (mMonth >> 2));
			    data[16] = (byte) (((mMonth << 6) & 0xc0) | (mDate << 1) | (alarmHour >> 4));
			    data[17] = (byte) (((alarmHour << 4) & 0xf0) | (alarmMinute >> 2 ));
			    data[18] = (byte) (((alarmMinute << 6) & 0xc0) | (alarmNo << 3));
				data[19] = (byte) (weekTag & 0xff); 	
			}
		return data;				
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub		
		super.onStart();
		registerReceiver(mBroadcastReceiver, makeIntentFilter());
	}

	private IntentFilter makeIntentFilter() {
		// TODO Auto-generated method stub
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("com.ainia.ble.RETURN_BLE_DATA");
		localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS");
		return localIntentFilter;
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(mBroadcastReceiver);
	}
	
}















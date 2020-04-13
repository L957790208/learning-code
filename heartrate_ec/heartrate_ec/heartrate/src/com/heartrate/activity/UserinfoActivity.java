package com.heartrate.activity;

import com.heartrate.bluetooth.BroadCastAction;
import com.heartrate.my.UserBodyInformation;
import com.heartrate.utils.PreferenceSaveClassUtils;
import com.heartrate.view.CustomNumberPicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker.OnValueChangeListener;

public class UserinfoActivity extends Activity implements OnClickListener{

	private String USERINFOSTR = "user_info"; 
	private EditText etNickname, etAge, etStepLength, etHeight, 
					 etWeight;
	private TextView tvSave, tvUpdateInfo , etSedentaryTime;
	private RadioGroup rgGender;
	private RadioButton rbMAn,rbLady;
	private UserBodyInformation mUserInfo;
	private ImageView ivBack;
	private RelativeLayout rlSedentaryTimeBtn;
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 String string = intent.getAction();
			 System.out.println("UserinfoActivity---BroadcastReceiver");	
			if (string.equals("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS")) {
				 Toast.makeText(UserinfoActivity.this, "信息设置成功", Toast.LENGTH_SHORT).show();
				 return ;
			}
			 if (string.equals("com.ainia.ble.RETURN_BLE_DATA")) {
				 intent.getSerializableExtra("return_ble_data");
				 Toast.makeText(UserinfoActivity.this, "信息接收成功", Toast.LENGTH_SHORT).show();
		    	 byte [] data = (byte[]) intent.getSerializableExtra("return_ble_data");
		    	//更新ui
		    	 updatUserUI(data);		    	 		    	 
			}
		}

	};
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);
		Window window = this.getWindow();
		//取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); 

		//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); 
		//设置状态栏颜色
		window.setStatusBarColor(getResources().getColor(R.color.steelblue));				
		
		mUserInfo = new UserBodyInformation();
		intiView();
		initShowData();
		//showDialog();

	}   


	private void getDialogVal(){

        // TODO Auto-generated method stub
		
        NumberPicker mPicker = new NumberPicker(UserinfoActivity.this);
        mPicker.setMinValue(0);
        mPicker.setMaxValue(60);
        
        mPicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				// TODO Auto-generated method stub
				etSedentaryTime.setText(newVal + "");
			}
        	
        });      
        AlertDialog mAlertDialog = new AlertDialog.Builder(UserinfoActivity.this)
        .setTitle("NumberPicker").setView(mPicker).setPositiveButton("确定",null).create();
        mAlertDialog.show();
    		
	}


	private void intiView() {
		// TODO Auto-generated method stub
		tvSave = (TextView) findViewById(R.id.tv_userinfo_save);
		ivBack = (ImageView) findViewById(R.id.iv_back);
		tvUpdateInfo = (TextView) findViewById(R.id.tv_update_userinfo);
		etNickname = (EditText) findViewById(R.id.et_user_nickname);
		rgGender = (RadioGroup) findViewById(R.id.rg_user_sex);	
		etAge = (EditText) findViewById(R.id.et_user_age);
		etStepLength = (EditText) findViewById(R.id.et_user_step_length);
		etHeight = (EditText) findViewById(R.id.et_user_height);		
		etWeight = (EditText) findViewById(R.id.et_user_weight);
		etSedentaryTime	= (TextView) findViewById(R.id.et_user_sedentary_time);
		rlSedentaryTimeBtn = (RelativeLayout) findViewById(R.id.rl_user_sedentary_time);
		rbMAn = (RadioButton) findViewById(R.id.rb_user_man_radio);
		rbLady = (RadioButton) findViewById(R.id.rb_user_lady_radio);
		
		tvSave.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		tvUpdateInfo.setOnClickListener(this);
		rlSedentaryTimeBtn.setOnClickListener(this);
	}
	

	private void initShowData() {
		if ((UserBodyInformation) PreferenceSaveClassUtils.readObject(this, USERINFOSTR) == null) {
			System.out.println("-----mUserInfo == null------");
		}else {
			mUserInfo = (UserBodyInformation) PreferenceSaveClassUtils.readObject(this, USERINFOSTR);	
			//更新UI
			updatUserUI(mUserInfo.getByteArray());
		}
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
		switch (v.getId()) {
		case R.id.tv_userinfo_save:
			saveUserinfo();
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_update_userinfo:
			updateUserInfo();
			break;	
		case R.id.rl_user_sedentary_time:
			getDialogVal();
			break;			
		default:
			break;
		}
	}

	private void updateUserInfo() {
		// TODO Auto-generated method stub
	    BroadCastAction.broadcastUpdate(this, "com.ainia.ble.ACTION_BLE_READ_USER_INFO");	
	}

	private void saveUserinfo() {
		// TODO Auto-generated method stub
		UserBodyInformation svaeUserInfo = new UserBodyInformation();
		System.out.println("保存用户信息");
		byte[] data = null;
		svaeUserInfo.setAge(Integer.parseInt(etAge.getText().toString()));
		if(rgGender.getCheckedRadioButtonId()==R.id.rb_user_man_radio)
			mUserInfo.setGender(0);
		else {
			mUserInfo.setGender(1);
		}				
		svaeUserInfo.setHeight(Integer.parseInt(etHeight.getText().toString()));
		//mUserInfo.setNickname(etNickname.getText().toString());
		svaeUserInfo.setSedentaryTime(Integer.parseInt(etSedentaryTime.getText().toString()));
		svaeUserInfo.setStepLength(Integer.parseInt(etStepLength.getText().toString()));
		svaeUserInfo.setWeight(Integer.parseInt(etWeight.getText().toString()));
		data = svaeUserInfo.getByteArray();
		Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_WIRTE_USER_INFO");
	    //localIntent.putExtra("read_ble_uuid", "1523");
		localIntent.putExtra("wirte_ble_info", data);
		
    	//把用户信息存入SharedPreferences
		saveUserInfPreference(svaeUserInfo);
		
	    BroadCastAction.broadcastUpdate(this, 
	    		"com.ainia.ble.ACTION_BLE_WIRTE_USER_INFO", localIntent);	
	}
	
	private void updatUserUI(byte[] info) {
		// TODO Auto-generated method stub
		mUserInfo.setUserBodyInformation(info);
		etAge.setText(mUserInfo.getAge() + "");
		if (mUserInfo.getGender() == 1) {
			rbLady.setChecked(true);
			rbMAn.setChecked(false);
		}
		if (mUserInfo.getGender() == 0) {
			rbLady.setChecked(false);
			rbMAn.setChecked(true);
		}		
		etHeight.setText(mUserInfo.getHeight() + "");
		etNickname.setText(mUserInfo.getNickname() + "");
		etSedentaryTime.setText(mUserInfo.getSedentaryTime() + "");
		etStepLength.setText(mUserInfo.getStepLength() + "");
		etWeight.setText(mUserInfo.getWeight() + "");
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

	public void saveUserInfPreference(UserBodyInformation info){	
		//把用户信息存入SharedPreferences
		if (info != null) {
			PreferenceSaveClassUtils.saveObject(this, USERINFOSTR, info);	
		}
			
	}		
	
}






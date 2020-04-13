package com.heartrate.fragment;

import junit.framework.Test;

import com.heartrate.activity.R;
import com.heartrate.bluetooth.BroadCastAction;
import com.heartrate.my.MeasurementData;
import com.heartrate.my.UserBodyInformation;
import com.heartrate.utils.PreferenceSaveClassUtils;
import com.heartrate.view.StepArc;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class StepCounterFragment extends BaseFragment {
	
	private String USERSTEP = "user_step_data";
	private String TARGETSTEP = "target_step_data";
	private Context context;
	private View mView;								
	private StepArc mHeartrateArc;
	private TextView tvKinestate, tvSpeed, tvTargetStep, tvTotalDistance, tvTotalCalories,
					 tvMovementTime, tvDeepSleepTime, tvLightSleepTime, tvBreakTime,
					 tvConnentState;	
	private MeasurementData mUserData;
	private UserBodyInformation userBodyInformation;
	
	int targetStep;
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
    		mUserData = (MeasurementData) msg.obj;   	
    		
    		updatStepUI(mUserData); 		
        }	
	};
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
		  public void onReceive(Context context, Intent intent){		    	
		     String str = intent.getAction();
		     System.out.println("StepCounterFragment---BroadcastReceiver");	
		     if (str.equals("com.ainia.ble.RETURN_BLE_DATA")) {
		    	 intent.getSerializableExtra("return_ble_data");	    	 
		    	 MeasurementData data = new MeasurementData();
		    	 data.setMeasurementData((byte[]) intent.getSerializableExtra("return_ble_data"));
		    	 //更新ui
		    	 saveStepData(data);
		    	 updatStepUI(data);	
		     }	
		     if (str.equals("com.ainia.ble.RETURN_BLE_CONNECT_OK")) {
		    	 tvConnentState.setText("已经连接设备");
		    	 mBroadcastUpdate(1);	    
		     }	
	         if (str.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
	        	 tvConnentState.setText("没有连接设备");
	        	 
			 }				     
		  }
 		    
	};	  	  
	private IntentFilter makeIntentFilter() {
		   IntentFilter localIntentFilter = new IntentFilter();
		   localIntentFilter.addAction("com.ainia.ble.RETURN_BLE_DATA");
		   localIntentFilter.addAction("com.ainia.ble.RETURN_BLE_CONNECT_OK");
		   localIntentFilter.addAction("com.ainia.ble.DISCONNECTED");//蓝牙连接断开
		   localIntentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");//蓝牙连接    		   
		   return localIntentFilter;
    }	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		mView=inflater.inflate(R.layout.fragment_step_counter, null);	
		//http://blog.csdn.net/suyan_why/article/details/52574197
		//调用getActivity()方法会报空指针错误
		this.context = getActivity();
		this.context.registerReceiver(this.mBroadcastReceiver, makeIntentFilter());		

		mFindView();
		initData();	
		return mView;
	}
		

/*	 public void onAttach(Context paramContext){
		 System.out.println("------onAttach-------");
		 super.onAttach(getActivity());
		 System.out.println("------onAttach-------");
	    this.context = paramContext;
	   // this.sharedPreferences = paramContext.getSharedPreferences("demo", 0);
	   // this.editor = this.sharedPreferences.edit();
	  }*/
	 

	public void onDestroyView()
	  {
	    this.context.unregisterReceiver(this.mBroadcastReceiver);
	    super.onDestroyView();
	  }	
	
	private void initData() {
		// TODO Auto-generated method stub
		targetStep = 10000;
		userBodyInformation = new UserBodyInformation(); 
		userBodyInformation.setTargetStep(targetStep);
		//mUserData = new MeasurementData();
		if (PreferenceSaveClassUtils.readObject(context, USERSTEP ) != null) {
			mUserData = (MeasurementData) PreferenceSaveClassUtils.readObject(context, USERSTEP );
			if (mUserData != null) {
				updatStepUI(mUserData);
			}
		}
	}
    private void mFindView(){   
    	mHeartrateArc = (StepArc) mView.findViewById(R.id.arc);
    	tvTargetStep = (TextView) mView.findViewById(R.id.step_tv_target_step);
    	tvConnentState = (TextView) mView.findViewById(R.id.tv_step_connent_state);
    	tvKinestate  = (TextView) mView.findViewById(R.id.tv_step_kinestate);
    	//tvSpeed = (TextView) mView.findViewById(R.id.tv);
    	tvTotalDistance = (TextView) mView.findViewById(R.id.tv_total_distance); 
    	tvTotalCalories = (TextView) mView.findViewById(R.id.tv_total_calories);
		tvMovementTime = (TextView) mView.findViewById(R.id.tv_movement_time);
		tvDeepSleepTime = (TextView) mView.findViewById(R.id.tv_deep_sleep);
		tvLightSleepTime = (TextView) mView.findViewById(R.id.tv_light_sleep);
		tvBreakTime = (TextView) mView.findViewById(R.id.tv_break_time); 	
    	mHeartrateArc.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				  
				Toast.makeText(getActivity(), "等待刷新", Toast.LENGTH_SHORT).show();	
				mBroadcastUpdate(1);						
			}
		});
    }			
	//更新ui数据
	private void updatStepUI(MeasurementData mUserData) {
		// TODO Auto-generated method stub
		//完成数
		int totalStep = mUserData.getTotalStep();
		float percentage = 0 ;
		percentage = (float)totalStep / (float)targetStep ;
		mHeartrateArc.updateData(totalStep, (int)(percentage*100));		
		tvTargetStep.setText(userBodyInformation.getTargetStep() + "");
		tvKinestate.setText(mUserData.getKinestate() + "");
		tvTotalDistance.setText(mUserData.getTotalDistance() + "m");
    	tvTotalCalories.setText(mUserData.getTotalCalories() + "kcal");	
		tvMovementTime.setText(mUserData.getMovementTime() /60  + "h " + mUserData.getMovementTime() %60 + "min");
		tvDeepSleepTime.setText(mUserData.getDeepSleepTime() /60 + "h " + mUserData.getDeepSleepTime() % 60 + "min");
		tvLightSleepTime.setText(mUserData.getLightSleepTime() / 60 + "h " + mUserData.getLightSleepTime() % 60 + "min");
		tvBreakTime.setText(mUserData.getBreakTime() / 60 + "h " + mUserData.getBreakTime() % 60 + "min");						
	}
	
	private void mBroadcastUpdate(int flag){
		if (flag ==1 ) {
			//读请求
		//	BroadCastAction.broadcastUpdate(this.context, "com.ainia.ble.ACTION_BLE_READ_DATA");
			//读2a53的属性
			Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_READ_DATA");
		    localIntent.putExtra("read_ble_uuid", "2a53");
		    BroadCastAction.broadcastUpdate(this.context, 
		    		"com.ainia.ble.ACTION_BLE_READ_DATA", localIntent);	
		    return ;
		}

		if (flag ==2 ) {
			BroadCastAction.broadcastUpdate(this.context, "com.ainia.ble.ACTION_BLE_SEND_DATA");
			/*Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_SEND_DATA");
		    localIntent.putExtra("ask_flag", "2");
		    BroadCastAction.broadcastUpdate(this.context, 
		    		"com.ainia.ble.ACTION_BLE_SEND_DATA", localIntent);	*/
		    return ;
		}		
	}
	
    public MeasurementData readPreference(){   	
    	MeasurementData measurementData;
    	measurementData = (MeasurementData) PreferenceSaveClassUtils.readObject(context, USERSTEP );
    	return measurementData;
    }
	public void saveStepData(MeasurementData measurementData){
		if (measurementData != null) {				
			PreferenceSaveClassUtils.saveObject(context, USERSTEP, measurementData);				
	    }	
	}	
	
}










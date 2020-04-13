package com.heartrate.fragment;

import java.util.ArrayList;
import com.heartrate.activity.R;
import com.heartrate.bluetooth.BroadCastAction;
import com.heartrate.tools.SaveHeartrateData;
import com.heartrate.view.HeartRateArc;
import com.heartrate.view.SimpleLineChart;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class HeartRateFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {
	private Context context;
	private View mView;		
	private HeartRateArc mHeartRateArc;
	private CheckBox btnON;
	private SimpleLineChart mSimpleLineChart;
	private ArrayList<Integer> pointArray;
	private SaveHeartrateData saveHeartrateData;
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 String string = intent.getAction();
			 System.out.println("DeviceListActivityS---BroadcastReceiver");	
			 if (string.equals("com.ainia.ble.RETURN_BLE_DATA")) {
				intent.getSerializableExtra("return_ble_data");
		    	 byte [] data = (byte[]) intent.getSerializableExtra("return_ble_data");
		    	//¸üÐÂui
		    	 updatStepUI(data[16] & 0xFF);	    	 
		         pointArray.add(data[16] & 0xFF);
		         mSimpleLineChart.updateData(data[16] & 0xFF);		        
		       //  saveHeartrateData.addDatabase(data[16] & 0xFF);
			}
		}
		
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mView=inflater.inflate(R.layout.fragment_heartrate, null);	
		
		this.context = getActivity();
		this.context.registerReceiver(this.mBroadcastReceiver, makeIntentFilter());
		
		mHeartRateArc = (HeartRateArc) mView.findViewById(R.id.heart_rate_arc);
		btnON = (CheckBox) mView.findViewById(R.id.on);
		btnON.setOnCheckedChangeListener(this);
		pointArray = new ArrayList<Integer>();
		initViewLineChart();
		saveHeartrateData = new SaveHeartrateData(getActivity());
		return mView;
	}
     
	 private void initViewLineChart(){
        mSimpleLineChart = (SimpleLineChart) mView.findViewById(R.id.hr_view_simpleLineChart);
        String[] xItem = {"1","2","3","4","5","6","7","8","9","10"};
        String[] yItem = {"10k","20k","30k","40k","50k"};
      //  if(mSimpleLineChart == null)
           // Log.e("wing","null!!!!");
      //  mSimpleLineChart.setXItem(xItem);
      //  mSimpleLineChart.setYItem(yItem);
       
        //int a = (int) (Math.random()*5);
       // for(int i = 0;i < 5;i++){
       // 	//pointArray.add((int) (Math.random()*120));
       // 	pointArray.add(85);
      //  }
/*        pointArray.add(40);
        pointArray.add(50);
        pointArray.add(60);
        pointArray.add(80);
        pointArray.add(100);
        pointArray.add(120);
        pointArray.add(140);
        pointArray.add(150);
        pointArray.add(160);
        pointArray.add(180); */   
        pointArray.add(0);
        mSimpleLineChart.setData(pointArray);
    }		
	
	
	private IntentFilter makeIntentFilter() {
		 IntentFilter localIntentFilter = new IntentFilter();
		 localIntentFilter.addAction("com.ainia.ble.RETURN_BLE_DATA");
		 return localIntentFilter;
	}

	private void updatStepUI(int heartrate) {
		// TODO Auto-generated method stub
		mHeartRateArc.updateData(heartrate);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		}
	    return ;
	}	
	private void mBroadcastUpdate(boolean enable){
		Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_NOTIFICATION");
	    localIntent.putExtra("ble_notification_uuid", "2a53");
	    localIntent.putExtra("ble_notification_enale", enable);
	    BroadCastAction.broadcastUpdate(this.context, 
	    		"com.ainia.ble.ACTION_BLE_NOTIFICATION", localIntent);	
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.on:
			if (isChecked) {
				mBroadcastUpdate(true);		      
		       // saveHeartrateData.createDatabase();
			}else {
				mBroadcastUpdate(false);
				//saveHeartrateData.close();
			}
			break;			
		default:
			break;
		}		
	}
	public void onDestroyView()
	  {
		//saveHeartrateData.close();
	    this.context.unregisterReceiver(this.mBroadcastReceiver);
	    super.onDestroyView();
	  }		
}









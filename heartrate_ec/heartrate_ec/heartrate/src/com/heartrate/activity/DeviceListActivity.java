package com.heartrate.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.heartrate.bluetooth.BroadCastAction;

public class DeviceListActivity extends Activity implements OnClickListener{
  private String TAG = "--DeviceListActivity--";
  private Context appContext;
  private Button btnBack,btnStart;
  private ListView lv_device_list;
  private ProgressBar pb_scan_progress;
  private String mAddString;
  
  private DeviceAdapter adapter; 
  
  protected void onCreate(Bundle paramBundle){
	    super.onCreate(paramBundle);
	    requestWindowFeature(1);
	    setContentView(R.layout.activity_connect);
	    this.appContext = getApplicationContext(); 
	  
	    this.lv_device_list = ((ListView)findViewById(R.id.listView));
	    this.btnBack = ((Button)findViewById(R.id.btn_back));
	    this.btnStart = ((Button)findViewById(R.id.btn_start));
	    //this.pb_scan_progress = ((ProgressBar)findViewById(R.id.progressBar1));
	    //this.pb_scan_progress.setVisibility(0);
	    this.btnBack.setOnClickListener(this);
	    this.btnStart.setOnClickListener(this);
	    this.lv_device_list = (ListView)findViewById(R.id.listView);
	    //adapter = new ArrayAdapter<String>(this, R.layout.item, new ArrayList<String>());
	    adapter = new DeviceAdapter(this);
	    this.lv_device_list.setAdapter(adapter);
	    this.lv_device_list.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				System.out.println("------------------" + position);
				TextView mtv = (TextView) view.findViewById(R.id.tv_device_mac);
				String item = mtv.getText().toString();
				mAddString = item;
				Intent localIntent = new Intent("com.ainia.ble.RETURN_DEVICE_ITEM");
			    localIntent.putExtra("device_item_address", item);			  
			    BroadCastAction.broadcastUpdate(DeviceListActivity.this.appContext, 
			    		"com.ainia.ble.RETURN_DEVICE_ITEM", localIntent);					
			}
		});
	    
	   }  
  
  private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      System.out.println(TAG+"---BroadcastReceiver");	
      String str = paramAnonymousIntent.getAction();
      //搜索到的device
      if (str.equals("com.ainia.ble.RETURN_BLE_SCAN_DEVICE")){
    	  
    	  String strAddress = (String) paramAnonymousIntent.getSerializableExtra("com.ainia.ble.DEVICE_ADDRESS");
    	//  String strName = (String) paramAnonymousIntent.getSerializableExtra("com.ainia.ble.DEVICE_NAME");    	  
    	 // System.out.println(TAG+strAddress); 		  
   	  
    	  //adapter.add(strAddress);
    	  adapter.addDevice(paramAnonymousIntent);
    	  adapter.notifyDataSetChanged(); 
    	  
      }
      //收到连接成功
      if (str.equals("com.ainia.ble.RETURN_BLE_CONNECT_OK")) {
    	  System.out.println(TAG + "---connent--ok");
    	  //存起来
    	  saveAddress(mAddString);
    	  DeviceListActivity.this.finish();
		
	}
      if (str.equals("com.ainia.ble.RETURN_BLE_SCAN_OVER")) {
      }
      
    } 
    
  };

 
  private static IntentFilter makeIntentFilter(){
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("com.ainia.ble.RETURN_BLE_SCAN_DEVICE");
    localIntentFilter.addAction("com.ainia.ble.RETURN_BLE_SCAN_OVER");
    localIntentFilter.addAction("com.ainia.ble.RETURN_BLE_CONNECT_OK");
    return localIntentFilter;
  }

/*  public void addItem(Intent paramIntent){
    if (paramIntent != null)
    {
      this.adapter.addDevice(paramIntent);
      this.adapter.notifyDataSetChanged();
    }
  } */
  
  protected void onPause() {
    super.onPause();
    unregisterReceiver(this.mBroadcastReceiver);
  }

  protected void onResume() {
    super.onResume();
    registerReceiver(this.mBroadcastReceiver, makeIntentFilter());
  }

  private class DeviceAdapter extends BaseAdapter{
    public ArrayList<Intent> deviceIntent;
    private LayoutInflater inflater;

    public DeviceAdapter(Context localContext){
      this.inflater = LayoutInflater.from(localContext);
      this.deviceIntent = new ArrayList<Intent>();
    }
 
    public void addDevice(Intent paramIntent){	
      Iterator<Intent> localIterator = this.deviceIntent.iterator();
      Intent localIntent;
      do{
        if (!localIterator.hasNext()) {
          this.deviceIntent.add(paramIntent);
          return;
        }
        localIntent = (Intent)localIterator.next();
      }
      while (!localIntent.getStringExtra("com.ainia.ble.DEVICE_ADDRESS").equals(paramIntent.getStringExtra("com.ainia.ble.DEVICE_ADDRESS")));
     // localIntent.putExtra("com.ainia.ble.DEVICE_RSSI", paramIntent.getStringExtra("com.ainia.ble.DEVICE_RSSI"));
    }

    public int getCount(){
      return this.deviceIntent.size();
    }

    public Object getItem(int paramInt){
      return this.deviceIntent.get(paramInt);
    }

    public long getItemId(int paramInt) {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
      if (paramView == null)
        paramView = this.inflater.inflate(R.layout.common_device_list, null);
        TextView localTextView1 = (TextView)paramView.findViewById(R.id.tv_device_name);
        TextView localTextView2 = (TextView)paramView.findViewById(R.id.tv_device_mac);
        TextView localTextView3 = (TextView)paramView.findViewById(R.id.tv_device_signal);
        Intent localIntent = (Intent)this.deviceIntent.get(paramInt);
        localTextView1.setText(localIntent.getStringExtra("com.ainia.ble.DEVICE_NAME"));
        localTextView2.setText(localIntent.getStringExtra("com.ainia.ble.DEVICE_ADDRESS"));
        localTextView3.setText(localIntent.getIntExtra("com.ainia.ble.DEVICE_RSSI",0) + "");
        
        return paramView;
      }
   }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	  	switch (v.getId()) {
	  	
		case R.id.btn_start:
		       BroadCastAction.broadcastUpdate(this.appContext, "com.ainia.ble.ACTION_BLE_START_SCAN");			
			break;
			
		case R.id.btn_back:
			BroadCastAction.broadcastUpdate(this.appContext, "com.ainia.ble.ACTION_BLE_STOP_SCAN");
			DeviceListActivity.this.finish();
			break;
			
		default:
			break;
		}
	}

	public static void startScan(ScanCallback callback){
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if(null == adapter){
			return;
		}
		BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
		if(null == scanner){
			return;
		}
		scanner.startScan(callback);
	}	
	
	public void saveAddress(String addressString){
		   SharedPreferences sharedPreferences= getSharedPreferences("com_my_heartrate",
	                Activity.MODE_PRIVATE);
	        //实例化SharedPreferences.Editor对象
	        SharedPreferences.Editor editor = sharedPreferences.edit();
	        //用putString的方法保存数据
	        editor.putString("bluetooth_address", addressString);
	        //提交当前数据
	        editor.apply();
	      }	
    }









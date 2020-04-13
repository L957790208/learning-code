package com.heartrate.activity;

import java.util.ArrayList;
import java.util.UUID;
import com.heartrate.bluetooth.BroadCastAction;
import com.heartrate.bluetooth.Profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class DebugActivity extends Activity implements OnClickListener{
	
	private TextView receiveText ;
    private int readAlluuidTag = 1 ;
    private ArrayList<String> uuids ;
    
	Handler mHandler = new Handler(){ 
		@Override public void handleMessage(Message msg) {
			if (msg.what == 1) {
				readuuid();
			}
		}


	}; 
	
	private void readuuid() {
		// TODO Auto-generated method stub
		readAlluuidTag++;
		if (readAlluuidTag > 8) {
			
			return ; 
		}
		Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_READ_DATA");
	    localIntent.putExtra("read_ble_uuid", uuids.get(readAlluuidTag));		
		BroadCastAction.broadcastUpdate(this,"com.ainia.ble.ACTION_BLE_READ_DATA", localIntent);
	}	
	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String string = intent.getAction();
			if (string.equals("com.ainia.ble.RETURN_BLE_DATA")) {
				
				 intent.getSerializableExtra("return_ble_data");
		    	 System.out.println(""+intent.getSerializableExtra("return_ble_data"));
		    	 byte [] data = (byte[]) intent.getSerializableExtra("return_ble_data");
		    	 String str;
		    	 str = byteToString(data);
/*		    	 
		    	 for (int i = 0; i < data.length; i++) {
		    		 receiveText.setText(receiveText.getText() + "_"+ data[i]);			    		 
				}*/
		    	 receiveText.setText(receiveText.getText() + uuids.get(readAlluuidTag));
		    	 receiveText.append("\n");
		    	 receiveText.setText(receiveText.getText() + str);
		    	 receiveText.append("\n");
		    	 receiveText.append("\n");
				 Message message = new Message();
				 message.what = 1;
				 mHandler.sendMessage(message);
				 return ;			
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		findViewById(R.id.btn_debug_start1).setOnClickListener(this);
		receiveText = (TextView) findViewById(R.id.tv_debug_receive_text1);
		registerReceiver(mBroadcastReceiver, makeIntentFilter());
		uuids = new ArrayList<String>();
		uuids.add(0, "0000");
		uuids.add(1, "0000");
		uuids.add(2, "0000");
		uuids.add(3, "0000");	
		uuids.add(4, "0000");	
		uuids.add(5, "0000");	
		///uuids.add(6, "1528");只能写
		uuids.add(6, "0000");	
		uuids.add(7, "0000");	
		uuids.add(8, "0000");		
		
		
	}

	private IntentFilter makeIntentFilter() {
		// TODO Auto-generated method stub
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("com.ainia.ble.RETURN_BLE_DATA");
		localIntentFilter.addAction("com.ainia.ble.ACTION_BLE_WRITE_SUCCESS");
		return localIntentFilter;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		System.out.println("11111");
		readAlluuidTag = 1;
		receiveText.setText("");
		Intent localIntent = new Intent("com.ainia.ble.ACTION_BLE_READ_DATA");
	    localIntent.putExtra("read_ble_uuid", uuids.get(readAlluuidTag));		
		BroadCastAction.broadcastUpdate(this,"com.ainia.ble.ACTION_BLE_READ_DATA", localIntent);		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	//将byte数组转16进制
	@SuppressLint("DefaultLocale")
	public static String byteToString( byte[] b) {  
		String str = new String();
		   for (int i = b.length - 1; i >= 0; i--) { 
		     String hex = Integer.toHexString(b[i] & 0xFF); 
		     if (hex.length() == 1) { 
		       hex = '0' + hex; 
		     } 
		     str = hex .toUpperCase() + "  " + str;
		   }
		     return str; 
		}
	
}

package com.heartrate.fragment;

import com.heartrate.activity.DebugActivity;
import com.heartrate.activity.DeviceListActivity;
import com.heartrate.activity.DeviceSettingActivity;
import com.heartrate.activity.R;
import com.heartrate.activity.UserinfoActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class UserFragment extends BaseFragment implements OnClickListener{
	
	private View mView;
	private RelativeLayout rUser,rDe,rPair,rAbout;
	
	private BluetoothManager bluetoothManager = null;
	private BluetoothAdapter mbBluetoothAdapter = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		mView = inflater.inflate(R.layout.fragment_user, null);
		intiView();
		
		return mView;
	}  

	private void intiView() {
		// TODO Auto-generated method stub
		rUser = (RelativeLayout) mView.findViewById(R.id.rl_user);
		rDe = (RelativeLayout) mView.findViewById(R.id.rl_ble_device);
		rPair = (RelativeLayout) mView.findViewById(R.id.rl_pair);
		rAbout = (RelativeLayout) mView.findViewById(R.id.rl_about);
		
		rUser.setOnClickListener(this);
		rDe.setOnClickListener(this);
		rPair.setOnClickListener(this);
		rAbout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.rl_user:
			intent = new Intent(getActivity(),UserinfoActivity.class); 
		    startActivity(intent);
			break;
		case R.id.rl_ble_device:
			intent = new Intent(getActivity(),DeviceSettingActivity.class); 
		    startActivity(intent);
			break;
		case R.id.rl_pair:
			openBluetooth(getActivity());		
			break;
		case R.id.rl_about:
			intent = new Intent(getActivity(),DebugActivity.class); 
		    startActivity(intent);
			break;			
		default:
			break;
		}
	}

	private void openBluetooth(Context context) {
		// TODO Auto-generated method stub
		bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		mbBluetoothAdapter = bluetoothManager.getAdapter();
		//ÅÐ¶ÏÀ¶ÑÀÊÇ·ñ¿ªÆô
		if (mbBluetoothAdapter == null || !mbBluetoothAdapter.isEnabled()) {
			//´ò¿ªÀ¶ÑÀ
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			context.startActivity(intent);
		}else {
		    Intent intent = new Intent(getActivity(),DeviceListActivity.class); 
		    startActivity(intent);	
		}
	}
		
}












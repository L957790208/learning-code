package com.heartrate.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class BasicActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Window window = this.getWindow();
		//ȡ������͸��״̬��,ʹ ContentView ���ݲ��ٸ���״̬��
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); 

		//��Ҫ������� flag ���ܵ��� setStatusBarColor ������״̬����ɫ
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); 
		//����״̬����ɫ
		window.setStatusBarColor(getResources().getColor(R.color.steelblue));		
	}
	}


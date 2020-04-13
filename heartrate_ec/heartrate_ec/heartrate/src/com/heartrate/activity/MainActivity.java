package com.heartrate.activity;

import com.heartrate.fragment.HeartRateFragment;
import com.heartrate.fragment.StepCounterFragment;
import com.heartrate.fragment.UserFragment;
import com.heartrate.view.StatusBarCompat;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.heartrate.activity.R.id;
import com.heartrate.bluetooth.BLEService;
/**
 * Author: xiaojun
 * Email: 786206257@qq.com
 * Date: 21/2/17
 */
public class MainActivity extends FragmentActivity {

	protected static final String TAG = "MainActivity";
	private Context mContext;

	private View mPopView;
	private View currentButton;
	
	private View includeBtn;

	private LinearLayout btnShouYe;
	private LinearLayout btnWoMen;
	private LinearLayout btnGengDou;	
	
	private TextView app_cancle;
	private TextView app_exit;
	private TextView app_gengxin;
	
	private PopupWindow mPopupWindow;
	
	private long clickTime = 0; //记录第一次点击的时间 		

	private BluetoothDevice device;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*
		Window window = this.getWindow();
		//设置透明状态栏,这样才能让 ContentView 向上
		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); 

		//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); 
		//设置状态栏颜色
		window.setStatusBarColor(getResources().getColor(R.color.steelblue));

		ViewGroup mContentView = (ViewGroup) this.findViewById(Window.ID_ANDROID_CONTENT);
		View mChildView = mContentView.getChildAt(0);
		if (mChildView != null) {
		    //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
		   // ViewCompat.setFitsSystemWindows(mChildView, false);
		}*/
		Window window = this.getWindow();
		//取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); 

		//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); 
		//设置状态栏颜色
		window.setStatusBarColor(getResources().getColor(R.color.steelblue));
		mContext = this;
		findView();
		init();
       // Intent localIntent = new Intent(getBaseContext(), BLEService.class);
		// getBaseContext().startService(localIntent);
		Intent intent = new Intent(MainActivity.this, BLEService.class);
        startService(intent);
	}
	
	private void findView(){	
		mPopView=LayoutInflater.from(mContext).inflate(R.layout.app_exit_menu, null);
		
		includeBtn = (View)findViewById(R.id.include_btn);
		btnShouYe=(LinearLayout) includeBtn.findViewById(R.id.btn_shouye);
		btnWoMen=(LinearLayout) includeBtn.findViewById(R.id.btn_women);
		btnGengDou=(LinearLayout) includeBtn.findViewById(R.id.btn_person);	
		app_cancle=(TextView) mPopView.findViewById(R.id.app_cancle);
		//app_gengxin=(TextView) mPopView.findViewById(R.id.app_gengxin_user);
		app_exit=(TextView) mPopView.findViewById(R.id.app_exit);
	}
	
	private void init(){
		//下面菜单
		btnShouYe.setOnClickListener(syOnClickListener);
		btnWoMen.setOnClickListener(wmOnClickListener);
		btnGengDou.setOnClickListener(gdOnClickListener);
		//模拟点击事件
		btnShouYe.performClick();
		
		
		//mPopupWindow=new PopupWindow(mPopView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

		//更新
/*		
 * app_gengxin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext, LoginActivity.class);
				startActivity(intent);
				((Activity)mContext).overridePendingTransition(R.anim.activity_up, R.anim.fade_out);
				finish();
			}
		});
		//取消
		app_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});		
		//退出
		app_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
	            System.exit(0); 
			}
		});   */
	}
	private OnClickListener settingOnClickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			
		    Intent intent = new Intent(MainActivity.this,UserinfoActivity.class); 
		   // intent.putExtras(bundle);
		    startActivity(intent);	
		}
	};	
	private OnClickListener syOnClickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			FragmentManager fm=getSupportFragmentManager();
			FragmentTransaction ft=fm.beginTransaction();
			StepCounterFragment shouYeFragment=new StepCounterFragment();
			ft.replace(R.id.fl_content, shouYeFragment,MainActivity.TAG);
			ft.commit();
			setButton(v);
			
		}
	};
	
	private OnClickListener wmOnClickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			FragmentManager fm=getSupportFragmentManager();
			FragmentTransaction ft=fm.beginTransaction();
			HeartRateFragment wmFragment=new HeartRateFragment();
			ft.replace(R.id.fl_content, wmFragment,MainActivity.TAG);
			ft.commit();
			setButton(v);
			
		}
	};
	
	private OnClickListener gdOnClickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			FragmentManager fm=getSupportFragmentManager();
			FragmentTransaction ft=fm.beginTransaction();
			UserFragment gdFragment=new UserFragment();
			ft.replace(R.id.fl_content, gdFragment,MainActivity.TAG);
			ft.commit();
			setButton(v);
			
		}
	};
	
	private void setButton(View v){
		if(currentButton!=null&&currentButton.getId()!=v.getId()){
			currentButton.setEnabled(true);
		}
		v.setEnabled(false);
		currentButton=v;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
/*		if(keyCode==KeyEvent.KEYCODE_MENU){
			this.onPause();
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b0000000")));
			mPopupWindow.showAtLocation(includeBtn, Gravity.BOTTOM, 0, 0);
			mPopupWindow.setAnimationStyle(R.style.app_pop);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			mPopupWindow.update();

		}*/
		//返回按键退出
		if(keyCode==KeyEvent.KEYCODE_BACK){
			exit();
			return true; 
		}
		return super.onKeyDown(keyCode, event);
	
	}

	@Override
	protected void onDestroy() {
		// TODO 自动生成的方法存根
		System.exit(0);
		super.onDestroy();
	}
    private void exit() { 
        if ((System.currentTimeMillis() - clickTime) > 2000) { 
            Toast.makeText(getApplicationContext(), "再按一次退出程序", 
                    Toast.LENGTH_SHORT).show(); 
            clickTime = System.currentTimeMillis(); 
        } else { 
            this.finish(); 
            System.exit(0); 
        } 
    }
  
}



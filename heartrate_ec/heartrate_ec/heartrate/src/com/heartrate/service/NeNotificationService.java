package com.heartrate.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

public class NeNotificationService extends AccessibilityService {
	
	private LinearLayout rootLayout;
	private static String qqpimsecure = "com.tencent.qqpimsecure";
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		//判断辅助服务触发的事件是否是通知栏改变事件
		
	    if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)  {
	    	if(event.getPackageName().equals(qqpimsecure)){
	    		
	    	}else{
	    		//AppLog.i("notification toStr():" + event.toString());
	    		//AppLog.i("notification getText : " + event.getText());
	    		//获取Parcelable对象

	    		Parcelable data = event.getParcelableData();
	    		//判断是否是Notification对象
				if (data instanceof Notification) {
					// Log.i(TAG, "Recieved notification");										
					Notification notification = (Notification) data;
					
					//analyzeNotify(notification);
					//AppLog.i("ticker: " + notification.tickerText);
					
					// Log.i(TAG, "contentview: " + notification.contentView));
					///*
					Intent intent = new Intent();
					intent.putExtra("NotifyData", notification);
					intent.putExtra("packageName", event.getPackageName());
					//AppLog.i("notification getPackageName: " + event.getPackageName());
					intent.setAction(".NeNotificationService");
					//sendBroadcast(intent);
					//进行处理解析通知栏内容的函数
					System.out.println("......NeNotificationService....包名......" + event.getPackageName());
			//		MainActivity.notifyReceive((String)event.getPackageName(), notification);
					//*/
					AnalyzeView(notification.contentView,event.getPackageName()+"");

				}
			}
	    }else {
	    	//Log.i(TAG, "other event : " + event.getEventType() + " .package:"  + event.getPackageName() + " .text:" + event.getText());
	    }		
	}
	private void analyzeNotify(Notification notification){
		RemoteViews views = notification.contentView;
		Class secretClass = views.getClass();

		try {
		    Map<Integer, String> text = new HashMap<Integer,String>();

		    Field outerField = secretClass.getDeclaredField("mActions");
		    outerField.setAccessible(true);
		    ArrayList<Object> actions = (ArrayList<Object>) outerField.get(views);

		    for (Object action : actions) {
		    	//AppLog.i("analyzeNotify action:" + action.toString());
		        Field innerFields[] = action.getClass().getDeclaredFields();
		        Field innerFieldsSuper[] = action.getClass().getSuperclass().getDeclaredFields();
		        
		        Object value = null;
		        Integer type = null;
		        Integer viewId = null;
		        for (Field field : innerFields) {
		            field.setAccessible(true);
		            //AppLog.i("analyzeNotify innerFields :" + field.toString());
		            if (field.getName().equals("value")) {
		                value = field.get(action);
		            } else if (field.getName().equals("type")) {
		                type = field.getInt(action);
		            }else if(field.getName().equals("URI")) {
		            	
		            	//AppLog.i("analyzeNotify innerFields URI :" + uri);
		            	
		            }else{
		            	//Object obj = (Object)field.get(action);
		            	//AppLog.i("analyzeNotify innerFields obj :" + obj);
		            	
		            }
		        }
		        for (Field field : innerFieldsSuper) {
		            field.setAccessible(true);
		            //AppLog.i("analyzeNotify innerFieldsSuper :" + field.toString());
		            if (field.getName().equals("viewId")) {
		                viewId = field.getInt(action);
		            }
		        }

		        if (value != null && type != null && viewId != null && (type == 9 || type == 10)) {
		            text.put(viewId, value.toString());
		        }
		    }

		   // AppLog.i("analyzeNotify title is: " + text.get(16908310));
		  //  AppLog.i("analyzeNotify info is: " + text.get(16909082));
		   // AppLog.i("analyzeNotify text is: " + text.get(16908358));
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	/**
	*Service被启动的时候会调用这个API
	*/	
	protected void onServiceConnected() {
		System.out.println("...............onServiceConnected......*Service被启动的时候会调用这个API............");
		//AppLog.d("onServiceConnected");
		//设置关心的事件类型
	    AccessibilityServiceInfo info = new AccessibilityServiceInfo();
	    info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED |
				AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | 
				AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
	    info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
	    info.notificationTimeout = 100;//两个相同事件的超时时间间隔
	    setServiceInfo(info);
	}

	@Override
	public void onInterrupt() {
		//AppLog.d( "onInterrupt");
        System.out.println("...." + "onInterrupt");
	}

	private void AnalyzeView(RemoteViews remoteView, String packName) {

		try {
			//把RemoteView apply后变成当前可以处理的View
			View v1 = remoteView.apply(this, rootLayout);
		
			//然后就是枚举处理这个View的内容
			EnumGroupViews(v1);
			
			//展示出来
			rootLayout.addView(v1);
			
			
		} catch (Exception e) {
		//	System.out.println("addToUi excep",e);
		}
		
	}

	private void EnumGroupViews(View v1){
		if(v1 instanceof ViewGroup){			
			ViewGroup lav = (ViewGroup)v1;
			int lcCnt = lav.getChildCount();
			for(int i = 0; i < lcCnt; i++)
			{
				View c1 = lav.getChildAt(i);
				if(c1 instanceof ViewGroup)
					EnumGroupViews(c1);//递归处理GroupView
				else if(c1 instanceof TextView)
					
				{
					//TestView则解析里面文本内容
					TextView txt = (TextView)c1;
					String str = txt.getText().toString().trim();
					if(str.length() > 0)
					{
						//这里打印文本内容
					}
					
					System.out.println( "TextView id:"+ txt.getId() + ".text:" + str);
				}else
				{
					//System.out.println("2 other layout:" + c1.toString());
					
				}
			}
		}
		else {
			//AppLog.w("1 other layout:" + v1.toString());
		}
	}	
	
	
}

















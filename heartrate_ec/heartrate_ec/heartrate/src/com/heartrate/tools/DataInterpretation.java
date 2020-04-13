package com.heartrate.tools;

import android.R.integer;
/**
 * Author: xiaojun
 * Email: 786206257@qq.com
 * 
 */
//数据解释类，把收到蓝牙的数据转成对应数据。
public class DataInterpretation {

	private static final int UUID_USER_DATA = 1; 
	private static final int UUID_TIME_SYNC = 2;	
	private static final int UUID_RSC_M = 3;
	private static final int UUID_CHISTORY_DATA= 4; 	
	private static final int UUID_IPONE = 5; 
	private static final int UUID_MESSAGE_PUSH = 6; 	
	private static final int UUID_SEARCH_IPONE = 7; 
	private static final int UUID_CLOCK_ALARM = 8; 		
	private static final int UUID_REMINDER_FUNCTION = 9; 	
	
	private byte[] data;
	
	
	public DataInterpretation(byte[] data2) {
		// TODO Auto-generated constructor stub
		data = data2;
	}

	//先判断是哪个子服务uuid
	public static void judge(int key){	
		switch (key) {
		case UUID_USER_DATA: 
			
			break;
		case UUID_TIME_SYNC:
			
			break;
		case UUID_RSC_M:
			
			break;			
		case UUID_CHISTORY_DATA:
			
			break;
		case UUID_IPONE:
			
			break;			
		case UUID_MESSAGE_PUSH:
			
			break;
		case UUID_SEARCH_IPONE:
			
			break;
		case UUID_CLOCK_ALARM:
			
			break;
		case UUID_REMINDER_FUNCTION:
			
			break;				
		
		default:
			break;
		}
	}
	
   public int getGender(byte[] data){
	    return data[0] & 0xFF;
   }
   
   public int getAge(byte[] data){
	    return data[1] & 0xFF;
   }
   
   public int getStep(byte[] data){
	    return data[2] & 0xFF;
   }
   
   public int getHeight(byte[] data){
	      int i;
	      i = (data[3] & 0xFF) | (data[4] << 8);
	    return i;
   }  
   
   public int getWeight(byte[] data){
	      int i;
	      i = (data[5] & 0xFF) | (data[6] << 8);
	    return i;
   }
   
    public int getSedentaryTime(byte[] data){
	      int i;
	      i = (data[7] & 0xFF) | (data[8] << 8);
	    return i;    	
    }

 //0x2A53   
    public int getSpeed(){
	    return data[1] & 0xFF;
   }
   
   public  int getTotalStep(){
	   System.out.println("-----getTotalStep------");
	      int i;
	      i = (data[2] & 0xFF) | (data[3] << 8);
	      System.out.println("-----------"+i);
	    return i;
   }
   //总距离
   public int getTotalDistance(){
	   System.out.println("-----getTotalDistance------");
	      int i;
	      i = (data[4] & 0xFF) | (data[5] << 8);
	      System.out.println("-----------"+i);
	    return i;
   }   
 //卡路里
   public int getTotalCalories(){
	   System.out.println("-----getTotalCalories------");
	      int i;
	      i = (data[6] & 0xFF) | (data[7] << 8);
	    return i;
   }
   
   public int getMovementTime(byte[] data){
	      int i;
	      i = (data[8] & 0xFF) | (data[9] << 8);
	    return i;
   }
   public int getDeepSleepTime(byte[] data){
	      int i;
	      i = (data[10] & 0xFF) | (data[11] << 8);
	    return i;
   }
   public int getShallowSleepTime(byte[] data){
	      int i;
	      i = (data[12] & 0xFF) | (data[13] << 8);
	    return i;
   }
   
   //白天休息时间Rest during the day
   public int getRestSleepTime(byte[] data){
	      int i;
	      i = (data[14] & 0xFF) | (data[15] << 8);
	    return i;
   }   
    //心率
   public int getHeartRate(byte[] data){
	    return data[16] & 0xFF;
   }
   //血氧
   public int getOxygen(byte[] data){
	    return data[17] & 0xFF;
   }   
   
   public String byteToHex( byte[] b,int i) {  
		 String hex = Integer.toHexString(b[i-1] & 0xFF); 
	     if (hex.length() == 1) { 
	       hex = '0' + hex; 
	     } 
		 return hex; 
		}	

   
   
}
























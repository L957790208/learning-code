package com.heartrate.my;

import java.io.Serializable;

import com.heartrate.tools.DataInterpretation;
import com.heartrate.tools.DecimalConversion;
/**
 * Author: xiaojun
 * Email: 786206257@qq.com
 * Date: 21/2/17
 */
public class MeasurementData implements Serializable{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int kinestate;       //运动状态
	 private int speed;          //速度
	 private int totalStep;      //总步数
	 private int totalDistance;  //总距离
	 private int totalCalories;	 //卡路里
	 private int movementTime;  //运动时间
	 private int deepSleepTime;	 //深睡时间 
	 private int lightSleepTime;  //浅睡时间
	 private int breakTime;	 //白天休息时间 
	// private int totalCalories;	 //心率值
	// private int totalCalories;	 //血氧	 
	 private byte[] data;
	 
	 public void setMeasurementData(byte[] d) {
		// TODO Auto-generated constructor stub
		 this.data = d;	 
		 kinestate = data[0] & 0xFF;
		 speed = data[1] & 0xFF;
		 totalStep = (data[2] & 0xFF) | (data[3] << 8);
		 totalDistance = (data[4] & 0xFF) | (data[5] << 8);
		 totalCalories = (data[6] & 0xFF) | (data[7] << 8);		 
		 movementTime = (data[8] & 0xFF) | (data[9] << 8);
		 deepSleepTime = (data[10] & 0xFF) | (data[11] << 8);		 
		 lightSleepTime = (data[12] & 0xFF) | (data[13] << 8);
		 breakTime = (data[14] & 0xFF) | (data[15] << 8);		 			 
	}

	public int getKinestate() {
		return kinestate;
	}

	public void setKinestate(int kinestate) {
		this.kinestate = kinestate;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getTotalStep() {
		return totalStep;
	}

	public void setTotalStep(int totalStep) {
		this.totalStep = totalStep;
	}

	public int getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(int totalDistance) {
		this.totalDistance = totalDistance;
	}

	public int getTotalCalories() {
		return totalCalories;
	}

	public void setTotalCalories(int totalCalories) {
		this.totalCalories = totalCalories;
	}

	public int getMovementTime() {
		return movementTime;
	}

	public void setMovementTime(int movementTime) {
		this.movementTime = movementTime;
	}

	public int getDeepSleepTime() {
		return deepSleepTime;
	}

	public void setDeepSleepTime(int deepSleepTime) {
		this.deepSleepTime = deepSleepTime;
	}

	public int getLightSleepTime() {
		return lightSleepTime;
	}

	public void setLightSleepTime(int lightSleepTime) {
		this.lightSleepTime = lightSleepTime;
	}

	public int getBreakTime() {
		return breakTime;
	}

	public void setBreakTime(int breakTime) {
		this.breakTime = breakTime;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}	
         
}







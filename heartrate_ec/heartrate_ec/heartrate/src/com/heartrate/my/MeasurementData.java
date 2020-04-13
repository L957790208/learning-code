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
	private int kinestate;       //�˶�״̬
	 private int speed;          //�ٶ�
	 private int totalStep;      //�ܲ���
	 private int totalDistance;  //�ܾ���
	 private int totalCalories;	 //��·��
	 private int movementTime;  //�˶�ʱ��
	 private int deepSleepTime;	 //��˯ʱ�� 
	 private int lightSleepTime;  //ǳ˯ʱ��
	 private int breakTime;	 //������Ϣʱ�� 
	// private int totalCalories;	 //����ֵ
	// private int totalCalories;	 //Ѫ��	 
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







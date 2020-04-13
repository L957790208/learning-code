package com.heartrate.my;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

public class UserBodyInformation implements Serializable{

	private static final long serialVersionUID = 1L;
	private String nickname;
	 private int gender;       //性别
	 private int age;          //年龄
	 private int stepLength;      //步长
	 private int height;  //身高
	 private int weight;	 //体重
	 private int sedentaryTime;  //久坐时间	 
	 private byte[] data;
	 private int targetStep; //白天目标步数
	 
	 public UserBodyInformation() {
		// TODO Auto-generated constructor stub	 			 
	}	 
	 public void setUserBodyInformation(byte[] d) {
		// TODO Auto-generated constructor stub
		 this.data = d;	 
		 gender = data[0] & 0xFF;
		 age = data[1] & 0xFF;
		 stepLength = data[2] & 0xFF;
		 height = (data[3] & 0xFF) | (data[4] << 8);
		 weight = (data[5] & 0xFF) | (data[6] << 8);		 
		 sedentaryTime = (data[7] & 0xFF) | (data[8] << 8);		 			 
	}
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getStepLength() {
		return stepLength;
	}

	public void setStepLength(int stepLength) {
		this.stepLength = stepLength;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getSedentaryTime() {
		return sedentaryTime;
	}

	public void setSedentaryTime(int sedentaryTime) {
		this.sedentaryTime = sedentaryTime;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

    public byte[] getByteArray(){
    	byte[] data = {0,0,0,0,0,0,0,0,0};
    	// data[0] = gender & 0xFF;
   	    data[1] = (byte) age;
   	    data[2] = (byte) stepLength;
   	    data[3] = (byte) ((byte) height & 0xff);
   	    data[4] = (byte) (height >>8) ;
   	    data[5] = (byte) ((byte) weight & 0xff);
   	    data[6] = (byte) (weight >>8) ;   	    
   	    data[7] = (byte) ((byte) sedentaryTime & 0xff);
   	    data[8] = (byte) (sedentaryTime >>8);   	    
		return data;
    	
    }
	public int getTargetStep() {
		return targetStep;
	}
	
	public void setTargetStep(int targetStep) {
		this.targetStep = targetStep;
	}	
	
}


















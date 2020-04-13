package com.heartrate.tools;
/**
 * Author: xiaojun
 * Email: 786206257@qq.com
 * 
 */
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;

public class SaveHeartrateData {
	private Activity activity;
	private SQLiteDatabase db;
		
	public SaveHeartrateData(FragmentActivity activity) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}

	public void createDatabase(){
		//打开或创建test.db数据库
		db = activity.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null);
		//创建hrdata表
		//db.execSQL("DROP TABLE IF EXISTS hrdata");	
		db.execSQL("DROP TABLE IF EXISTS hrtab");	
		//db.execSQL("CREATE TABLE hrdata (_id INTEGER PRIMARY KEY AUTOINCREMENT, hrid VARCHAR, time VARCHAR, allbpm SMALLINT)");
		db.execSQL("CREATE TABLE hrtab (_id INTEGER PRIMARY KEY AUTOINCREMENT, hrid VARCHAR, allbpm INT)");
	}
    public void addDatabase(int value){
    	ContentValues values =new ContentValues();
		values.put("allbpm", 1);
    	db.insert("hrtab", "allbpm", values);
    	System.out.println("...............");
    }
	public void close() {
		// TODO Auto-generated method stub
		//db.execSQL("DROP TABLE IF EXISTS hrtab"); 
		db.close();
	}

}















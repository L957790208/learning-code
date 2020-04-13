package com.heartrate.utils;

import com.heartrate.bluetooth.Person;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * Author: xiaojun
 * Email: 786206257@qq.com
 * 
 */
public class saveDataBase {

	public void initDateBase(){
		//打开或创建test.db数据库
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("heartrate.db", null);
		db.execSQL("DROP TABLE IF EXISTS person");
		//创建person表
		db.execSQL("CREATE TABLE person (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age SMALLINT)");
		Person person = new Person();
		person.name = "john";
		person.age = 30;
		//插入数据
		db.execSQL("INSERT INTO person VALUES (NULL, ?, ?)", new Object[]{person.name, person.age});
		
		person.name = "david";
		person.age = 33;
		//ContentValues以键值对的形式存放数据
		ContentValues cv = new ContentValues();
		cv.put("name", person.name);
		cv.put("age", person.age);
		//插入ContentValues中的数据
		db.insert("person", null, cv);
		
		cv = new ContentValues();
		cv.put("age", 35);
		//更新数据
		db.update("person", cv, "name = ?", new String[]{"john"});
		
		Cursor c = db.rawQuery("SELECT * FROM person WHERE age >= ?", new String[]{"33"});
		while (c.moveToNext()) {
			int _id = c.getInt(c.getColumnIndex("_id"));
			String name = c.getString(c.getColumnIndex("name"));
			int age = c.getInt(c.getColumnIndex("age"));
			Log.i("db", "_id=>" + _id + ", name=>" + name + ", age=>" + age);
		}
		c.close();
		
		//删除数据
		db.delete("person", "age < ?", new String[]{"35"});
		
		//关闭当前数据库
		db.close();
		
		//删除test.db数据库
//		deleteDatabase("test.db");
		
	}

	
	
}












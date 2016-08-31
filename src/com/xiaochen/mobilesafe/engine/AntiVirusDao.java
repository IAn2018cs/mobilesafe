package com.xiaochen.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {
	// 指定访问数据库的路径
	public static String path = "data/data/com.xiaochen.mobilesafe/files/antivirus.db";

	public static List<String> getVirusList() {
		// 打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		// 查询datable表的md5
		Cursor cursor = db.query("datable", new String[] { "md5" }, null, null, null, null, null);
		// 创建存储MD5的集合
		List<String> virusList = new ArrayList<String>();
		// 循环取数据
		while(cursor.moveToNext()){
			virusList.add(cursor.getString(0));
		}
		
		cursor.close();
		db.close();
		
		return virusList;
	}
}

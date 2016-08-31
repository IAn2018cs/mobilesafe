package com.xiaochen.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {
	// 指定访问数据库的路径
	public static String path = "data/data/com.xiaochen.mobilesafe/files/commonnum.db";

	/**获得组的集合
	 * @return 返回包含组名，组下孩子的表名序号，组下孩子的集合
	 */
	public static List<Group> getGroup() {
		// 打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		// 查询classlist表
		Cursor cursor = db.query("classlist", new String[]{"name","idx"}, null, null, null, null, null);
		// 创建一个存储组的集合
		List<Group> groupList = new ArrayList<Group>();
		while(cursor.moveToNext()){
			Group group = new Group();
			group.name = cursor.getString(0);
			group.idx = cursor.getString(1);
			group.childList = getGroupChild(group.idx);
			groupList.add(group);
		}
		cursor.close();
		db.close();
		
		return groupList;
	}
	
	/**获取组的孩子
	 * @param idx  表的序号
	 * @return 组下孩子的集合
	 */
	public static List<Child> getGroupChild(String idx) {
		// 打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		// 查询组对应孩子的表table1   table2
		Cursor cursor = db.query("table"+idx, new String[]{"number","name"}, null, null, null, null, null);
		// 创建一个存储组的集合
		List<Child> childList = new ArrayList<Child>();
		while(cursor.moveToNext()){
			Child child = new Child();
			child.number = cursor.getString(0);
			child.name = cursor.getString(1);
			childList.add(child);
		}
		cursor.close();
		db.close();
		
		return childList;
	}
	
	public static class Group{
		public String name;
		public String idx;
		public List<Child> childList;
	}
	
	public static class Child{
		public String name;
		public String number;
	}
}

package com.xiaochen.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaochen.mobilesafe.db.BlackNumberOpenHelper;
import com.xiaochen.mobilesafe.db.domain.BlackNumberInfo;

public class BlackNumberDao {
	private BlackNumberOpenHelper blackNumberOpenHelper;

	private BlackNumberDao(Context context){
		// 创建数据库
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
	
	private static BlackNumberDao blackNumberDao = null;
	
	public static BlackNumberDao getInstance(Context context){
		if(blackNumberDao == null){
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}
	
	/**增加一条数据
	 * @param phone  要添加的电话号码
	 * @param mode   拦截模式   (1 短信，2 电话， 3 所有)
	 */
	public void insert(String phone, int mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		
		db.close();
	}
	
	/**删除一条数据
	 * @param phone  要删除的电话号码
	 */
	public void delete(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		db.delete("blacknumber", "phone = ?", new String[]{phone});
		db.close();
	}
	
	/**更改一条数据
	 * @param phone  根据哪个电话号码去更改
	 * @param mode   更改后的模式
	 */
	public void update(String phone, int mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update("blacknumber", values, "phone = ?", new String[]{phone});
		db.close();
	}
	
	
	/**查询数据库中所有的数据
	 * @return  返回包含电话和模式的集合
	 */
	public List<BlackNumberInfo> queryAll(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"phone","mode"}, null, null, null, null, "_id desc");
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setPhone(cursor.getString(0));
			blackNumberInfo.setMode(cursor.getInt(1));
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		
		return blackNumberList;
	}
	
	/**分页查询  每次查询20条数据
	 * @param index 开始查询的索引位置
	 * @return  返回包含电话和模式的集合
	 */
	public List<BlackNumberInfo> queryLimit(int index){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;", new String[]{index+""});
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setPhone(cursor.getString(0));
			blackNumberInfo.setMode(cursor.getInt(1));
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		
		return blackNumberList;
	}
	
	/**获取数据库中一共有多少条数据
	 * @return  数据库中数据的总个数
	 */
	public int getCount(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
		int count = 0;
		if(cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		
		return count;
	}
	
	/**获取电话号码的拦截模式
	 * @param phone  要查询其模式的电话号码
	 * @return   该电话号码的拦截模式    (1短信  2电话  3所有  0无)
	 */
	public int getMode(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null, null);
		int mode = 0;
		if(cursor.moveToNext()){
			mode = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		
		return mode;
	}
}

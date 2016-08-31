package com.xiaochen.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.xiaochen.mobilesafe.db.AppLockOpenHelper;

public class AppLockDao {
	private AppLockOpenHelper appLockOpenHelper;
	private Context mContext;

	private AppLockDao(Context context){
		this.mContext = context;
		// 创建数据库
		appLockOpenHelper = new AppLockOpenHelper(context);
	}
	
	// 单例模式
	private static AppLockDao appLockDao = null;
	
	public static AppLockDao getInstance(Context context){
		if(appLockDao == null){
			appLockDao = new AppLockDao(context);
		}
		return appLockDao;
	}
	
	/**增加一个加锁应用
	 * @param packagename  应用的包名
	 */
	public void insert(String packagename){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("packagename", packagename);
		db.insert("applock", null, values);
		
		db.close();

		// 获取内容解析者通知数据库发送改变
		mContext.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}
	
	/**删除一条数据
	 * @param packagename  应用的包名
	 */
	public void delete(String packagename){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		db.delete("applock", "packagename = ?", new String[]{packagename});

		db.close();

		// 获取内容解析者通知数据库发送改变
		mContext.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}
	
	/**查询数据库中所有的数据
	 * @return  返回包含包名的集合
	 */
	public List<String> queryAll(){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
		List<String> lockList = new ArrayList<String>();
		while(cursor.moveToNext()){
			lockList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		
		return lockList;
	}
	
}

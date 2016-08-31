package com.xiaochen.mobilesafe.engine;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	// 指定访问数据库的路径
	public static String path = "data/data/com.xiaochen.mobilesafe/files/address.db";
	private static String address = "未知号码";

	public static String getAddress(String phone,Context context) {
		// 打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		// 手机号码正则表达式
		String regularExpression = "^1[3|4|5|7|8]\\d{9}";
		// 如果能匹配上就是手机号
		if (phone.matches(regularExpression)) {
			// 截取前七位号码
			phone = phone.substring(0, 7);
			// 根据phone去查询outkey
			Cursor cursor = db.query("data1", new String[] { "outkey" }, "id = ?", new String[] { phone }, null, null, null);
			// 如果能移到下一个就说明能查到
			if (cursor.moveToNext()) {
				String outkey = cursor.getString(0);
				Cursor inCursor = db.query("data2", new String[] { "location" }, "id = ?", new String[] { outkey }, null, null, null);
				if (inCursor.moveToNext()) {
					address = inCursor.getString(0);
				}
			}else{
				juheApi(phone,context);
				System.out.println("跳到聚合数据查询");
			}
		}else{
			int length = phone.length();
			switch (length) {
			case 3:
				address = "报警电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "服务电话";
				break;
			case 7:
				address = "本地电话";
				break;
			case 8:
				address = "本地电话";
				break;
			case 11:
				//3位区号加8位座机
				String area1 = phone.substring(1, 3);
				Cursor cursor1 = db.query("data2", new String[]{"location"}, "area=?", new String[]{area1}, null, null, null);
				if(cursor1.moveToNext()){
					address = cursor1.getString(0);
				}else{
					address = "未知号码";
				}
				break;
			case 12:
				//4位区号加8位座机
				String area2 = phone.substring(1, 4);
				Cursor cursor2 = db.query("data2", new String[]{"location"}, "area=?", new String[]{area2}, null, null, null);
				if(cursor2.moveToNext()){
					address = cursor2.getString(0);
				}else{
					address = "未知号码";
				}
				break;
			default:
				address = "未知号码";
				break;
			}
			
		}
		return address;
	}

	private static void juheApi(String phone, Context context) {
		try {
			//将字符串转换成长整型
			long parseLong = Long.parseLong(phone);
			Parameters params = new Parameters();
			params.add("phone", parseLong);
			params.add("dtype", "json");
			JuheData.executeWithAPI(context, 11,
					"http://apis.juhe.cn/mobile/get", JuheData.GET, params,
					new DataCallBack() {
						@Override
						public void onSuccess(int arg0, String arg1) {
							// 解析json
							try {
								JSONObject jsonObject = new JSONObject(arg1);
								//结果码
								String resultcode = jsonObject.getString("resultcode");
								//如果结果码是200则请求成功
								if(resultcode.equals("200")){
									//请求结果集合
									JSONObject jsonObject2 = jsonObject.getJSONObject("result");
									//省份
									String province = jsonObject2.getString("province");
									//城市
									String city = jsonObject2.getString("city");
									//运营商
									String company = jsonObject2.getString("company");
									company = company.substring(2, 4);
									address = province+city+company;
								} else {
									address = "未知号码";
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFinish() {

						}

						@Override
						public void onFailure(int arg0, String arg1,
								Throwable arg2) {

						}
					});
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		}
	}
}

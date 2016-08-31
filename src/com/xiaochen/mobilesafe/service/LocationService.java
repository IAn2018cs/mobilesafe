package com.xiaochen.mobilesafe.service;

import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		//获取位置管理者
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//以最优的方式获取经纬度坐标
		Criteria criteria = new Criteria();
		//由于可能使用流量  所以允许花费
		criteria.setCostAllowed(true);
		//指定精确度   为了省电  这里指定为精确
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		//获取最优的提供经纬度的方式
		String bestProvider = locationManager.getBestProvider(criteria, true);
		
		//请求位置的时间间隔和移动间隔 (提供经纬度方式，最小更新时间，最小移动多少距离更新，监听器)
		locationManager.requestLocationUpdates(bestProvider, 0, 0, new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				//提供经纬度方式状态改变时候调用
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				//提供经纬度方式打开时调用
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				//提供经纬度方式关闭时调用
			}
			
			@Override
			public void onLocationChanged(Location location) {
				//位置发生改变时调用
				//获取经度
				double longitude = location.getLongitude();
				//获取维度
				double latitude = location.getLatitude();
				
				//获取短信管理者  并发送短信
				SmsManager smsManager = SmsManager.getDefault();
				//获取存储的发送短信人的电话号码
				String send_phone = SpUtils.getStringSp(getApplicationContext(), ConstantValue.SEND_SMS_PHONE_NUMBER, "");
				smsManager.sendTextMessage(send_phone, null, "经度:"+longitude+",纬度:"+latitude, null, null);
			}
		});
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}

package com.xiaochen.mobilesafe.service;

import com.xiaochen.mobilesafe.engine.ProcessInfoProvider;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ScreenLockService extends Service {
	private InnerReceiver receiver;

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册一个监听锁屏的广播
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		receiver = new InnerReceiver();
		registerReceiver(receiver, intentFilter);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消注册广播
		if(receiver!=null){
			unregisterReceiver(receiver);
		}
	}
	
	class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(final Context context, Intent intent) {
			ProcessInfoProvider.killAllProcess(context);
		}
	}
}

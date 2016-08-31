package com.xiaochen.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.engine.ProcessInfoProvider;
import com.xiaochen.mobilesafe.receiver.ProcessWidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class ProcessWidgetService extends Service {
	private Timer mTimer;
	private InnerReceiver mReceiver;

	@Override
	public void onCreate() {
		// 用定时器管理更新窗体小部件上的UI
		startTimer();
		
		// 注册监听解屏锁屏的广播
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		mReceiver = new InnerReceiver();
		registerReceiver(mReceiver, intentFilter);
		
		super.onCreate();
	}
	
	class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
				// 取消定时器
				cancleTimer();
			}else{
				startTimer();
			}
		}
	}
	
	private void startTimer() {
		// 创建一个定时器  开启后0秒执行  后每隔3秒执行一次
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// 更新UI
				updateUI();
			}
		}, 0, 3000);
	}

	// 取消定时器
	public void cancleTimer() {
		if(mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
	}

	// 更新窗体小部件UI
	protected void updateUI() {
		// 获得窗体小部件管理者
		AppWidgetManager aWM = AppWidgetManager.getInstance(this);
		
		// 将窗体小部件的布局转换成view对象
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
		
		// 设置view里的文字
		remoteViews.setTextViewText(R.id.tv_process_count, "进程总数:"+ProcessInfoProvider.getProcessCount(this));
		String strMemory = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailable(this));
		remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存:"+strMemory);
		
		// 设置窗体点击事件  跳转到应用主界面
		Intent intent = new Intent("android.intent.action.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		// 获取一个activity的PendingIntent
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);  // flags 固定写法  点击立马生效
		remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);
		
		// 设置窗体按钮点击事件  传递一个广播  杀死后台进程
		Intent broadcastIntent = new Intent("android.intent.action.KILL_ALL_PROCESS");
		broadcastIntent.putExtra("memory", ProcessInfoProvider.getAvailable(this));
		broadcastIntent.putExtra("count", ProcessInfoProvider.getProcessCount(this));
		// 获取一个Broadcast的PendingIntent
		PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);  // flags 固定写法  点击立马生效
		remoteViews.setOnClickPendingIntent(R.id.bt_clear, broadcastPendingIntent);
		
		// 更新窗体小部件 ComponentName参数为 上下文环境      定义的广播接收者字节码文件
		ComponentName componentName = new ComponentName(this, ProcessWidget.class);
		aWM.updateAppWidget(componentName, remoteViews);
	}

	@Override
	public void onDestroy() {
		cancleTimer();
		if(mReceiver != null){
			unregisterReceiver(mReceiver);
		}
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}

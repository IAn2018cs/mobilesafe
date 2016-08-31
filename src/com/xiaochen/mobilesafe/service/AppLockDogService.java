package com.xiaochen.mobilesafe.service;

import java.util.List;

import com.xiaochen.mobilesafe.activity.LockActivity;
import com.xiaochen.mobilesafe.db.dao.AppLockDao;
import com.xiaochen.mobilesafe.engine.ProcessInfoProvider;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ConfigurationInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class AppLockDogService extends Service {
	private boolean isWatch;
	private AppLockDao mDao;
	private List<String> mPackagenameList;
	private SkipLockReceiver mSkipLockReceiver;
	private String mPackageName = "";
	private MyContentObserver mMyContentObserver;

	@Override
	public void onCreate() {
		// 服务开启  将是否循环监听设置为true
		isWatch = true;
		// 获取数据库
		mDao = AppLockDao.getInstance(this);
		// 当服务开启就去一直监听当前开启的应用栈
		watchTask();
		
		// 注册一个监听是否解锁的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.xiaochen.SKIP");
		mSkipLockReceiver = new SkipLockReceiver();
		registerReceiver(mSkipLockReceiver, intentFilter);
		
		// 通过内容解析者注册一个内容观察者  第二个参数为是否模糊查询
		mMyContentObserver = new MyContentObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, mMyContentObserver);
		
		super.onCreate();
	}
	
	class MyContentObserver extends ContentObserver{
		public MyContentObserver(Handler handler) {
			super(handler);
		}
		// 当数据库改变时会调用此方法
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// 重新查询数据库
			new Thread(){
				public void run() {
					mPackagenameList = mDao.queryAll();
				};
			}.start();
		}
	}

	
	// 监听是否解锁的广播
	class SkipLockReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			mPackageName = intent.getStringExtra("packagename");
		}
	}
	
	private void watchTask() {
		// 由于一直循环是耗时操作  所以开子线程
		new Thread(){
			public void run() {
				//获取数据库中存储的包集合
				mPackagenameList = mDao.queryAll();
				// 需要一直去循环操作
				while(isWatch){
					// 通过ActivityManager获取当前开启的任务栈信息集合
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
					String packagename = runningAppProcesses.get(0).processName;
					// 只需要获取当前显示的任务栈即可  所以获取最大数为1个
//					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					// 得到当前运行任务栈的信息
//					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					// 获得当前任务栈顶的activity对应应用的包名
//					String packagename = runningTaskInfo.topActivity.getPackageName();
					// 去和程序锁数据库中的包名比对  如果数据库中存储的包集合包含该包名 就开启拦截界面
					if(mPackagenameList.contains(packagename)){
						// 如果没有解锁  继续拦截
						if(!mPackageName.equals(packagename)){
							Intent intent = new Intent(getApplicationContext(),LockActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packagename", packagename);
							startActivity(intent);
							// 发送一条更新UI的广播
							Intent inActivityIntent = new Intent("com.xiaochen.LOCK_DOG");
							inActivityIntent.putExtra("packagename", packagename);
							sendBroadcast(inActivityIntent);
						}
					}
					
					// 不在后台任务运行时才重置已解锁包名(长按home键)
					/*// 如果运行中的应用中不包含已经解锁的应用  就把包名重置为空
					List<String> processPackageName = ProcessInfoProvider.getProcessPackageName(getApplicationContext());
					if(!processPackageName.contains(mPackageName)){
						mPackageName = "";
					}*/
					
					// 看不见的时候就重置包名(按home键或back键退出应用)
					List<RunningAppProcessInfo> nowrunningAppProcesses = am.getRunningAppProcesses();
					String nowpackagename = nowrunningAppProcesses.get(0).processName;
					if(!nowpackagename.equals(mPackageName)){
						mPackageName = "";
					}
					
					try {
						// 为了让cup合理利用   循环一次睡一下  时间轮片
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		// 停止循环
		isWatch = false;
		// 注销广播
		if(mSkipLockReceiver!=null){
			unregisterReceiver(mSkipLockReceiver);
		}
		// 注销内容观察者
		if(mMyContentObserver!=null){
			getContentResolver().unregisterContentObserver(mMyContentObserver);
		}
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}

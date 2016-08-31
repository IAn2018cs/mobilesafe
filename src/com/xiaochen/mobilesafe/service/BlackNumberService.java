package com.xiaochen.mobilesafe.service;

import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;
import com.xiaochen.mobilesafe.db.dao.BlackNumberDao;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class BlackNumberService extends Service {
	private SmsReceiver smsReceiver;
	private BlackNumberDao mDao;
	private MyPhoneStateListener myPhoneStateListener;
	private TelephonyManager mTM;
	private MyContentObserver myContentObserver;

	@Override
	public void onCreate() {
		super.onCreate();
		// 打开数据库
		mDao = BlackNumberDao.getInstance(getApplicationContext());
		
		// 注册监听短信的广播
		IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		// 设置接收广播的优先级  1000为最高
		intentFilter.setPriority(1000);
		smsReceiver = new SmsReceiver();
		registerReceiver(smsReceiver, intentFilter);
		
		// 监听电话状态
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			// 空闲(没有电话)
			case TelephonyManager.CALL_STATE_IDLE:
				
				break;
			// 摘机(接听)
			case TelephonyManager.CALL_STATE_OFFHOOK:
				
				break;
			// 响铃
			case TelephonyManager.CALL_STATE_RINGING:
				// 拦截电话  就是在响铃的时候挂断电话
				int mode = mDao.getMode(incomingNumber);
				if(mode == 2 || mode == 3){
					// 挂断电话
					endCall();
					// 删除通话记录
					deleteCallLog(incomingNumber);
				}
				break;
			}
		}
	}
	
	public void deleteCallLog(String phone) {
		// 获取内容解析器  去注册内容观察者  当数据库发生改变时  再去删除数据  参数(Uri地址   是否模糊查询   内容观察者ContentObserver对象)
		myContentObserver = new MyContentObserver(new Handler(),phone);
		getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, myContentObserver);
	}
	
	// 挂断电话的方法
	public void endCall() {
		// 要调用aidl文件ITelephony里的endCall方法  需要用aidl和反射   下面这个方法就是返回ITelephony对象
		// ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
		// 需要先拿到上面方法里的参数  是个IBinder类型    通过反射的方法
		try {
			
			// 1.拿到ServiceManager的字节码文件
			Class<?> clazz = Class.forName("android.os.ServiceManager");
			// 2.拿到类里的getService方法   参数(方法名   参数类型的字节码文件)
			Method method = clazz.getMethod("getService", String.class);
			// 3.调用该方法   参数(调用该方法的对象[由于是静态方法，所以不需要对象]   具体参数)
			IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
			// 4.将得到的IBinder传入到上面那个方法中   得到ITelephony对象
			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
			// 5.调用ITelephony里的endCall()方法  挂断电话
			iTelephony.endCall();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 内容观察者
	class MyContentObserver extends ContentObserver{
		private String phone;

		public MyContentObserver(Handler handler, String phone) {
			super(handler);
			this.phone = phone;
		}
		
		// 当数据库改变时会调用此方法
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// 通过内容解析者去删除数据库中的数据
			getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phone});
		}
		
	}

	// 拦截短息的广播接收者
	class SmsReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取短信信息
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				//获取短信对象
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
				//获取短信内容
				String body = smsMessage.getMessageBody();
				System.out.println(body);
				//获取短信发送者    
				String phone = smsMessage.getOriginatingAddress();
				
				// 获取该号码的拦截模式   如果是短信或所有  就停止短信的广播传送
				int mode = mDao.getMode(phone);
				if(mode == 1 || mode == 3){
					// 中断这条广播
					abortBroadcast();
				}
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消注册广播
		if(smsReceiver != null){
			unregisterReceiver(smsReceiver);
		}
		
		// 取消监听电话状态
		if(mTM != null && myPhoneStateListener != null){
			mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		
		// 注销内容观察者
		if(myContentObserver != null){
			getContentResolver().unregisterContentObserver(myContentObserver);
		}
	}
}

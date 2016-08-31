package com.xiaochen.mobilesafe.service;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.engine.AddressDao;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressShowService extends Service {
	private TelephonyManager mTM;
	private MyPhoneStateListener mMyPhoneStateListener;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private WindowManager mWM;
	private View mView;
	private int mScreenWidth;
	private int mScreenHeight;
	private InnerOutCallReceiver mInnerOutCallReceiver;
	@Override
	public void onCreate() {
		super.onCreate();
		// 获得TelephonyManager对象
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// 获得WindowManager窗体对象
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		// 获得继承PhoneStateListener的子类对象MyPhoneStateListener
		mMyPhoneStateListener = new MyPhoneStateListener();
		// 设置电话的状态监听
		mTM.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		// 给监听电话外拨的广播注册过滤条件   (加权限)
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		// 创建广播接收者对象
		mInnerOutCallReceiver = new InnerOutCallReceiver();
		// 动态注册广播
		registerReceiver(mInnerOutCallReceiver, intentFilter);
	}	
	
	// 监听电话外拨的广播接收者
	class InnerOutCallReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取拨出电话号码的字符串
			String phone = getResultData();
			// 接收到广播后  调用展示自定义吐司的方法
			showToast(phone);
		}
	}
	
	class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			//空闲状态
			case TelephonyManager.CALL_STATE_IDLE:
				// 关闭自定义的toast
				if(mWM!=null && mView!=null){
					mWM.removeView(mView);
				}
				break;

			//摘机状态  通话或打电话
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// 关闭自定义的toast
				if(mWM!=null && mView!=null){
					mWM.removeView(mView);
				}
				break;
				
			//响铃状态
			case TelephonyManager.CALL_STATE_RINGING:
				// 展示自定义的toast
	            showToast(incomingNumber);
				break;
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	public void showToast(String incomingNumber) {
		// 展示自定义的toast  params属性值
		final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        // 在响铃是显示toast和电话类型级别一样
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        // 指定toast的位置
        params.gravity = Gravity.TOP + Gravity.LEFT;
        
        // 获取存在sp中左上角的坐标位置   赋给toast左上角坐标params.x和params.y
        params.x = SpUtils.getIntSp(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        params.y = SpUtils.getIntSp(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
        
        // 给toast设置拖动事件
        mView = View.inflate(getApplicationContext(), R.layout.address_toast, null);
        mView.setOnTouchListener(new OnTouchListener() {
        	int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 按下
				case MotionEvent.ACTION_DOWN:
					// 获取起始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				// 移动
				case MotionEvent.ACTION_MOVE:
					// 移动中的位置
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					
					// 移动的距离
					int disX = moveX - startX;
					int disY = moveY - startY;
					
					// 获取移动后的iv_drag位置
					params.x = params.x + disX;
					params.y = params.y + disY;
					
					// 容错处理  防止toast被拖动到外面去
					if(params.x<0){
						params.x = 0;
					}
					if(params.x+mView.getWidth()>mScreenWidth){
						params.x = mScreenWidth - mView.getWidth();
					}
					if(params.y<0){
						params.y = 0;
					}
					if(params.y+mView.getHeight()>mScreenHeight-22){
						params.y = mScreenHeight-22 - mView.getHeight();
					}
					
					// 告知窗体按手势移动去做更新
					mWM.updateViewLayout(mView, params);
					
					// 更新起始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				
				//抬起
				case MotionEvent.ACTION_UP:
					// 记录iv_drag左上角的位置
					SpUtils.putIntSp(getApplicationContext(), ConstantValue.LOCATION_X, params.x);
					SpUtils.putIntSp(getApplicationContext(), ConstantValue.LOCATION_Y, params.y);
					break;
				}
				
				return true;
			}
		});
        
        // 设置查询的归属地文字
        TextView tv_addresstoast = (TextView) mView.findViewById(R.id.tv_addresstoast);
        String address = AddressDao.getAddress(incomingNumber, getApplicationContext());
        tv_addresstoast.setText(address);
        // 背景图片资源id
        int[] drawableId = new int[]{R.drawable.call_locate_white,
        							 R.drawable.call_locate_orange,
        							 R.drawable.call_locate_blue,
        							 R.drawable.call_locate_gray,
        							 R.drawable.call_locate_green};
        int idIndex = SpUtils.getIntSp(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        // 设置背景
        tv_addresstoast.setBackgroundResource(drawableId[idIndex]);
        // 将toast view添加到窗体上  需要加权限
        mWM.addView(mView, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消监听
		if(mTM!=null && mMyPhoneStateListener!=null){
			mTM.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		
		// 当服务销毁时  取消注册广播
		if(mInnerOutCallReceiver!=null){
			unregisterReceiver(mInnerOutCallReceiver);
		}
		
	}
}

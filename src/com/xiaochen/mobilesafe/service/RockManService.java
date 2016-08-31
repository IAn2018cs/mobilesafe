package com.xiaochen.mobilesafe.service;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.activity.RockBackgroundActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class RockManService extends Service {
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mRockView;
	private WindowManager mWM;
	private int mScreenWidth;
	private int mScreenHeight;
	private WindowManager.LayoutParams params;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			params.y = (Integer) msg.obj;
			mWM.updateViewLayout(mRockView, params);
		};
	};

	@Override
	public void onCreate() {
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		// 展示小火箭
		showRock();
		super.onCreate();
	}

	private void showRock() {
		// 展示自定义的toast params属性值
		params = mParams;
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
		// 自定义一个view
		mRockView = View.inflate(this, R.layout.rock_man_view, null);
		
		ImageView iv_rock = (ImageView) mRockView.findViewById(R.id.iv_rock);
		AnimationDrawable drawable = (AnimationDrawable) iv_rock.getBackground();
		drawable.start();
		
		// 将小火箭挂载到窗体上(加权限)
		mWM.addView(mRockView, params);
		
		// 设置滑动事件
		iv_rock.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE:
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					
					int disX = moveX - startX;
					int disY = moveY - startY;
					
					// 移动后的params
					params.x = params.x + disX;
					params.y = params.y + disY;
					
					// 容错处理  防止toast被拖动到外面去
					if(params.x<0){
						params.x = 0;
					}
					if(params.x+mRockView.getWidth()>mScreenWidth){
						params.x = mScreenWidth - mRockView.getWidth();
					}
					if(params.y<0){
						params.y = 0;
					}
					if(params.y+mRockView.getHeight()>mScreenHeight-22){
						params.y = mScreenHeight-22 - mRockView.getHeight();
					}
					
					// 告知窗体按手势移动去做更新
					mWM.updateViewLayout(mRockView, params);
					
					// 更新起始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
				case MotionEvent.ACTION_UP:
					if(params.x>(mScreenWidth/2-100) && params.x<(mScreenWidth/2+100) && params.y>mScreenHeight*3/4){
						// 发射火箭
						sendRock();
						// 开启一个尾气动画  (透明的activity)  启动方式为新开一个任务栈
						Intent intent = new Intent(getApplicationContext(), RockBackgroundActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
					break;
				}
				// 图片本身没有点击事件  要想响应滑动事件  需要返回true
				return true;
			}
		});
	}

	protected void sendRock() {
		// 设置火箭居中
		params.x = mScreenWidth/2 - mRockView.getWidth()/2;
		mWM.updateViewLayout(mRockView, params);
		new Thread(){
			public void run() {
				int height = mScreenHeight*3/4;
				for(int i=0;height>0;i++){
					height = height-i*35;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message msg = Message.obtain();
					msg.obj = height;
					mHandler.sendMessage(msg);
				}
			};
		}.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		if(mWM!=null && mRockView!=null){
			mWM.removeView(mRockView);
		}
		super.onDestroy();
	}
}

package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ToastLocationActivity extends Activity {
	private ImageView iv_drag;
	private Button bt_top;
	private Button bt_bottom;
	private WindowManager mWM;
	private int mScreenWidth;
	private int mScreenHeight;
//	private long startTime = 0;
	private long[] mHit = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);
		 //将状态栏设置为透明
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		initUI();
	}

	private void initUI() {
		// 要拖拽的图片
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		// 上下两个按钮
		bt_top = (Button) findViewById(R.id.bt_top);
		bt_bottom = (Button) findViewById(R.id.bt_bottom);
		// 获取屏幕的宽和高
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		
		// 回显上次拖动的位置   iv_drag在RelativeLayout中   所以其所在位置规则由它提供
		int locationX = SpUtils.getIntSp(getApplicationContext(), ConstantValue.LOCATION_X, 0);
		int locationY = SpUtils.getIntSp(getApplicationContext(), ConstantValue.LOCATION_Y, 30);
		// 设置iv_drag的宽和高都包裹内容
		android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = locationX;
		params.topMargin = locationY;
		iv_drag.setLayoutParams(params);
		
		// 回显按钮的位置
		if(locationY>(mScreenHeight-22)/2){
			bt_top.setVisibility(View.VISIBLE);
			bt_bottom.setVisibility(View.INVISIBLE);
		}else{
			bt_top.setVisibility(View.INVISIBLE);
			bt_bottom.setVisibility(View.VISIBLE);
		}
		
		// 监听iv_drag触摸事件
		iv_drag.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

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
					int top = iv_drag.getTop() + disY;
					int bottom = iv_drag.getBottom() + disY;
					int left = iv_drag.getLeft() + disX;
					int right = iv_drag.getRight() + disX;
					
					// 防止iv_drag被拖动到外面去
					if(left<0 || right>mScreenWidth || top<30 || bottom>mScreenHeight-0){
						return true;
					}
					
					// 当iv_drag的top(距离可显示区域的顶部的距离)大于可显示区域的一半  就让下面的button隐藏 显示上面的button
					if(top>(mScreenHeight-22)/2){
						bt_top.setVisibility(View.VISIBLE);
						bt_bottom.setVisibility(View.INVISIBLE);
					}else{
						bt_top.setVisibility(View.INVISIBLE);
						bt_bottom.setVisibility(View.VISIBLE);
					}
					
					// 将iv_drag按移动后的位置展示
					iv_drag.layout(left, top, right, bottom);
					
					// 更新起始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				
				//抬起
				case MotionEvent.ACTION_UP:
					// 记录iv_drag左上角的位置
					SpUtils.putIntSp(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtils.putIntSp(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
					break;
				}
				
				// 如果只有触摸事件，没有点击事件，返回true才响应事件
				// 如果既有触摸事件，又有点击事件，需要返回false
				return false;
			}
		});
		
		// 给iv_drag设置双击居中
		iv_drag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 第一种方法只能实现双击事件
				/*if(startTime!=0){
					long endTime = System.currentTimeMillis();
					if(endTime-startTime < 500){
					}
				}
				startTime = System.currentTimeMillis();*/
				// 采用谷歌的拷贝数组方法 可以实现多击事件
				// 参数(要拷贝的数组,要拷贝数组的起始位置,目标数组,拷贝到目标数组的起始位置,要拷贝的长度(次数))
				System.arraycopy(mHit, 1, mHit, 0, mHit.length-1);
				mHit[mHit.length-1] = SystemClock.uptimeMillis();
				if(mHit[mHit.length-1]-mHit[0] < 500){
					int left = mScreenWidth/2 - iv_drag.getWidth()/2;
					int top = (mScreenHeight-22)/2 - iv_drag.getHeight()/2;
					int right = mScreenWidth/2 + iv_drag.getWidth()/2;
					int bottom = (mScreenHeight-22)/2 + iv_drag.getHeight()/2;
					
					// 按照上面的规则显示
					iv_drag.layout(left, top, right, bottom);
					
					// 记录位置
					SpUtils.putIntSp(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtils.putIntSp(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
				}
			}
		});
	}
}

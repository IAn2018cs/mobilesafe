package com.xiaochen.mobilesafe.activity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class SetSelfPagerActivity extends StatusBarColorActivity {
	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 创建手势管理对象 用作管理在onTouchEvent(event)传递过来的手势动作
		gestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						// e1为按下的 e2为抬起的
						if (e1.getX() - e2.getX() > 0) {
							// 由右向左滑动 下一页
							showNextPage();
						}

						if (e1.getX() - e2.getX() < 0) {
							// 由左向右滑动 上一页
							showPrePage();
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}

	// 下一页按钮点击事件
	public void nextClick(View v) {
		showNextPage();
	}

	// 上一页按钮点击事件
	public void previousClick(View v) {
		showPrePage();
	}

	// 上一页的抽象方法 具体由子类实现
	public abstract void showPrePage();

	// 下一页的抽象方法 具体由子类实现
	public abstract void showNextPage();

	// 监听屏幕上的手势事件 (按下 滑动 抬起)
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 通过手势处理类 接收手势事件 用作处理
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}

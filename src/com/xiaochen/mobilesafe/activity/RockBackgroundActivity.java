package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class RockBackgroundActivity extends Activity {
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			finish();
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rock_bg);
		 //将状态栏设置为透明
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		initUI();
	}

	private void initUI() {
		ImageView iv_rock_top = (ImageView) findViewById(R.id.iv_rock_top);
		ImageView iv_rock_bottom = (ImageView) findViewById(R.id.iv_rock_bottom);
		
		// 开启一个透明动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);
		iv_rock_top.startAnimation(alphaAnimation);
		iv_rock_bottom.startAnimation(alphaAnimation);
		
		// 发一条消息  一秒钟后执行
		mHandler.sendEmptyMessageDelayed(0, 1000);
	}
}

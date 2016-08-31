package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.utlis.SystemBarTintManager;

import android.annotation.TargetApi;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost.TabSpec;

public class BaseTestActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(R.color.top_bg_color);// 通知栏所需颜色
		}
		
		
		setContentView(R.layout.activity_tab_basetest);
		// 生成选项卡		newTabSpec为添加一个唯一标识		setIndicator为添加内容(可以为自定义的View)	  
		TabSpec tab1 = getTabHost().newTabSpec("tab1").setIndicator("缓存清理");
		TabSpec tab2 = getTabHost().newTabSpec("tab2").setIndicator("手机杀毒");
		// 设置选项卡内容   即点击选项卡后的操作  可以添加intent  view id等
		tab1.setContent(new Intent(this,CleanCacheActivity.class));
		tab2.setContent(new Intent(this,MobileAntiVirusActivity.class));
		// 将选项卡添加到host(宿主)中去
		getTabHost().addTab(tab1);
		getTabHost().addTab(tab2);
	}
	
	// 改变状态栏颜色
	@TargetApi(19)
	public void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
}

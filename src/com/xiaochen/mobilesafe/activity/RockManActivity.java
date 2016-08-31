package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.service.RockManService;
import com.xiaochen.mobilesafe.utlis.ServiceUtil;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RockManActivity extends StatusBarColorActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rock_man);
		initUI();
	}

	private void initUI() {
		Button bt_start = (Button) findViewById(R.id.bt_start); 
		Button bt_stop = (Button) findViewById(R.id.bt_stop);
		// 开启火箭(服务)
		bt_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startService(new Intent(getApplicationContext(), RockManService.class));
				finish();
			}
		});
		// 关闭火箭(服务)
		bt_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ServiceUtil.isRunning(getApplicationContext(), "com.xiaochen.mobilesafe.service.RockManService")){
					stopService(new Intent(getApplicationContext(), RockManService.class));
					finish();
				}else{
					ToastUtli.show(getApplicationContext(), "您没有开启小火箭");
				}
			}
		});
	}
}

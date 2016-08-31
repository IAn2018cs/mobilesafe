package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.service.ScreenLockService;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.ServiceUtil;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends StatusBarColorActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		
		// 显示系统进程的方法
		initSystemShow();
		
		// 锁屏清理内存的方法
		initScreenLockClear();
	}

	private void initScreenLockClear() {
		final CheckBox cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
		
		// 回显
		boolean isRuning = ServiceUtil.isRunning(this, "com.xiaochen.mobilesafe.service.ScreenLockService");
		cb_lock_clear.setChecked(isRuning);
		if(isRuning){
			cb_lock_clear.setText("锁屏清理已开启");
		}else{
			cb_lock_clear.setText("锁屏清理已关闭");
		}
		
		// 监听checkbox的状态改变
		cb_lock_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			// isChecked为改变后的状态
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// 根据选中状态去更改文字
				if(isChecked){
					cb_lock_clear.setText("锁屏清理已开启");
					startService(new Intent(getApplicationContext(), ScreenLockService.class));
					// 提示用户
					ToastUtli.show(getApplicationContext(), "当锁屏时会自动清理内存");
				}else{
					cb_lock_clear.setText("锁屏清理已关闭");
					stopService(new Intent(getApplicationContext(), ScreenLockService.class));
				}
			}
		});
	}

	private void initSystemShow() {
		final CheckBox cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		
		// 回显
		boolean showSystem = SpUtils.getBoolSp(this, ConstantValue.PROCESS_SYSTEM_SHOW, false);
		cb_show_system.setChecked(showSystem);
		if(showSystem){
			cb_show_system.setText("显示系统进程");
		}else{
			cb_show_system.setText("隐藏系统进程");
		}
		
		// 监听checkbox的状态改变
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			// isChecked为改变后的状态
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// 根据选中状态去更改文字
				if(isChecked){
					cb_show_system.setText("显示系统进程");
				}else{
					cb_show_system.setText("隐藏系统进程");
				}
				// 存储选中状态
				SpUtils.putBoolSp(getApplicationContext(), ConstantValue.PROCESS_SYSTEM_SHOW, isChecked);
			}
		});
	}
}

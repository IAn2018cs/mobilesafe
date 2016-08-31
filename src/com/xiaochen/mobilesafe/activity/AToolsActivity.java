package com.xiaochen.mobilesafe.activity;


import java.io.File;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.engine.SmsBef;
import com.xiaochen.mobilesafe.engine.SmsBef.CallBack;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AToolsActivity extends StatusBarColorActivity {
	private TextView atools_address;
	private TextView atools_sms_bef;
	private TextView atools_common_use;
	private TextView atools_app_lock;
	private TextView atools_rock;
	private TextView developer_test;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);

		// 归属地查询
		mobileNoTrack();
		// 短信备份
		smsBef();
		// 常用号码查询
		commonUse();
		// 程序锁
		appLock();
		// 小火箭
		rockMan();
		// 开发者测试
		developerTest();
	}

	private void developerTest() {
		if(SpUtils.getBoolSp(this, ConstantValue.OPEN_DEVELOPER, false)){
			developer_test = (TextView) findViewById(R.id.developer_test);
			View view = findViewById(R.id.developer_test_bottom);
			developer_test.setVisibility(View.VISIBLE);
			view.setVisibility(View.VISIBLE);
			developer_test.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(getApplicationContext(), BaseTestActivity.class));
				}
			});
		}
	}

	private void rockMan() {
		atools_rock = (TextView) findViewById(R.id.atools_rock);
		atools_rock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), RockManActivity.class));
			}
		});
	}

	private void appLock() {
		atools_app_lock = (TextView) findViewById(R.id.atools_app_lock);
		atools_app_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), AppLockActivity.class));
			}
		});
	}

	private void commonUse() {
		atools_common_use = (TextView) findViewById(R.id.atools_common_use);
		atools_common_use.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), CommonNumberActivity.class));
			}
		});
	}

	private void smsBef() {
		atools_sms_bef = (TextView) findViewById(R.id.atools_sms_bef);
		atools_sms_bef.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSmsBefDialog();
			}
		});
	}

	protected void showSmsBefDialog() {
		// 创建进度条对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.fdn);
		progressDialog.setTitle("短信备份");
		// 设置样式为水平
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
		
		// 备份短信  可能为耗时操作  所以在子线程里
		new Thread(){
			public void run() {
				// 获取文件存储路径
				String name = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"smsBackup.xml";
//				String name = "smsBackup.xml";
				// 短信备份
				SmsBef.backUp(getApplicationContext(), name, new CallBack() {
					@Override
					public void setProgress(int index) {
						progressDialog.setProgress(index);
					}
					
					@Override
					public void setMax(int max) {
						progressDialog.setMax(max);
					}

					@Override
					public void dismissDialog() {
						progressDialog.dismiss();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ToastUtli.show(getApplicationContext(), "备份完成");
							}
						});
						
					}
				});
				
			};
		}.start();
	}

	// 归属地查询
	private void mobileNoTrack() {
		atools_address = (TextView) findViewById(R.id.atools_address);
		atools_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(AToolsActivity.this, MobileNoTrack.class));
			}
		});
	}
}

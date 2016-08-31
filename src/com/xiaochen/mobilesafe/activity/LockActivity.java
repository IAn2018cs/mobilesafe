package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LockActivity extends Activity {
	private ImageView iv_icon;
	private TextView tv_name;
	private EditText et_pwd;
	private Button bt_submit;
	private PackageManager mPM;
	private UpdateLockUIReceiver mUpdateLockUIReceiver;
	private String mPackageName;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lock);
		// 将状态栏设置为透明
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        
        initUI();
        
        // 注册一个广播  当收到看门狗发来带包名的广播时  更新UI
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.xiaochen.LOCK_DOG");
        mUpdateLockUIReceiver = new UpdateLockUIReceiver();
        registerReceiver(mUpdateLockUIReceiver, intentFilter);
	}
	
	// 更新UI的广播
	class UpdateLockUIReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			mPackageName = intent.getStringExtra("packagename");
			resumeUI(mPackageName);
		}
	}
	
	// 更新UI
	private void resumeUI(String packagename) {
		// 获取包管理者 用来获取应用信息
		mPM = getPackageManager();
		try {
			// 获取应用信息
			ApplicationInfo applicationInfo = mPM.getApplicationInfo(packagename, 0);
			// 设置应用图标
			iv_icon.setImageDrawable(applicationInfo.loadIcon(mPM));
			// 设置应用名称
			tv_name.setText(applicationInfo.loadLabel(mPM).toString());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initUI() {
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_name = (TextView) findViewById(R.id.tv_name);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		bt_submit = (Button) findViewById(R.id.bt_submit);
		
		// 获取包管理者 用来获取应用信息
		mPM = getPackageManager();
		// 获得传过来的包名
		mPackageName = getIntent().getStringExtra("packagename");
		try {
			// 获取应用信息
			ApplicationInfo applicationInfo = mPM.getApplicationInfo(mPackageName, 0);
			// 设置应用图标
			iv_icon.setImageDrawable(applicationInfo.loadIcon(mPM));
			// 设置应用名称
			tv_name.setText(applicationInfo.loadLabel(mPM).toString());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		// 监听EditText文字变化
		et_pwd.addTextChangedListener(new TextWatcher() {
			// 文字变化时
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			// 文字变化前
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			// 文字变化后
			@Override
			public void afterTextChanged(Editable s) {
				String spPwd = SpUtils.getStringSp(getApplicationContext(), ConstantValue.APP_LOCK_PWD, "");
				String pwd = et_pwd.getText().toString().trim();
				if(pwd.equals(spPwd)){
					// 解锁后给服务发一条已解锁的广播
					Intent intent = new Intent("com.xiaochen.SKIP");
					intent.putExtra("packagename", mPackageName);
					sendBroadcast(intent);
					finish();
				}
			}
		});
		
		// 按钮的点击事件
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String spPwd = SpUtils.getStringSp(getApplicationContext(), ConstantValue.APP_LOCK_PWD, "");
				String pwd = et_pwd.getText().toString().trim();
				if(!TextUtils.isEmpty(pwd)){
					if(pwd.equals(spPwd)){
						// 解锁后给服务发一条已解锁的广播
						Intent intent = new Intent("com.xiaochen.SKIP");
						intent.putExtra("packagename", mPackageName);
						sendBroadcast(intent);
						finish();
					}else{
						ToastUtli.show(getApplicationContext(), "密码错误");
					}
				}else{
					ToastUtli.show(getApplicationContext(), "密码不能为空");
				}
			}
		});
	}
	
	// 重写返回键
	@Override
	public void onBackPressed() {
		// 通过隐示意图 开启桌面
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		// 注销广播
		if(mUpdateLockUIReceiver!=null){
			unregisterReceiver(mUpdateLockUIReceiver);
		}
		super.onDestroy();
	}
}

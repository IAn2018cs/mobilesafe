package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.receiver.DeviceAdmin;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SetSelf4Activity extends SetSelfPagerActivity {
	private CheckBox cb_self_open;
	private Button bt_admin;
	private ComponentName mDeviceAdminSample;
	private DevicePolicyManager mDPM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setself4);
		initUI();
	}

	private void initUI() {
		// 创建一个组件对象 传进上下文环境和继承DeviceAdminReceiver广播的字节码文件
		mDeviceAdminSample = new ComponentName(this, DeviceAdmin.class);

		// 获取设备管理者对象
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		cb_self_open = (CheckBox) findViewById(R.id.cb_self_open);
		bt_admin = (Button) findViewById(R.id.bt_admin);
		// 回显数据
		if (SpUtils.getBoolSp(getApplicationContext(), ConstantValue.OPEN_SELF,
				false)) {
			cb_self_open.setChecked(true);
			cb_self_open.setText("您已经开启防盗保护");
		} else {
			cb_self_open.setChecked(false);
			cb_self_open.setText("您没有开启防盗保护");
		}
		// 监听CheckBox的状态改变
		cb_self_open.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// isChecked为点击后的状态 存储在sp中
				SpUtils.putBoolSp(getApplicationContext(),
						ConstantValue.OPEN_SELF, isChecked);
				// 根据选中状态 更改文字
				if (isChecked) {
					cb_self_open.setText("您已经开启防盗保护");
				} else {
					cb_self_open.setText("您没有开启防盗保护");
				}
			}
		});

		// 给按钮设置点击事件
		bt_admin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mDPM.isAdminActive(mDeviceAdminSample)) {
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							mDeviceAdminSample);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"手机卫士");
					startActivity(intent);
				}else{
					ToastUtli.show(getApplicationContext(), "您已经激活设备管理员");
				}
			}
		});
	}

	@Override
	public void showPrePage() {
		Intent intent = new Intent(this, SetSelf3Activity.class);
		startActivity(intent);
		finish();
		// 设置进入 退出 平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);

	}

	@Override
	public void showNextPage() {
		boolean open_self = SpUtils.getBoolSp(getApplicationContext(),
				ConstantValue.OPEN_SELF, false);
		if (open_self && mDPM.isAdminActive(mDeviceAdminSample)) {
			SpUtils.putBoolSp(this, ConstantValue.SETUP_OVER, true);
			Intent intent = new Intent(this, SetupOverActivity.class);
			startActivity(intent);
			finish();
			// 设置进入 退出 平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else if (!open_self) {
			ToastUtli.show(getApplicationContext(), "请开启防盗保护");
		} else if (!mDPM.isAdminActive(mDeviceAdminSample)) {
			ToastUtli.show(getApplicationContext(), "请激活设备管理器");
		}
	}

}

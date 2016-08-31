package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;
import com.xiaochen.mobilesafe.view.SettingCheckItemView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class SetSelf2Activity extends SetSelfPagerActivity {
	private SettingCheckItemView set_sim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setself2);

		initUI();
	}

	private void initUI() {
		set_sim = (SettingCheckItemView) findViewById(R.id.set_sim);
		// 读取条目的选中状态，用作显示，(根据sp中是否存储了sim卡的序列号来判断)
		String simNumber = SpUtils.getStringSp(this, ConstantValue.SIM_NUMBER,
				"");
		// 判断存储序列卡号是否为空""
		if (TextUtils.isEmpty(simNumber)) {
			// 如果为空就给条目设置为未选中状态
			set_sim.setCheck(false);
		} else {
			// 不为空就设置为选中状态
			set_sim.setCheck(true);
		}
		// 给自定义控件注册点击事件
		set_sim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取条目的选中状态
				boolean isCheck = set_sim.isCheck();
				// 取反后设置给条目
				set_sim.setCheck(!isCheck);
				// 再根据选中状态去存储sim卡序列号
				if (set_sim.isCheck()) {
					// 如果选中就存储sim卡序列
					// 获取TelephonyManager
					TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String simSerialNumber = manager.getSimSerialNumber();
					SpUtils.putStringSp(getApplicationContext(),
							ConstantValue.SIM_NUMBER, simSerialNumber);
				} else {
					// 删掉sp中sim卡序列号的节点
					SpUtils.remove(getApplicationContext(),
							ConstantValue.SIM_NUMBER);
				}
			}
		});
	}

	@Override
	public void showPrePage() {
		Intent intent = new Intent(this, SetSelf1Activity.class);
		startActivity(intent);
		finish();
		// 设置进入 退出 平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}

	@Override
	public void showNextPage() {
		if (set_sim.isCheck()) {
			Intent intent = new Intent(this, SetSelf3Activity.class);
			startActivity(intent);
			finish();
			// 设置进入 退出 平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			ToastUtli.show(getApplicationContext(), "请绑定SIM卡");
		}
	}
}

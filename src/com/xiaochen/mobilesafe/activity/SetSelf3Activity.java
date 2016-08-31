package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class SetSelf3Activity extends SetSelfPagerActivity {
	private EditText et_phone_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setself3);

		initUI();
	}

	private void initUI() {
		String phone = SpUtils.getStringSp(this,
				ConstantValue.CONTACT_PHONE_NUMBER, "");
		// 读取sp中的数据 进行回显
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		et_phone_number.setText(phone);
	}

	// 联系人的按钮点击事件
	public void contactClick(View v) {
		Intent intent = new Intent(SetSelf3Activity.this,
				ContactListActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String phone = data.getStringExtra("phone");
			// 将字符串中的-和空格替换成空字符
			phone = phone.replace("+86", "").replace("-", "").replace(" ", "")
					.trim();
			et_phone_number.setText(phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void showPrePage() {
		Intent intent = new Intent(this, SetSelf2Activity.class);
		startActivity(intent);
		finish();
		// 设置进入 退出 平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);

	}

	@Override
	public void showNextPage() {
		String phone = et_phone_number.getText().toString().trim();
		// 判断edittext是否为空字符
		if (!TextUtils.isEmpty(phone)) {
			Intent intent = new Intent(this, SetSelf4Activity.class);
			startActivity(intent);
			finish();
			// 设置进入 退出 平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);

			SpUtils.putStringSp(this, ConstantValue.CONTACT_PHONE_NUMBER, phone);
		} else {
			ToastUtli.show(getApplicationContext(), "请输入手机号码");
		}

	}
}

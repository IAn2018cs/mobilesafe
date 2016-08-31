package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SetupOverActivity extends StatusBarColorActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean setup_over = SpUtils.getBoolSp(this, ConstantValue.SETUP_OVER,
				false);
		if (setup_over) {
			setContentView(R.layout.activity_setself_over);
			initUI();
		} else {
			Intent intent = new Intent(this, SetSelf1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	private void initUI() {
		String phone = SpUtils.getStringSp(this,
				ConstantValue.CONTACT_PHONE_NUMBER, "");
		boolean open_self = SpUtils.getBoolSp(getApplicationContext(),
				ConstantValue.OPEN_SELF, false);
		TextView tv_self_phone = (TextView) findViewById(R.id.tv_self_phone);
		TextView tv_reset = (TextView) findViewById(R.id.tv_reset);
		TextView tv_set_pwd = (TextView) findViewById(R.id.tv_set_pwd);
		ImageView iv_self_lock = (ImageView) findViewById(R.id.iv_self_lock);
		// 回显存在sp中的安全号码
		tv_self_phone.setText(phone);

		// 根据sp中的是否开启防盗保护 来设置锁的图片样式
		if (open_self) {
			iv_self_lock.setBackgroundResource(R.drawable.lock);
		} else {
			iv_self_lock.setBackgroundResource(R.drawable.unlock);
		}

		// 设置textview重新进入设置向导的点击事件
		tv_reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SpUtils.putBoolSp(getApplicationContext(),
						ConstantValue.SETUP_OVER, false);
				Intent intent = new Intent(SetupOverActivity.this,
						SetSelf1Activity.class);
				startActivity(intent);
				finish();
			}
		});

		// 设置textview设置锁屏密码的点击事件
		tv_set_pwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSetPwdDialog();
			}
		});
	}

	// 设置密码对话框
	private void showSetPwdDialog() {
		// 自定义对话框 要用dialog.setView(view); 来设置
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		// 将一个xml转换成一个view对象
		View view = View.inflate(this, R.layout.dialog_set_pwd, null);
		// 兼容低版本 去掉对话框的内边距
		dialog.setView(view, 0, 0, 0, 0);
		// dialog.setView(view);
		dialog.show();

		// 找到相应的控件
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		final EditText et_set_pwd = (EditText) view
				.findViewById(R.id.et_set_pwd);
		final EditText et_confirm_pwd = (EditText) view
				.findViewById(R.id.et_confirm_pwd);

		// 确认的按钮
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取edittext的内容
				String set_pwd = et_set_pwd.getText().toString().trim();
				String confirm_pwd = et_confirm_pwd.getText().toString().trim();

				if (TextUtils.isEmpty(set_pwd)
						|| TextUtils.isEmpty(confirm_pwd)) {
					ToastUtli.show(getApplicationContext(), "密码不能为空");
				} else if (set_pwd.equals(confirm_pwd)) {
					// 将加密后的密码存在sp中
					SpUtils.putStringSp(getApplicationContext(),
							ConstantValue.REMOTE_LOCK_PASWORD, set_pwd);
					dialog.dismiss();
				} else {
					ToastUtli.show(getApplicationContext(), "两次密码不一致");
				}
			}
		});

		// 取消的按钮
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
}

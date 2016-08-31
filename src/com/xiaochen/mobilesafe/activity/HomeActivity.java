package com.xiaochen.mobilesafe.activity;

import net.youmi.android.normal.banner.BannerManager;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.Md5Util;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends StatusBarColorActivity {
	private GridView gv_home;
	private String[] mStrDatas;
	private int[] mDrawableDatas;
	private LinearLayout adLayout;
	private View adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		initUI();
		// 初始化数据
		initData();
		// 设置点击事件
		initClick();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 判断是否显示广告
		if(SpUtils.getBoolSp(this, ConstantValue.OPEN_AD, true)){
			adLayout.setVisibility(View.VISIBLE);
		}else{
			adLayout.setVisibility(View.GONE);
		}
	}
	
	// GridView的条目点击事件
	private void initClick() {
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				// 手机防盗
				case 0:
					// 显示一个输入密码对话框
					showDialog();
					break;

				// 通信卫士
				case 1:
					startActivity(new Intent(HomeActivity.this, BlackNumberActivity.class));
					break;

				// 软件管理
				case 2:
					startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
					break;

				// 进程管理
				case 3:
					startActivity(new Intent(HomeActivity.this, ProcessManagerActivity.class));
					break;

				// 流量统计
				case 4:
					startActivity(new Intent(HomeActivity.this, DataTrafficActivity.class));
					break;

				// 手机杀毒
				case 5:
					startActivity(new Intent(HomeActivity.this, MobileAntiVirusActivity.class));
					break;

				// 缓存清理
				case 6:
					startActivity(new Intent(HomeActivity.this, CleanCacheActivity.class));
					break;

				// 高级工具
				case 7:
					startActivity(new Intent(HomeActivity.this,
							AToolsActivity.class));
					break;
				// 设置中心
				case 8:
					startActivity(new Intent(HomeActivity.this,
							SettingActivity.class));
					break;
				}
			}
		});
	}

	// 显示一个对话框
	protected void showDialog() {
		// 判断是否为第一次点开 即sp中是否存有密码 //或者给默认值为"" 然后调用TextUtils.isEmpty(值) 来判断
		if (SpUtils.getStringSp(this, ConstantValue.MOBLE_SAFE_PSD, null) == null) {
			// 初始设置密码对话框
			showSetPwdDialog();
		} else {
			// 确认密码对话框
			showConfirmPwdDialog();
		}
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
							ConstantValue.MOBLE_SAFE_PSD,
							Md5Util.encryption(set_pwd));
					Intent intent = new Intent(getApplicationContext(),
							SetupOverActivity.class);
					startActivity(intent);
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

	// 确认密码对话框
	private void showConfirmPwdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_confirm_pwd, null);
		// 兼容低版本 去掉对话框的内边距
		dialog.setView(view, 0, 0, 0, 0);
		// dialog.setView(view);
		dialog.show();

		// 找到相应的控件
		Button bt_con_submit = (Button) view.findViewById(R.id.bt_con_submit);
		Button bt_con_cancel = (Button) view.findViewById(R.id.bt_con_cancel);
		final EditText et_pwd = (EditText) view.findViewById(R.id.et_pwd);

		bt_con_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd = et_pwd.getText().toString().trim();
				// 先将输入的密码加密 再进行比对
				// pwd = Md5Util.encoder(pwd);
				String spPwd = SpUtils.getStringSp(getApplicationContext(),
						ConstantValue.MOBLE_SAFE_PSD, null);

				if (TextUtils.isEmpty(pwd)) {
					ToastUtli.show(getApplicationContext(), "密码不能为空");
				} else if (Md5Util.encryption(pwd).equals(spPwd)) {
					Intent intent = new Intent(getApplicationContext(),
							SetupOverActivity.class);
					startActivity(intent);
					dialog.dismiss();
				} else {
					ToastUtli.show(getApplicationContext(), "密码不正确");
				}
			}
		});

		bt_con_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	// 初始化数据
	private void initData() {
		// 设置GridView文字数据
		mStrDatas = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计",
				"手机杀毒", "缓存清理", "高级工具", "设置中心" };
		// 设置GridView图片数据
		mDrawableDatas = new int[] { R.drawable.home_safe,
				R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager,
				R.drawable.home_trojan, R.drawable.home_sysoptimize,
				R.drawable.home_tools, R.drawable.home_settings };
		// 设置GridView数据适配器
		gv_home.setAdapter(new MyAdapter());
	}

	// 数据适配器
	class MyAdapter extends BaseAdapter {
		// 返回个数
		@Override
		public int getCount() {
			return mStrDatas.length;
		}

		// 返回每个item对象
		@Override
		public Object getItem(int position) {
			return mStrDatas[position];
		}

		// 返回item的id
		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.gridview_item, null);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			iv_icon.setImageResource(mDrawableDatas[position]);
			tv_title.setText(mStrDatas[position]);
			return view;
		}

	}

	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);
		// 获取要嵌入广告条的布局
		adLayout = (LinearLayout)findViewById(R.id.adLayout);
		// 实例化广告条
		adView = BannerManager.getInstance(this).getBanner(this);
		// 将广告条加入到布局中
		adLayout.addView(adView);
	}

	// 重写返回键  使其回到桌面
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// 通过隐示意图 开启桌面
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

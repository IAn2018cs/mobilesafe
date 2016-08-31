package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.service.AddressShowService;
import com.xiaochen.mobilesafe.service.AppLockDogService;
import com.xiaochen.mobilesafe.service.BlackNumberService;
import com.xiaochen.mobilesafe.utlis.ServiceUtil;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.view.SettingCheckItemView;
import com.xiaochen.mobilesafe.view.SettingClickItemView;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends StatusBarColorActivity {
	private String[] mToastStyleDes;
	private SettingClickItemView set_toast_style;
	private int mToastStyleIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		// 初始化更新的条目
		initUpDataItem();
		// 初始化显示来电归属地的条目
		initAttributionItem();
		// 初始化选择toast风格的条目
		initToastStyleItem();
		// 初始化设置toast位置的条目
		initToastLocationItem();
		// 初始化黑名单拦截设置的条目
		initBlackListItem();
		// 初始化程序锁设置的条目
		initAppLockItem();
		// 关于
		initInfo();
		// 广告设置
		initADSet();
	}

	private void initADSet() {
		final SettingCheckItemView set_open_ad = (SettingCheckItemView) findViewById(R.id.set_open_ad);
		// 获取sp中的值
		boolean isCheck = SpUtils.getBoolSp(getApplicationContext(),
				ConstantValue.OPEN_AD, true);
		// 将sp中的值设置给SettingItemView
		set_open_ad.setCheck(isCheck);
		// 给SettingItemView设置点击事件
		set_open_ad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取选中状态
				boolean check = set_open_ad.isCheck();
				// 取反并设置新的选中状态
				set_open_ad.setCheck(!check);
				// 更改sp中的值 并储存
				SpUtils.putBoolSp(getApplicationContext(),
						ConstantValue.OPEN_AD, !check);
			}
		});
	}

	private void initInfo() {
		SettingClickItemView set_info = (SettingClickItemView) findViewById(R.id.set_info);
		//设置标题
		set_info.setTitle("关于");
		//设置描述
		set_info.setDsc("o(^▽^)o");
		//设置点击事件
		set_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					startActivity(new Intent(SettingActivity.this,InFoActivity.class));	
			}
		});
	}

	private void initAppLockItem() {
		final SettingCheckItemView set_app_lock = (SettingCheckItemView) findViewById(R.id.set_app_lock);
		// 看服务是否在运行
		boolean isCheck = ServiceUtil.isRunning(this, "com.xiaochen.mobilesafe.service.AppLockDogService");
		// 将服务是否在运行的结果设置给SettingItemView
		set_app_lock.setCheck(isCheck);
		// 给SettingItemView设置点击事件
		set_app_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取选中状态
				boolean check = set_app_lock.isCheck();
				// 取反并设置新的选中状态
				set_app_lock.setCheck(!check);
				// 如果选中就开启服务  没有选中就关闭服务
				if(!check){
					startService(new Intent(getApplicationContext(), AppLockDogService.class));
				}else{
					stopService(new Intent(getApplicationContext(), AppLockDogService.class));
				}
			}
		});
	}

	private void initBlackListItem() {
		final SettingCheckItemView set_blacklist = (SettingCheckItemView) findViewById(R.id.set_blacklist);
		// 看服务是否在运行
		boolean isCheck = ServiceUtil.isRunning(this, "com.xiaochen.mobilesafe.service.BlackNumberService");
		// 将服务是否在运行的结果设置给SettingItemView
		set_blacklist.setCheck(isCheck);
		// 给SettingItemView设置点击事件
		set_blacklist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取选中状态
				boolean check = set_blacklist.isCheck();
				// 取反并设置新的选中状态
				set_blacklist.setCheck(!check);
				
				// 如果选中就开启服务  没有选中就关闭服务
				if(!check){
					startService(new Intent(getApplicationContext(), BlackNumberService.class));
				}else{
					stopService(new Intent(getApplicationContext(), BlackNumberService.class));
				}
			}
		});
	}

	private void initToastLocationItem() {
		SettingClickItemView set_toast_location = (SettingClickItemView) findViewById(R.id.set_toast_location);
		//设置标题
		set_toast_location.setTitle("来电归属地提示框位置");
		//设置描述
		set_toast_location.setDsc("设置来电归属地提示框位置");
		//设置点击事件
		set_toast_location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					startActivity(new Intent(SettingActivity.this,ToastLocationActivity.class));	
			}
		});
	}

	private void initToastStyleItem() {
		set_toast_style = (SettingClickItemView) findViewById(R.id.set_toast_style);
		//设置标题
		set_toast_style.setTitle("设置来电归属地显示风格");
		mToastStyleDes = new String[]{"透明","橙色","蓝色","灰色","绿色"};
		mToastStyleIndex = SpUtils.getIntSp(this, ConstantValue.TOAST_STYLE, 0);
		//设置描述
		set_toast_style.setDsc(mToastStyleDes[mToastStyleIndex]);
		//设置点击事件
		set_toast_style.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//展示一个单选对话框
				showSingleChoiceDialog();
			}
		});
		
	}
	
	protected void showSingleChoiceDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.mvl);
		builder.setTitle("请选择归属地样式");
		mToastStyleIndex = SpUtils.getIntSp(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
		// 设置单选框(选项内容, 被选中的条目索引, 监听事件)
		builder.setSingleChoiceItems(mToastStyleDes, mToastStyleIndex, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//记录点击的索引值
				SpUtils.putIntSp(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
				//关闭对话框
				dialog.dismiss();
				//回显选择对应风格的文字描述
				set_toast_style.setDsc(mToastStyleDes[which]);
			}
		});
		// 设置消极的按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		// 显示对话框
		builder.show();
	}

	private void initAttributionItem() {
		final SettingCheckItemView set_attribution = (SettingCheckItemView) findViewById(R.id.set_attribution);
		// 获取服务是否运行  如果运行就设置为选中状态
		boolean isRunning = ServiceUtil.isRunning(this, "com.xiaochen.mobilesafe.service.AddressShowService");
		set_attribution.setCheck(isRunning);
		set_attribution.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取选中状态
				boolean check = set_attribution.isCheck();
				// 取反并设置新的选中状态
				set_attribution.setCheck(!check);
				// 如果选中就开启服务  没有选中就关闭服务
				if(!check){
					startService(new Intent(getApplicationContext(),AddressShowService.class));
				}else{
					stopService(new Intent(getApplicationContext(),AddressShowService.class));
				}
			}
		});
	}

	private void initUpDataItem() {
		final SettingCheckItemView set_updata = (SettingCheckItemView) findViewById(R.id.set_updata);
		// 获取sp中的值
		boolean isCheck = SpUtils.getBoolSp(getApplicationContext(),
				ConstantValue.OPEN_UPDATE, false);
		// 将sp中的值设置给SettingItemView
		set_updata.setCheck(isCheck);
		// 给SettingItemView设置点击事件
		set_updata.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取选中状态
				boolean check = set_updata.isCheck();
				// 取反并设置新的选中状态
				set_updata.setCheck(!check);
				// 更改sp中的值 并储存
				SpUtils.putBoolSp(getApplicationContext(),
						ConstantValue.OPEN_UPDATE, !check);
			}
		});
	}
}

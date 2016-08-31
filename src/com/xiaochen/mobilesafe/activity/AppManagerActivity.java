package com.xiaochen.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.db.domain.AppInfo;
import com.xiaochen.mobilesafe.engine.AppInfoProvider;
import com.xiaochen.mobilesafe.utlis.ShowDialog;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManagerActivity extends StatusBarColorActivity implements OnClickListener {
	private ListView lv_applist;
	private List<AppInfo> mAppInfoList;
	private List<AppInfo> mUserAppInfoList;
	private List<AppInfo> mSystemAppInfoList;
	private MyAdapter mAdapter;
	private TextView tv_title;
	private AppInfo mAppInfo;
	private PopupWindow mPopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanager);
		initTitle();
		initListView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initDate();
	}

	private void initListView() {
		lv_applist = (ListView) findViewById(R.id.lv_applist);
		
		lv_applist.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			// 监听滑动
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(mUserAppInfoList!=null && mSystemAppInfoList!=null){
					if(firstVisibleItem < mUserAppInfoList.size()+1){
						tv_title.setText("用户应用("+mUserAppInfoList.size()+")");
					}else{
						tv_title.setText("系统应用("+mSystemAppInfoList.size()+")");
					}
				}
			}
		});
		
		lv_applist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0 || position == mUserAppInfoList.size()+1){
					// 标题
					return;
				}else{
					if(position < mUserAppInfoList.size()+1){
						mAppInfo = mUserAppInfoList.get(position-1); // 减掉一个标题
					}else{
						mAppInfo = mSystemAppInfoList.get(position-mUserAppInfoList.size()-2);  // 减掉用户应用的个数和两个标题
					}
					
					showPopupWindow(view);
				}
			}
		});
	}

	protected void showPopupWindow(View view) {
		View contentView = View.inflate(this, R.layout.popupwindow_layout, null);
		
		TextView tv_uninstall = (TextView) contentView.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) contentView.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) contentView.findViewById(R.id.tv_share);
		// 通过接口的方式设置点击事件
		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);
		
		// 设置动画(透明加缩放)
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(400);
		alphaAnimation.setFillAfter(true);
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(400);
		scaleAnimation.setFillAfter(true);
		//动画集合Set
		AnimationSet animationSet = new AnimationSet(true);
		//添加两个动画
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		
		// 创建窗体对象  指定宽和高
		mPopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		// 设置窗体背景  这里设置为透明
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		// 指定窗体位置
		mPopupWindow.showAsDropDown(view, 100, -view.getHeight());
		
		// 开启动画集合
		contentView.startAnimation(animationSet);
	}

	private void initDate() {
		ShowDialog.showDialog(AppManagerActivity.this);
		new Thread(){
			public void run() {
				mAppInfoList = AppInfoProvider.getAppInfo(getApplicationContext());
				mSystemAppInfoList = new ArrayList<AppInfo>();
				mUserAppInfoList = new ArrayList<AppInfo>();
				// 将集合分为用户应用集合和系统应用集合
				for (AppInfo appInfo : mAppInfoList) {
					if(appInfo.isSystem()){
						mSystemAppInfoList.add(appInfo);
					}else{
						mUserAppInfoList.add(appInfo);
					}
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						mAdapter = new MyAdapter();
						lv_applist.setAdapter(mAdapter);
						ShowDialog.dismiss();
					}
				});
			};
		}.start();
	}

	@SuppressLint("NewApi")
	private void initTitle() {
		TextView tv_rom_memory = (TextView) findViewById(R.id.tv_rom_memory);
		TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
		// 悬浮标题栏
		tv_title = (TextView) findViewById(R.id.tv_title);
		
		// 获取磁盘(内存)和sd卡可用空间
		long romSize = Environment.getDataDirectory().getFreeSpace();
		long sdSize = Environment.getExternalStorageDirectory().getFreeSpace();
		
		// Formatter.formatFileSize(this, romSize) 将字节转化为带有相应单位的字符串(MB G)
		tv_rom_memory.setText("内部存储可用:"+Formatter.formatFileSize(this, romSize));
		tv_sd_memory.setText("sd卡可用:"+Formatter.formatFileSize(this, sdSize));
		
		/*String romPath = Environment.getDataDirectory().getAbsolutePath();
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		long rom = getAvailSpace(romPath);
		long sd = getAvailSpace(sdPath);
		System.out.println("rom:"+Formatter.formatFileSize(this, rom));
		System.out.println("sd:"+Formatter.formatFileSize(this, sd));*/
	}

	/*@SuppressLint("NewApi")
	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);
		// 获取可用区块个数
		//long num = statFs.getAvailableBlocks();
		long num = statFs.getAvailableBlocksLong();
		// 获取区块的大小
		//long size = statFs.getBlockSize();
		long size = statFs.getBlockSizeLong();
		
		return num*size;
	}*/
	
	class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mAppInfoList.size()+2; // 加上两个标题条目
		}

		@Override
		public AppInfo getItem(int position) {
			if(position == 0 || position == mUserAppInfoList.size()+1){
				// 标题
				return null;
			}
			if(position < mUserAppInfoList.size()+1){
				return mUserAppInfoList.get(position-1); // 减掉一个标题
			}else{
				return mSystemAppInfoList.get(position-mUserAppInfoList.size()-2);  // 减掉用户应用的个数和两个标题
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		// 条目类型的个数  默认1个
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
		@Override
		public int getItemViewType(int position) {
			if(position == 0 || position == mUserAppInfoList.size()+1){
				return 0;
			}else{
				return 1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			switch (type) {
			// 为标题栏的时候
			case 0:
				TitleHolder titleHolder = null;
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.appinfo_title_item, null);
					
					titleHolder = new TitleHolder();
					titleHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
					convertView.setTag(titleHolder);
				}else{
					titleHolder = (TitleHolder) convertView.getTag();
				}
				
				if(position == 0){
					titleHolder.tv_title.setText("用户应用("+mUserAppInfoList.size()+")");
				}else{
					titleHolder.tv_title.setText("系统应用("+mSystemAppInfoList.size()+")");
				}
				break;
			// 为应用信息的时候
			case 1:
				ViewHolder viewHolder = null;
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.appinfo_item, null);
					
					viewHolder = new ViewHolder();
					viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
					viewHolder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
					convertView.setTag(viewHolder);
				}else{
					viewHolder = (ViewHolder) convertView.getTag();
				}
				
				viewHolder.iv_icon.setImageDrawable(getItem(position).getIcon());
				viewHolder.tv_name.setText(getItem(position).getName());
				if(getItem(position).isSdCard()){
					viewHolder.tv_location.setText("外置存储卡");
				}else{
					viewHolder.tv_location.setText("手机内存");
				}
				break;
			}
			
			return convertView;
		}
	}
	
	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_location;
	}
	static class TitleHolder {
		TextView tv_title;
	}
	
	// PopupWindow布局里的点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 卸载
		case R.id.tv_uninstall:
			if(mAppInfo.isSystem()){
				ToastUtli.show(getApplicationContext(), "系统应用不能卸载");
			}else{
				if(mAppInfo.getPackageName().equals("com.xiaochen.mobilesafe")){
					ToastUtli.show(getApplicationContext(), "该应用不能在此卸载");
				}else{
					Intent intent = new Intent("android.intent.action.DELETE");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:"+mAppInfo.getPackageName()));
					startActivity(intent);
				}
			}
			break;
		// 启动
		case R.id.tv_start:
			if(mAppInfo.getPackageName().equals("com.xiaochen.mobilesafe")){
				ToastUtli.show(getApplicationContext(), "该应用已经启动");
			}else{
				PackageManager pm = getPackageManager();
				Intent launchIntent = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
				if(launchIntent != null){
					startActivity(launchIntent);
				}else{
					ToastUtli.show(getApplicationContext(), "该应用不可启动");
				}
			}
			break;
		// 分享  这里暂时先用短信
		case R.id.tv_share:
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, "分享一个叫"+mAppInfo.getName()+"的应用，自己去应用商店搜吧。(我是不是傻，拿短信和你说这个( ▼-▼ ))");
			startActivity(intent);
			break;
		}
		// 关闭PopupWindow
		if(mPopupWindow !=null){
			mPopupWindow.dismiss();
		}
	}
}

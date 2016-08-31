package com.xiaochen.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.db.domain.AppInfo;
import com.xiaochen.mobilesafe.engine.AppInfoProvider;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity extends StatusBarColorActivity {
	private ImageView iv_clean_all, iv_scan_bottom, iv_scan_top, iv_no_clean;
	private TextView tv_name;
	private ProgressBar pb_cache;
	private LinearLayout ll_addview;
	private int index = 0;
	private long totalCacherSize = 0;  // 缓存总大小
	private RotateAnimation mRotateAnimation, mCleanAnimation;
	private int cacheAppNum = 0;
	private TranslateAnimation mTranslateAnim;
	private PackageManager mPm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cleancache);
		initAnim();
		initUI();
		initDate();
	}

	private void initDate() {
		// 获取包管理者对象
		mPm = getPackageManager();
		// 耗时操作  开启子线程
		new Thread(){
			public void run() {
				// 获取所有应用的信息
				List<AppInfo> appInfoList = AppInfoProvider.getAppInfo(getApplicationContext());
				// 设置进度条最大值
				pb_cache.setMax(appInfoList.size());
				// 循环遍历集合
				for (AppInfo info : appInfoList) {
					// 更新UI
					final AppInfo temInfo = info;
					runOnUiThread(new Runnable() {
						public void run() {
							// 更新正在扫描的文字
							tv_name.setText("正在扫描："+temInfo.getName());
							// 获取应用缓存大小(需要加权限) 从设置的源码中拷贝出来
							long cacheSize = temInfo.getCacheSize();
							if(cacheSize > 0){
								// 缓存应用的个数加一
								cacheAppNum++;
								// 缓存总大小累加
								totalCacherSize += cacheSize;
								// 创建一个条目
								View view = View.inflate(getApplicationContext(), R.layout.clean_cache_view, null);
								ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
								ImageView ic_clear = (ImageView) view.findViewById(R.id.ic_clear);
								TextView view_tv_name = (TextView) view.findViewById(R.id.tv_name);
								TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
								
								// 设置应用名称
								view_tv_name.setText(temInfo.getName());
								// 设置应用图标
								iv_icon.setImageDrawable(temInfo.getIcon());
								// 设置缓存大小
								tv_cache_size.setText("缓存:"+Formatter.formatFileSize(getApplicationContext(), cacheSize));
								// 设置清理的点击事件
								final View animView = view;
								ic_clear.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// 开启一个平移动画
										animView.startAnimation(mTranslateAnim);
										// 监听动画的执行
										mTranslateAnim.setAnimationListener(new AnimationListener() {
											@Override
											public void onAnimationStart(Animation animation) {
											}
											@Override
											public void onAnimationRepeat(Animation animation) {
											}
											@Override
											public void onAnimationEnd(Animation animation) {
												// 动画执行完时移除此view  并弹出吐司
												ll_addview.removeView(animView);
												ToastUtli.show(getApplicationContext(),"清理了"+Formatter.formatFileSize(getApplicationContext(), temInfo.getCacheSize())+"缓存");
											}
										});
										
										// 清理单个缓存的方法
										cleanCache(temInfo.getPackageName());
									}
								});
								
								// 将view添加到布局中
								ll_addview.addView(view, 0);
							}
						}
					});
					
					// 更新进度条
					index++;
					pb_cache.setProgress(index);
					
					// 为了让用户看到  每次循环睡一下
					try {
						// 睡的时间在30—129毫秒
						Thread.sleep(30+new Random().nextInt(100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// 循环完后更新UI
				runOnUiThread(new Runnable() {
					public void run() {
						// 清除动画
						iv_scan_top.clearAnimation();
						iv_scan_top.setVisibility(View.GONE);
						iv_scan_bottom.setVisibility(View.GONE);
						if(cacheAppNum > 0){
							iv_clean_all.setVisibility(View.VISIBLE);
							tv_name.setText("扫描完成，发现"+Formatter.formatFileSize(getApplicationContext(), totalCacherSize)+"缓存，点击清理");
							iv_clean_all.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if(ll_addview.getChildCount() > 1){
										iv_clean_all.setVisibility(View.GONE);
										iv_scan_bottom.setImageResource(R.drawable.clean_cache_bottom);
										iv_scan_top.setImageResource(R.drawable.clean_cache_top);
										iv_scan_bottom.setVisibility(View.VISIBLE);
										iv_scan_top.setVisibility(View.VISIBLE);
										iv_scan_top.startAnimation(mCleanAnimation);
										tv_name.setText("正在清理···");
										// 清理所有缓存
										cleanAllCache();
										// 移除UI
										removeView();
									}
								}
							});
						}else{
							iv_clean_all.setVisibility(View.VISIBLE);
							iv_clean_all.setImageResource(R.drawable.clean_done2);
							iv_no_clean.setVisibility(View.VISIBLE);
							tv_name.setText("扫描完成，没有发现缓存");
						}
					}
				});
			};
		}.start();
	}
	
	// 清除单个缓存
	private void cleanCache(String packageName) {
		/*// 此方法需要加一个系统权限    android.permission.DELETE_CACHE_FILES  只有root后才能加上
		// 因此   不能用此方法
		 清理单个缓存需要调用mPm的隐藏方法 所以需要反射调用 
		 * public abstract void deleteApplicationCacheFiles(String packageName,IPackageDataObserver observer);
		 * packageName要清理缓存应用的包名，observer为实现了IPackageDataObserver的子类对象
		 * IPackageDataObserver为aidl文件(Stub)
		 
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			Method method = clazz.getMethod("deleteApplicationCacheFiles", String.class,
					IPackageDataObserver.class);
			method.invoke(mPm, packageName, new IPackageDataObserver.Stub() {
				@Override
				public void onRemoveCompleted(String packageName,
						boolean succeeded) throws RemoteException {
					// 清理缓存完成后调用的方法 执行在子线程
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		/*// 开启系统的清理缓存界面
		Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setData(Uri.parse("package:"+packageName));
		startActivity(intent);*/
	}
	
	// 清理所有缓存
	private void cleanAllCache() {
	   /* 清理缓存需要调用mPm的隐藏方法    所以需要反射调用
	    * public abstract void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer);
		* freeStorageSize为申请的内存大小，当申请过大内存时(long类型的最大值)，系统就会清理缓存来凑内存，因此就间接的清理了缓存
		* IPackageDataObserver为aidl文件(Stub)
		*/
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
			method.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
				@Override
				public void onRemoveCompleted(String packageName, boolean succeeded)
						throws RemoteException {
					// 清理缓存完成后调用的方法   执行在子线程
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 清理所有缓存对应的UI操作
	private void removeView() {
		final int childCount = ll_addview.getChildCount();
		new Thread(){
			public void run() {
				for (int i = 0; i < childCount-1; i++) {
					runOnUiThread(new Runnable() {
						public void run() {
							ll_addview.removeViewAt(0);
						}
					});
					try {
						Thread.sleep(5+new Random().nextInt(10));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// 清除完成
				runOnUiThread(new Runnable() {
					public void run() {
						// 清除动画
						iv_scan_top.clearAnimation();
						iv_scan_top.setVisibility(View.GONE);
						iv_scan_bottom.setVisibility(View.GONE);
						// 显示完成图片
						iv_clean_all.setImageResource(R.drawable.clean_done2);
						iv_clean_all.setVisibility(View.VISIBLE);
						iv_no_clean.setVisibility(View.VISIBLE);
						// 设置文字
						tv_name.setText("清理完成");
					}
				});
			};
		}.start();
	}

	// 初始化控件
	private void initUI() {
		iv_clean_all = (ImageView) findViewById(R.id.iv_clean_all);
		iv_scan_bottom = (ImageView) findViewById(R.id.iv_scan_bottom);
		iv_scan_top = (ImageView) findViewById(R.id.iv_scan_top);
		iv_no_clean = (ImageView) findViewById(R.id.iv_no_clean);
		tv_name = (TextView) findViewById(R.id.tv_name);
		pb_cache = (ProgressBar) findViewById(R.id.pb_cache);
		ll_addview = (LinearLayout) findViewById(R.id.ll_addview);
		
		// 开启动画
		iv_scan_top.startAnimation(mRotateAnimation);
	}
	
	// 初始化动画
	private void initAnim() {
		mRotateAnimation = new RotateAnimation(
				0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateAnimation.setDuration(1500);
		mRotateAnimation.setFillAfter(true);
		// 设置无限循环
		mRotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
		// 设置插补器  使动画匀速执行
		mRotateAnimation.setInterpolator(new LinearInterpolator());
		
		mCleanAnimation = new RotateAnimation(
				0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mCleanAnimation.setDuration(900);
		mCleanAnimation.setFillAfter(true);
		// 设置无限循环
		mCleanAnimation.setRepeatCount(RotateAnimation.INFINITE);
		// 设置插补器  使动画中间加速执行
		mCleanAnimation.setInterpolator(new LinearInterpolator());
		
		mTranslateAnim = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0);
		mTranslateAnim.setFillAfter(true);
		mTranslateAnim.setDuration(500);
	}
}

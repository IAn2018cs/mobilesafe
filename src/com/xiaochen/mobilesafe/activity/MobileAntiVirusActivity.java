package com.xiaochen.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.engine.AntiVirusDao;
import com.xiaochen.mobilesafe.engine.AppInfoProvider;
import com.xiaochen.mobilesafe.utlis.Md5Util;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MobileAntiVirusActivity extends StatusBarColorActivity {
	private ImageView iv_bottom, iv_arc;
	private TextView tv_name;
	private ProgressBar pb_virus;
	private LinearLayout ll_addtext;
	private RotateAnimation mRotateAnimation;
	private int index = 0;
	private boolean isCycle = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mobile_antivirus);
		initAnim();
		initUI();
		// 检测病毒
		checkVirus();
	}

	// 获取手机安装应用的签名信息 然后MD5加密 再与数据库中的病毒MD5信息比对  如果数据库中包含就是病毒
	private void checkVirus() {
		// 由于是耗时操作  放在子线程
		new Thread(){
			public void run() {
				// 获取病毒数据库中的信息
				List<String> virusList = AntiVirusDao.getVirusList();
				// 创建一个存储病毒应用信息的集合
				List<InnerAppInfo> viruseAppList = new ArrayList<InnerAppInfo>();
				// 创建一个存储所有扫描应用信息的集合
				List<InnerAppInfo> allAppList = new ArrayList<InnerAppInfo>();
				// 获取包管理者对象
				PackageManager pm = getPackageManager();
				// 获得所有应用签名文件(GET_SIGNATURES:已安装应用的签名文件	GET_UNINSTALLED_PACKAGES:卸载应用的残余文件)
				List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
				// 设置进度条最大值
				pb_virus.setMax(installedPackages.size());
				// 遍历集合
				for (PackageInfo packageInfo : installedPackages) {
					// 创建javabean对象
					InnerAppInfo appInfo = new InnerAppInfo();
					// 获取应用的签名文件数组
					Signature[] signatures = packageInfo.signatures;
					// 获取签名文件数组的第一位   然后进行MD5加密  再和数据库比对
					Signature signature = signatures[0];
					// 将签名文件转换成字符串
					String charsString = signature.toCharsString();
					// 给签名文件MD5加密
					String commonMd5 = Md5Util.commonMd5(charsString);
					// 看病毒数据库中是否包含此MD5签名信息
					if(virusList.contains(commonMd5)){
						// 标记病毒应用
						appInfo.isVirus = true;
						// 添加到病毒应用集合中
						viruseAppList.add(appInfo);
					}else{
						appInfo.isVirus = false;
					}
					// 获取应用包名
					appInfo.packageName = packageInfo.packageName;
					// 获取应用名称
					appInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
					// 将javabean对象添加到扫描应用集合
					allAppList.add(appInfo);
					
					// 更新进度
					index++;
					pb_virus.setProgress(index);
					
					// 为了让用户看到  每次循环睡一下
					try {
						// 睡的时间在30—129毫秒
						Thread.sleep(30+new Random().nextInt(100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					// 更新UI
					final InnerAppInfo info = appInfo;
					runOnUiThread(new Runnable() {
						public void run() {
							tv_name.setText("正在查杀："+info.name);
							TextView textView = new TextView(getApplicationContext());
							textView.setMaxLines(1);
							if(info.isVirus){
								textView.setText("发现病毒："+info.name+"   点击卸载");
								textView.setTextColor(Color.RED);
								// 设置点击事件
								// TODO 需要改进  判断是否存在该应用
								textView.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										String packageName = info.packageName;
										Intent intent = new Intent("android.intent.action.DELETE");
										intent.addCategory("android.intent.category.DEFAULT");
										intent.setData(Uri.parse("package:"+packageName));
										startActivity(intent);
									}
								});
							}else{
								textView.setText("扫描安全："+info.name);
								textView.setTextColor(Color.BLACK);
							}
							ll_addtext.addView(textView, 0);
						}
					});
				}
				
				// 循环结束  更新UI
				final List<InnerAppInfo> temVirusAppList = viruseAppList;
				runOnUiThread(new Runnable() {
					public void run() {
						// 清除动画
						isCycle = false;
						iv_arc.clearAnimation();
						iv_arc.setVisibility(View.GONE);
						// 存在病毒
						if(temVirusAppList.size()>0){
							tv_name.setText("扫描完成，发现"+temVirusAppList.size()+"个病毒应用，点击卸载");
							tv_name.setTextColor(Color.RED);
							iv_bottom.setImageResource(R.drawable.app_affected);
							// TODO 需要改进  判断是否存在该应用
							tv_name.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									// 卸载应用
									for (InnerAppInfo innerAppInfo : temVirusAppList) {
										String packageName = innerAppInfo.packageName;
										Intent intent = new Intent("android.intent.action.DELETE");
										intent.addCategory("android.intent.category.DEFAULT");
										intent.setData(Uri.parse("package:"+packageName));
										startActivity(intent);
									}
								}
							});
						}else{
							tv_name.setText("扫描完成，没有发现病毒应用");
							iv_bottom.setImageResource(R.drawable.no_app_affect);
						}
					}
				});
				
			};
		}.start();
	}
	
	// 创建一个应用信息的javabean
	class InnerAppInfo{
		boolean isVirus;
		String packageName;
		String name;
	}

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
		
	}

	private void initUI() {
		iv_bottom = (ImageView) findViewById(R.id.iv_bottom);
		iv_arc = (ImageView) findViewById(R.id.iv_arc);
		tv_name = (TextView) findViewById(R.id.tv_name);
		pb_virus = (ProgressBar) findViewById(R.id.pb_virus);
		ll_addtext = (LinearLayout) findViewById(R.id.ll_addtext);
		
		iv_arc.startAnimation(mRotateAnimation);
		/*mRotateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				if(isCycle){
					iv_arc.startAnimation(mRotateAnimation);
				}
			}
		});*/
	}
}

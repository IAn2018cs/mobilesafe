package com.xiaochen.mobilesafe.engine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.xiaochen.mobilesafe.db.domain.AppInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;

public class AppInfoProvider {
	private static PackageManager mPm;
	private static long cacheSize = 0;

	/**获取所有应用信息
	 * @param context  上下文环境
	 * @return  包含应用信息集合 
	 */
	public static List<AppInfo> getAppInfo(Context context){
		// 获取包管理者
		mPm = context.getPackageManager();
		// 获取所有应用信息
		List<ApplicationInfo> installedApplications = mPm.getInstalledApplications(0);
		// 创建存储javabean的集合
		List<AppInfo> list = new ArrayList<AppInfo>();
		for (ApplicationInfo applicationInfo : installedApplications) {
			// 创建javabean对象
			AppInfo appInfo = new AppInfo();

			// 获取应用包名
			String packageName = applicationInfo.packageName;
			// 获取应用名称
			String name = applicationInfo.loadLabel(mPm).toString();
			// 获取应用图标
			Drawable loadIcon = applicationInfo.loadIcon(mPm);
			// 获取应用缓存大小
			long size = getCacheSize(packageName);
			// 获取应用标记
			int flags = applicationInfo.flags;
			
			// 为javabean对象赋值
			if((flags&ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
				appInfo.setSystem(true);
			}else{
				appInfo.setSystem(false);
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				appInfo.setSdCard(true);
			}else{
				appInfo.setSdCard(false);
			}
			appInfo.setIcon(loadIcon);
			appInfo.setName(name);
			appInfo.setPackageName(packageName);
			appInfo.setCacheSize(size);
			// 将javabean对象添加到集合中
			list.add(appInfo);

			/*// dataDir:数据的位置
			String dataDir = applicationInfo.dataDir;
		    // dataDir:/data/data/com.android.soundrecorder
			// sourceDir:应用apk位置(区分系统应用和用户应用)
			String sourceDir = applicationInfo.sourceDir;
			// sourceDir:/system/app/SoundRecorder.apk
		    // sourceDir:/data/app/com.xiaochen.mobilesafe-2.apk
			System.out.println("应用名称:"+name+"\n"+"包名:"+packageName+"\n"+"dataDir:"+dataDir+"\n"+"sourceDir:"+sourceDir+"\n"+"flags:"+flags);
			System.out.println("-------------------------");*/
		}
		/*System.out.println("下一种方法啦*******************************");
		List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			AppInfo appInfo = new AppInfo();
			
			String packageName = packageInfo.packageName;  //包名
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;  //应用信息
			Drawable icon = applicationInfo.loadIcon(packageManager);  //应用图标
			String loadLabel = applicationInfo.loadLabel(packageManager).toString();  //应用名称
			
			appInfo.setIcon(icon);
			appInfo.setName(loadLabel);
			appInfo.setPackageName(packageName);
			list.add(appInfo);
			
			int icon2 = applicationInfo.icon;
			System.out.println("packageName:"+packageName+"\n"+"icon:"+icon2+"\n"+"应用名称:"+loadLabel);
			System.out.println("-------------------------------------");
		}*/
		
		return list;
	}
	
	/**获取应用缓存大小(需要加权限) 从设置的源码中拷贝出来
	 * @param packageName  获取应用缓存的包名
	 * @return 返回long类型的缓存大小   单位byte
	 */
	public static long getCacheSize(String packageName){
		// 创建了一个IPackageStatsObserver.Stub子类的对象,并且实现了onGetStatsCompleted方法
		IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
			public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
				// 缓存大小的过程,子线程中代码,不能去处理UI
				cacheSize = stats.cacheSize;
			}
		};
		
		/* 要获取应用的缓存大小  需要调用mPm的一个隐藏方法  所以用反射去调用
		 * 参数分别为:应用包名,IPackageStatsObserver的子类对象
		 * mPm.getPackageSizeInfo("com.android.browser", mStatsObserver);
		 */
		try {
			// 获取要使用的类  参数为:类的全路径名
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			// 获取类中的方法  参数为:方法名,参数类型
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
			// 调用这个方法   参数为:调用者对象,具体参数
			method.invoke(mPm, packageName,mStatsObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cacheSize;
	}
	
	/**获取所有安装应用的包名
	 * @param context 上下文环境
	 * @return  返回String集合
	 */
	public static List<String> getAppPackageNameList(Context context){
		// 获取包管理者
		PackageManager packageManager = context.getPackageManager();
		// 获取所有应用信息
		List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
		// 创建存储集合
		List<String> list = new ArrayList<String>();
		for (ApplicationInfo applicationInfo : installedApplications) {
			// 获取应用包名
			list.add(applicationInfo.packageName);
		}
		
		return list;
	}
}

package com.xiaochen.mobilesafe.utlis;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {
	public static boolean isRunning(Context context ,String serviceName){
		// 获取ActivityManager对象
		ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取运行中的服务  (参数为最多获取多少个)
		List<RunningServiceInfo> runningServices = mAM.getRunningServices(100);
		// 遍历服务集合  获取每个服务的类的名称 和传递进来的服务对比  如果一样就返回true
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			if(serviceName.equals(runningServiceInfo.service.getClassName())){
				return true;
			}
		}
		return false;
	}
}

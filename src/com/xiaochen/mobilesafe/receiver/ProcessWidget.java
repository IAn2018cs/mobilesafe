package com.xiaochen.mobilesafe.receiver;

import com.xiaochen.mobilesafe.service.ProcessWidgetService;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ProcessWidget extends AppWidgetProvider {
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
	
	// 创建第一个窗体小部件
	@Override
	public void onEnabled(Context context) {
		// 开启服务
		context.startService(new Intent(context, ProcessWidgetService.class));
		super.onEnabled(context);
	}
	
	// 添加一个窗体小部件
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// 开启服务
		context.startService(new Intent(context, ProcessWidgetService.class));
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	// 窗体小部件宽高发生改变时
	@SuppressLint("NewApi")
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// 开启服务
		context.startService(new Intent(context, ProcessWidgetService.class));
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}
	
	// 删除窗体小部件
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}
	
	// 删除最后一个窗体小部件
	@Override
	public void onDisabled(Context context) {
		// 关闭服务
		context.stopService(new Intent(context, ProcessWidgetService.class));
		super.onDisabled(context);
	}
}

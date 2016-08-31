package com.xiaochen.mobilesafe.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import com.thinkland.sdk.android.JuheSDKInitializer;

import android.app.Application;
import android.os.Environment;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化聚合数据
		JuheSDKInitializer.initialize(getApplicationContext());
		
		// 捕获全局未捕获的异常
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				// 输出异常
				ex.printStackTrace();
				
				// 将打印的异常存到SD卡中
				String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"ian_mobilesafe_error.log";
				File file = new File(path);
				try {
					PrintWriter printWriter = new PrintWriter(file);
					ex.printStackTrace(printWriter);
					printWriter.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				// 可以将异常文件上传到服务器
				
				// 手动退出应用
				System.exit(0);
			}
		});
	}
}

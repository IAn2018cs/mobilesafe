package com.xiaochen.mobilesafe.receiver;

import com.xiaochen.mobilesafe.engine.ProcessInfoProvider;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;

public class KillAllProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 杀死所有进程
		ProcessInfoProvider.killAllProcess(context);
		// 计算清理了多少内存
		long startMem = intent.getLongExtra("memory", 0);
		long endMem = ProcessInfoProvider.getAvailable(context);
		String strMen = Formatter.formatFileSize(context, endMem-startMem);
		// 计算杀死了多少进程
		int starCount = intent.getIntExtra("count", 0);
		int endCount = ProcessInfoProvider.getProcessCount(context);
		int count = starCount - endCount;
		if(count > 0){
			// 弹出吐司
			ToastUtli.show(context, "清理了"+count+"个进程，释放了"+strMen+"内存");
		}else{
			ToastUtli.show(context, "现在很干净，没有可清理的进程");
		}
		
	}

}

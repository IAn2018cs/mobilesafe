package com.xiaochen.mobilesafe.utlis;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.View;

import com.xiaochen.mobilesafe.R;

public class ShowDialog {
	private static AlertDialog dialog;

	public static void showDialog(Context mthis) {
		// 自定义对话框 要用dialog.setView(view); 来设置
		Builder builder = new AlertDialog.Builder(mthis);
		dialog = builder.create();

		// 将一个xml转换成一个view对象
		View view = View.inflate(mthis, R.layout.dialog_progress, null);
		// 兼容低版本 去掉对话框的内边距
		dialog.setView(view, 0, 0, 0, 0);
		// dialog.setView(view);
		dialog.show();
		
	}
	
	public static void dismiss(){
		dialog.dismiss();
	}
}

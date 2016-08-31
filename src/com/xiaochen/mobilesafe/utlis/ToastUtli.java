package com.xiaochen.mobilesafe.utlis;

import android.content.Context;
import android.widget.Toast;

public class ToastUtli {
	public static void show(Context context, String text){
		Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show();
	}
}

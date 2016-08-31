package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InFoActivity extends StatusBarColorActivity {
	private long[] mHit2 = new long[4];
	private long[] mHit3 = new long[8];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		TextView text_V = (TextView) findViewById(R.id.text_V);
		// 拿到包管理者
		PackageManager pm = getPackageManager();
		// 获取包的信息(Info)
		try {
			// flags：为0是获取基本信息
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			text_V.setText("版本号："+info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		// 设置点击事件
		RelativeLayout rl_click = (RelativeLayout) findViewById(R.id.rl_click);
		rl_click.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 四击事件  开启开发者模式
				System.arraycopy(mHit2, 1, mHit2, 0, mHit2.length-1);
				mHit2[mHit2.length-1] = SystemClock.uptimeMillis();
				if(mHit2[mHit2.length-1]-mHit2[0] < 1000){
					SpUtils.putBoolSp(getApplicationContext(), ConstantValue.OPEN_DEVELOPER, true);
					ToastUtli.show(getApplicationContext(), "您已开启开发者模式");
				}
				// 八击事件  关闭开发者模式
				System.arraycopy(mHit3, 1, mHit3, 0, mHit3.length-1);
				mHit3[mHit3.length-1] = SystemClock.uptimeMillis();
				if(mHit3[mHit3.length-1]-mHit3[0] < 2000){
					SpUtils.putBoolSp(getApplicationContext(), ConstantValue.OPEN_DEVELOPER, false);
					ToastUtli.show(getApplicationContext(), "您已关闭开发者模式");
				}
			}
		});
	}
}

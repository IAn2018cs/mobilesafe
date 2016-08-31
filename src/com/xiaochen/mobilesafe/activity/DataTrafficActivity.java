package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.net.TrafficStats;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DataTrafficActivity extends StatusBarColorActivity {
	private TextView tv_mobile_s, tv_mobile_r, tv_wifi_s, tv_wifi_r;
	private ImageView iv_liantong, iv_yidong, iv_dianxin;
	private String strMobileRx, strMobileSx, strWifiRx, strWifiSx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datatraffic);
		
		initData();
		initUI();
		initClick();
	}
	
	private void initClick() {
		// 移动的点击事件
		iv_yidong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//获取短信管理者  并发送短信
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage("10086", null, "流量查询", null, null);
				ToastUtli.show(getApplicationContext(), "已发送短信，请注意查收");
			}
		});
		// 联通的点击事件
		iv_liantong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//获取短信管理者  并发送短信
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage("10010", null, "907", null, null);
				ToastUtli.show(getApplicationContext(), "已发送短信，请注意查收");
			}
		});
		// 电信的点击事件
		iv_dianxin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//获取短信管理者  并发送短信
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage("10001", null, "108", null, null);
				ToastUtli.show(getApplicationContext(), "已发送短信，请注意查收");
			}
		});
	}

	private void initData() {
		// 获取手机下载的流量
		long mobileRxBytes = TrafficStats.getMobileRxBytes();
		// 获取手机下载和上传的总流量
		long mobileTxBytes = TrafficStats.getMobileTxBytes();
		// 手机上传的流量
		long mobileSxBytes = mobileTxBytes - mobileRxBytes;

		// 获取手机和wifi的总下载流量
		long totalRxBytes = TrafficStats.getTotalRxBytes();
		// 获取手机和wifi的下载和上传的总流量
		long totalTxBytes = TrafficStats.getTotalTxBytes();
		// wifi下载的流量
		long wifiRxBytes = totalRxBytes - mobileRxBytes;
		// wifi上传的流量
		long wifiSxBytes = totalTxBytes - mobileTxBytes - wifiRxBytes;
		
		// 将byte转换成存储类型字符串
		// 手机下载的流量
		strMobileRx = Formatter.formatFileSize(this, mobileRxBytes);
		// 手机上传的流量
		strMobileSx = Formatter.formatFileSize(this, mobileSxBytes);
		// wifi下载的流量
		strWifiRx = Formatter.formatFileSize(this, wifiRxBytes);
		// wifi上传的流量
		strWifiSx = Formatter.formatFileSize(this, wifiSxBytes);
	}

	private void initUI() {
		tv_mobile_s = (TextView) findViewById(R.id.tv_mobile_s);
		tv_mobile_r = (TextView) findViewById(R.id.tv_mobile_r);
		tv_wifi_s = (TextView) findViewById(R.id.tv_wifi_s);
		tv_wifi_r = (TextView) findViewById(R.id.tv_wifi_r);
		
		// 填充文字
		tv_mobile_s.setText("手机上传流量：\n	"+strMobileSx);
		tv_mobile_r.setText("手机下载流量：\n	"+strMobileRx);
		tv_wifi_s.setText("wifi上传流量：\n	"+strWifiSx);
		tv_wifi_r.setText("wifi下载流量：\n	"+strWifiRx);
		
		iv_liantong = (ImageView) findViewById(R.id.iv_liantong);
		iv_yidong = (ImageView) findViewById(R.id.iv_yidong);
		iv_dianxin = (ImageView) findViewById(R.id.iv_dianxin);
	}
}
